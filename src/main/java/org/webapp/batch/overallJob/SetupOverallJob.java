package org.webapp.batch.overallJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.webapp.batch.BatchSettings;
import org.webapp.model.Overall;
import org.springframework.classify.*;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Map;


@Configuration
@EnableBatchProcessing
public class SetupOverallJob {
    private static final String JOB_NAME = "setupOverallJob";
    private static final String FIRST_STEP_NAME = "setupOverallStep";
    private static final int CHUNK_SIZE = 15;
    private static final Logger logger = LoggerFactory.getLogger(SetupOverallJob.class);

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSource dataSource;
    private DataSourceTransactionManager transactionManager;
    private OverallStepsDataShareBean<Overall> dataShareBean;


    @Autowired
    public SetupOverallJob(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
                           DataSource dataSource,
                           DataSourceTransactionManager transactionManager,
                           OverallStepsDataShareBean<Overall> dataShareBean) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.dataShareBean = dataShareBean;
    }

    @Bean
    public CronTriggerFactoryBean setupOverallTrigger() {
        return BatchSettings.cronTriggerFactoryBeanBuilder()
                .cronExpression("0 30 9 L * ?") // 매월 말일 오전 9시 30분에
                .jobDetailFactoryBean(setupOverallJobSchedule())
                .build();
    }

    @Bean
    public JobDetailFactoryBean setupOverallJobSchedule() {
        return BatchSettings.jobDetailFactoryBeanBuilder()
                .job(setupOverall())
                .build();
    }

    @Bean
    public Job setupOverall() {
        return jobBuilderFactory.get(JOB_NAME)
                .start(setupOverallStep())
                //.next(setupRestaurantsSteps())
                .build();
    }

    @Bean
    @JobScope
    public Step setupOverallStep() {
        return stepBuilderFactory.get(FIRST_STEP_NAME)
                .<Overall, Overall>chunk(CHUNK_SIZE)
                .reader(setupOverallReader())
                .processor(setupOverallProcessor())
                .writer(classifierCompositeItemWriter())
                //.stream((ItemStream) setupRemovedRestaurantsWriter())
                //.stream((ItemStream) setupNewRestaurantsWriter())
                .transactionManager(this.transactionManager)
                .build();
    }

    @Bean(destroyMethod = "")
    @StepScope
    public JdbcCursorItemReader<Overall> setupOverallReader() {
        logger.info("[SetupOverallJob] : ItemReader started.");
        return new JdbcCursorItemReaderBuilder<Overall>()
                .sql("SELECT STATION, RESTAURANTS FROM OVERALL")
                .rowMapper(new BeanPropertyRowMapper<>(Overall.class))
                .fetchSize(CHUNK_SIZE)
                .dataSource(this.dataSource)
                .name("overall-ItemReader")
                .build();
    }

    @Bean
    public ItemProcessor<Overall, Overall> setupOverallProcessor() {
        return new SetupOverallProcessor();
    }

    @Bean
    @StepScope
    public ClassifierCompositeItemWriter<Overall> classifierCompositeItemWriter() {

        ClassifierCompositeItemWriter<Overall> classifierCompositeItemWriter
                = new ClassifierCompositeItemWriter<>();

        classifierCompositeItemWriter.setClassifier((Classifier<Overall, ItemWriter<? super Overall>>) overall -> {
            System.out.println(overall);
            System.out.println(overall.getStation());
            if(dataShareBean.isRemovedRestaurantsContainsKey(overall.getStation())) {
                return setupRemovedRestaurantsWriter();
            }

            return setupNewRestaurantsWriter();
        });

        return classifierCompositeItemWriter;
    }

    @Bean
    public SetupNewRestaurantsWriter setupNewRestaurantsWriter() {
        SetupNewRestaurantsWriter writer = new SetupNewRestaurantsWriter(this.dataSource);
        writer.prepareForUpdate();
        return writer;
    }

    @Bean
    public SetupRemovedRestaurantsWriter setupRemovedRestaurantsWriter() {
        SetupRemovedRestaurantsWriter writer = new SetupRemovedRestaurantsWriter(this.dataSource, this.dataShareBean);
        writer.prepareForUpdate();
        return writer;
    }

// Instaplace -> myRestaurants 컬럼에 dataSharedBean.removedList 에 포함된
// 단어(음식점) 있으면 Json_remove 해주기 위한 스텝 하나더 추가하기


//    @Bean
//    @JobScope
//    public Step setupRestaurantsSteps() {
//        return stepBuilderFactory.get(STEP_NAME)
//                .<Instaplace, Instaplace>chunk(CHUNK_SIZE) //<Reader에서 반환할 타입, Writer에 파라미터로 넘어올 타입>
//                .reader(setupRestaurantsReader())
//                //.processor(setupRestaurantsProcessor())
//                //.writer(setupRestaurantsWriter())
//                //.listener(overallPromotionLister())
//                .transactionManager(this.transactionManager)
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public JdbcCursorItemReader<Instaplace> setupRestaurantsReader() {
//
//    }
}
