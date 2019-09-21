package org.webapp.batch.locationJob;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.jsr.configuration.xml.JobFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.webapp.batch.BatchSettings;
import org.webapp.model.Instafood;
import org.webapp.model.Instaplace;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class SearchLocationJob {

    private static final Logger logger = LoggerFactory.getLogger(SearchLocationJob.class);
    private static final String JOB_NAME = "searchLocation-Job";
    private static final String CHECK_DB_STEP_NAME = "getLatestCrawlDate-Step";
    private static final String LOCATION_STEP_NAME = "setupInstaLocation-Step";
    private static final String FOOD_STEP_NAME = "setupRestaurantsInLocation-Step";
    private static final int CHUNK_SIZE = 1;

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;
    private DataSourceTransactionManager transactionManager;
    private LocationStepsDataShareBean dataShareBean;

    SearchLocationJob() {}

    @Autowired
    public SearchLocationJob(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             DataSource dataSource,
                             DataSourceTransactionManager transactionManager,
                             LocationStepsDataShareBean dataShareBean) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.dataShareBean = dataShareBean;
    }

    @Bean
    public CronTriggerFactoryBean searchLocationTrigger() {
        return BatchSettings.cronTriggerFactoryBeanBuilder()
                .name("SearchLocation-Trigger")
                .cronExpression("0 30 3 * * ? *")
                //.cronExpression("0 0/2 * * * ?") // 1~2분마다
                .jobDetailFactoryBean(searchLocationJobSchedule())
                .build();
    }

    @Bean
    public JobDetailFactoryBean searchLocationJobSchedule() {
        return BatchSettings.jobDetailFactoryBeanBuilder()
                .job(searchLocation())
                .build();
    }

    @Bean
    public Job searchLocation() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(getLatestCrawlDateStep())
                .next(setupInstaLocationStep())
                .next(setupRestaurantsInLocationStep())
                .build();
    }

    @Bean
    @JobScope
    public Step getLatestCrawlDateStep() {
        return stepBuilderFactory.get(CHECK_DB_STEP_NAME)
                .tasklet(new GetLatestCrawlDateTasklet(this.dataSource, this.dataShareBean))
                .build();
    }

    @Bean
    @JobScope
    public Step setupInstaLocationStep() {
        return stepBuilderFactory.get(LOCATION_STEP_NAME)
                .<Overall, List<Instaplace>>chunk(CHUNK_SIZE)
                .reader(setupInstaLocationReader())
                .processor(setupInstaLocationProcessor())
                .writer(setupInstaLocationWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    @StepScope
    public JdbcCursorItemReader<Overall> setupInstaLocationReader() {
        logger.info("[SearchLocationJob] : SetupInstaPlace-ItemReader started.");

        return new JdbcCursorItemReaderBuilder<Overall>()
                .sql("SELECT station, restaurants FROM overall")
                .rowMapper(new BeanPropertyRowMapper<>(Overall.class))
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .name("instaplace-ItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Overall, List<Instaplace>> setupInstaLocationProcessor() {
        return new SetupInstaLocationProcessor();
    }

    @Bean
    public ItemWriter<List<Instaplace>> setupInstaLocationWriter() {
        return new SetupInstaLocationWriter();
    }

    @Bean
    @JobScope
    public Step setupRestaurantsInLocationStep() {
        return stepBuilderFactory.get(FOOD_STEP_NAME)
                .<Instafood, Instafood>chunk(30)
                .reader(setupRestaurantsInLocationReader())
                .processor(setupRestaurantsInLocationProcessor())
                .writer(setupRestaurantsInLocationWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    public SetupRestaurantsInLocationReader setupRestaurantsInLocationReader() {
        return new SetupRestaurantsInLocationReader();
    }

    @Bean
    public ItemProcessor<Instafood, Instafood> setupRestaurantsInLocationProcessor() {
        return new SetupRestaurantsInLocationProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Instafood> setupRestaurantsInLocationWriter() {
        String sql = "INSERT INTO instafood(station, post, date, likeCNT, myRestaurant, photoURL) " +
                        "VALUES(:station, :post, :date, :likeCNT, :myRestaurant, :photoURL)";

        return new JdbcBatchItemWriterBuilder<Instafood>()
                .dataSource(this.dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

}
