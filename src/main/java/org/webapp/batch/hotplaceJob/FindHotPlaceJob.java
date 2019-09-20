package org.webapp.batch.hotplaceJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.webapp.batch.BatchSettings;
import org.webapp.model.Instahot;
import org.webapp.model.Instaranking;
import org.webapp.model.Overall;
import org.webapp.model.Youtubehot;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


@Configuration(value = "GetHotPlaceJobBean")
@EnableBatchProcessing
public class FindHotPlaceJob {
    private static final String JOB_NAME = "findHotPlace-Job";
    private static final String SETTING_STEP_NAME = "resetInstaranking-Step";
    private static final String FIRST_STEP_NAME = "setupPlaceRanking-Step";
    private static final String SECOND_STEP_NAME = "setupInstaHotPlace-Step";
    private static final String THIRD_STEP_NAME = "setupYoutubeHotPlace-Step";
    private static final int CHUNK_SIZE = 1;
    private static final Logger logger = LoggerFactory.getLogger(FindHotPlaceJob.class);

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;
    private DataSourceTransactionManager transactionManager;
    private JdbcTemplate jdbcTemplate;

    FindHotPlaceJob() {}

    @Autowired
    public FindHotPlaceJob(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
                           DataSource dataSource,
                           DataSourceTransactionManager transactionManager) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

//    @Bean
//    public TaskExecutor asyncTaskExecutor() {
//        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
//        asyncTaskExecutor.setConcurrencyLimit(10);
//        return asyncTaskExecutor;
//    }

    @Bean
    public CronTriggerFactoryBean findHotPlaceTrigger() {
        return BatchSettings.cronTriggerFactoryBeanBuilder()
                .name("FindHotPlace-Trigger")
                .cronExpression("0 0/2 * * * ?") // 1~2분마다
                //.cronExpression("0 30 6 * * ? *") //매일 오전 6시 30분에
                .jobDetailFactoryBean(findHotPlaceJobSchedule())
                .build();
    }

    @Bean
    public JobDetailFactoryBean findHotPlaceJobSchedule() {
        return BatchSettings.jobDetailFactoryBeanBuilder()
                .job(findHotPlaceJob())
                .build();
    }

    @Bean
    public Job findHotPlaceJob() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(resetInstarankingStep())
                .next(setupPlaceRankingStep())
                .next(setupInstaHotPlaceStep())
                .next(setupYoutubeHotPlaceStep())
                .build();
    }

    @Bean
    @JobScope
    public Step resetInstarankingStep() {
        return stepBuilderFactory.get(SETTING_STEP_NAME)
                .tasklet(new Tasklet() {

                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        logger.info("[FindHotPlaceJob] : ResetInstaranking-Tasklet started.");
                        String sql = "DELETE FROM instaranking";
                        jdbcTemplate.update(sql);

                        return RepeatStatus.FINISHED;
                    }
                }).build();
    }

    @Bean
    @JobScope
    public Step setupPlaceRankingStep() {
        return stepBuilderFactory.get(FIRST_STEP_NAME)
                .<Overall, List<Instaranking>>chunk(CHUNK_SIZE)
                .reader(setupPlaceRankingReader())
                .processor(setupPlaceRankingProcessor())
                .writer(setupPlaceRankingWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    public JdbcCursorItemReader<Overall> setupPlaceRankingReader() {
        logger.info("[FindHotPlaceJob] : SetupPlaceRanking-ItemReader started.");

        return new JdbcCursorItemReaderBuilder<Overall>()
                .sql("SELECT STATION FROM OVERALL")
                .rowMapper(new BeanPropertyRowMapper<>(Overall.class))
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .name("instahot-ItemReader")
                .build();
    }


    @Bean
    public ItemProcessor<Overall, List<Instaranking>> setupPlaceRankingProcessor() {
        return new SetupPlaceRankingProcessor();
    }

    @Bean
    public ItemWriter<List<Instaranking>> setupPlaceRankingWriter() {
        return new SetupPlaceRankingWriter();
    }

    @Bean
    @JobScope
    public Step setupInstaHotPlaceStep() {
        return stepBuilderFactory.get(SECOND_STEP_NAME)
                .<Map.Entry<String, String>, List<Instahot>>chunk(CHUNK_SIZE)
                .reader(setupInstaHotPlaceReader())
                .processor(setupInstaHotPlaceProcessor())
                .writer(setupInstaHotPlaceWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    public SetupInstaHotPlaceReader setupInstaHotPlaceReader() {
        return new SetupInstaHotPlaceReader();
    }

    @Bean
    public ItemProcessor<Map.Entry<String, String>, List<Instahot>> setupInstaHotPlaceProcessor() {
        return new SetupInstaHotPlaceProcessor();
    }

    @Bean
    public ItemWriter<List<Instahot>> setupInstaHotPlaceWriter() {
        return new SetupInstaHotPlaceWriter();
    }

    @Bean
    @JobScope
    public Step setupYoutubeHotPlaceStep() {
        return stepBuilderFactory.get(THIRD_STEP_NAME)
                .<Map.Entry<String, String>, List<Youtubehot>>chunk(CHUNK_SIZE)
                .reader(setupInstaHotPlaceReader())
                .processor(setupYoutubeHotPlaceProcessor())
                .writer(setupYoutubeHotPlaceWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean
    public ItemProcessor<Map.Entry<String, String>, List<Youtubehot>> setupYoutubeHotPlaceProcessor() {
        return new SetupYoutubeHotPlaceProcessor();
    }

    @Bean
    public ItemWriter<List<Youtubehot>> setupYoutubeHotPlaceWriter() {
        return new SetupYoutubeHotPlaceWriter();
    }

    // 배치 비동기 처리 생각해보기 : ItemProcessor, ItemWriter -> 어떤 작업들에 비동기 걸어야 이득인지?
//    @Bean
//    public ItemProcessor<Map.Entry<String, String>, Future<List<Instahot>>> asyncInstaHotPlaceProcessor() {
//        AsyncItemProcessor<Map.Entry<String, String>, List<Instahot>> itemProcessor
//                = new AsyncItemProcessor<>();
//        itemProcessor.setDelegate(setupInstaHotPlaceProcessor());
//        itemProcessor.setTaskExecutor(asyncTaskExecutor());
//        return itemProcessor;
//    }
//
//    @Bean
//    public ItemProcessor<Map.Entry<String, String>, List<Instahot>> setupInstaHotPlaceProcessor() {
//        return new SetupInstaHotPlaceProcessor();
//    }
//
//    @Bean
//    public ItemWriter<Future<List<Instahot>>> asynInstaHotPlaceWriter() {
//        AsyncItemWriter<Future<List<Instahot>>> itemWriter
//                = new AsyncItemWriter<>();
//        itemWriter.setDelegate(setupInstaHotPlaceWriter());
//        return itemWriter;
//    }

}
