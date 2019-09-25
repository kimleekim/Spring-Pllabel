package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.webapp.batch.BatchSettings;
import org.webapp.model.Instafood;
import org.webapp.model.Overall;
import org.webapp.model.Youtubefood;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class FindHotFoodJob {

    private static final Logger logger  = LoggerFactory.getLogger(FindHotFoodJob.class);
    private static final String JOB_NAME = "findHotFood-Job";
    private static final String READ_STEP_NAME = "getLatestFoodDate-Step";
    private static final String INSTAGRAM_STEP_NAME = "getInstaFoodPosts-Step";
    private static final String COMPUTING_STEP_NAME = "mappingTop10Restaurants-Step";
    private static final String YOUTUBE_STEP_NAME = "setupYoutubeHotFood-Step";
    private static final int CHUNK_SIZE = 1;

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;
    private DataSourceTransactionManager transactionManager;
    private FoodStepsDataShareBean dataShareBean;

    FindHotFoodJob() {}

    @Autowired
    public FindHotFoodJob(JobBuilderFactory jobBuilderFactory,
                          StepBuilderFactory stepBuilderFactory,
                          DataSource dataSource,
                          DataSourceTransactionManager transactionManager,
                          FoodStepsDataShareBean dataShareBean) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.dataShareBean = dataShareBean;
    }

    @Bean
    public CronTriggerFactoryBean findHotFoodTrigger() throws Exception {
        return BatchSettings.cronTriggerFactoryBeanBuilder()
                .name("FindHotFood-Trigger")
                //.cronExpression("0 0/2 * * * ?") for test
                .cronExpression("0 30 7 * * ? *")
                .jobDetailFactoryBean(findHotFoodJobSchedule())
                .build();
    }

    @Bean
    public JobDetailFactoryBean findHotFoodJobSchedule() throws Exception {
        return BatchSettings.jobDetailFactoryBeanBuilder()
                .job(findHotFood())
                .build();
    }

    @Bean
    public Job findHotFood() throws Exception {
        return jobBuilderFactory.get(JOB_NAME)
                .start(getLatestFoodDateStep())
                .next(getInstaFoodPostsStep())
                .next(mappingTop10RestaurantsStep())
                .next(setupYoutubeHotFoodStep())
                .build();
    }

    @Bean
    @JobScope
    public Step getLatestFoodDateStep() {
        return stepBuilderFactory.get(READ_STEP_NAME)
                .tasklet(new GetLatestFoodDateTasklet(this.dataSource, this.dataShareBean))
                .build();
    }

    @Bean
    @JobScope
    public Step getInstaFoodPostsStep() throws Exception {
        return stepBuilderFactory.get(INSTAGRAM_STEP_NAME)
                .<Overall, List<Instafood>>chunk(CHUNK_SIZE)
                .reader(getInstaFoodPostsReader())
                .processor(getInstaFoodPostsProcessor())
                .writer(getInstaFoodPostsWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    @StepScope
    public JdbcPagingItemReader<Overall> getInstaFoodPostsReader() throws Exception {
        logger.info("[FindHotFoodJob] : SetupInstaFoodPosts-ItemReader started.");

        return new JdbcPagingItemReaderBuilder<Overall>()
                .pageSize(CHUNK_SIZE)
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Overall.class))
                .queryProvider(foodQueryProvider())
                .name("getInstaFoodPosts-Reader")
                .build();
    }

    @Bean
    public PagingQueryProvider foodQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProviderFactoryBean
                = new SqlPagingQueryProviderFactoryBean();
        queryProviderFactoryBean.setDataSource(this.dataSource);
        queryProviderFactoryBean.setSelectClause("station, restaurants");
        queryProviderFactoryBean.setFromClause("from overall");

        Map<String, Order> orderBy = Collections.singletonMap("station", Order.ASCENDING);
        queryProviderFactoryBean.setSortKeys(orderBy);

        return queryProviderFactoryBean.getObject();
    }

    @Bean
    public ItemProcessor<Overall, List<Instafood>> getInstaFoodPostsProcessor() {
        return new GetInstaFoodPostsProcessor();
    }

    @Bean
    public ItemWriter<List<Instafood>> getInstaFoodPostsWriter() {
        return new GetInstaFoodPostsWriter();
    }

    @Bean
    @JobScope
    public Step mappingTop10RestaurantsStep() {
        return stepBuilderFactory.get(COMPUTING_STEP_NAME)
                .<String, Map<String, List<String>>>chunk(CHUNK_SIZE)
                .reader(mappingTop10RestaurantsReader())
                .processor(mappingTop10RestaurantsProcessor())
                .writer(mappingTop10RestaurantsWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    @StepScope
    public ItemReader<String> mappingTop10RestaurantsReader() {
        return new MappingTop10RestaurantsReader();
    }

    @Bean
    public ItemProcessor<String, Map<String, List<String>>> mappingTop10RestaurantsProcessor() {
        return new MappingTop10RestaurantsProcessor();
    }

    @Bean
    public ItemWriter<Map<String, List<String>>> mappingTop10RestaurantsWriter() {
        return new MappingTop10RestaurantsWriter();
    }

    @Bean
    @JobScope
    public Step setupYoutubeHotFoodStep() {
        return stepBuilderFactory.get(YOUTUBE_STEP_NAME)
                .<Map.Entry<String, List<String>>, List<Youtubefood>>chunk(CHUNK_SIZE)
                .reader(setupYoutubeHotFoodReader())
                .processor(setupYoutubeHotFoodProcessor())
                .writer(setupYoutubeHotFoodWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    public SetupYoutubeHotFoodReader setupYoutubeHotFoodReader() {
        return new SetupYoutubeHotFoodReader();
    }

    @Bean
    public ItemProcessor<Map.Entry<String, List<String>>, List<Youtubefood>> setupYoutubeHotFoodProcessor() {
        return new SetupYoutubeHotFoodProcessor();
    }

    @Bean
    public ItemWriter<List<Youtubefood>> setupYoutubeHotFoodWriter() {
        return new SetupYoutubeHotFoodWriter();
    }
}
