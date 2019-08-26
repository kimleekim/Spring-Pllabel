package org.webapp.config;

import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

@Configuration
@PropertySource("classpath:quartz.properties")
@EnableBatchProcessing
public class ScheduleConfiguration {
    private DataSourceContext dataSourceContext;

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ScheduleConfiguration.class);

    @Autowired
    public ScheduleConfiguration(DataSourceContext dataSourceContext) {
        this.dataSourceContext = dataSourceContext;
    }

    @Bean
    public SpringBeanJobFactory jobFactory(AutowireCapableBeanFactory beanFactory) {
        return new SpringBeanJobFactory() {
            @Override
            protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
                Object job = super.createJobInstance(bundle);
                beanFactory.autowireBean(job);
                return job;
            }
        };
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory,
                                                     Trigger[] registryTrigger) throws Exception {
        DataSource dataSource;
        dataSource = this.dataSourceContext.dataSourceForBatchJobs();
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        schedulerFactoryBean.setSchedulerName("Setting-Crawling-Data");
        schedulerFactoryBean.setJobFactory(jobFactory);
        schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true);
        schedulerFactoryBean.setOverwriteExistingJobs(true);
        schedulerFactoryBean.setQuartzProperties(quartzProperties());
        schedulerFactoryBean.setDataSource(dataSource);
        schedulerFactoryBean.setTriggers(registryTrigger);

        return schedulerFactoryBean;
    }

    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));

        Properties properties = null;

        try {
            propertiesFactoryBean.afterPropertiesSet();
            properties = propertiesFactoryBean.getObject();
        } catch(Exception e) {
            logger.warn("Cannot load quartz.properties");
        }

        return properties;
    }

    @Bean
    public Trigger[] registryTrigger(List<CronTriggerFactoryBean> cronTriggerFactoryBeanList) {
        return cronTriggerFactoryBeanList.stream().map(CronTriggerFactoryBean::getObject).toArray(Trigger[]::new);
    }

    @Bean
    public SmartLifecycle gracefulShutdownHookForQuartz(SchedulerFactoryBean schedulerFactoryBean) {
        return new SmartLifecycle() {
            private boolean isRunning = false;
            private final Logger logger = LoggerFactory.getLogger(this.getClass());
            @Override
            public boolean isAutoStartup() {
                return true;
            }

            @Override
            public void start() {
                logger.info("Quartz Graceful Shutdown Hook started.");
                isRunning = true;
            }

            @Override
            public void stop(Runnable callback) {
                stop();
                logger.info("Shut down spring container");
                callback.run();
            }

            @Override
            public void stop() {
                isRunning = false;
                try {
                    logger.info("Quartz Graceful Shutdown... ");
                    schedulerFactoryBean.destroy();
                } catch (SchedulerException e) {
                    try {
                        logger.info(
                                "Error shutting down Quartz: " + e.getMessage(), e);
                        schedulerFactoryBean.getScheduler().shutdown(false);
                    } catch (SchedulerException ex) {
                        logger.error("Unable to shutdown the Quartz scheduler.", ex);
                    }
                }
            }

            @Override
            public boolean isRunning() {
                return isRunning;
            }

            @Override
            public int getPhase() {
                return Integer.MAX_VALUE;
            }
        };
    }
}
