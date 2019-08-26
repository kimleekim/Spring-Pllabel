package org.webapp.batch;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.util.StringUtils;

import java.util.*;


@EnableBatchProcessing
@Configuration
public class BatchSettings {
    private static final String JOB_NAME = "job";
    private static final String JOB_PARAMETERS_NAME_BY_CONFIG = "jobParameters";
    private static final String JOB_PARAMETERS_NAME_BY_TRIGGER = "triggerJobParameters";
    private static final String JOB_PARAMETERS_INSTANCE_ID = "InstanceId";
    private static final String JOB_PARAMETERS_TIMESTAMP = "timestamp";
    private static final List<String> KEYWORDS = Arrays.asList(JOB_NAME, JOB_PARAMETERS_NAME_BY_CONFIG);

    public static JobDetailFactoryBeanBuilder jobDetailFactoryBeanBuilder() {
        return new JobDetailFactoryBeanBuilder();
    }

    public static CronTriggerFactoryBeanBuilder cronTriggerFactoryBeanBuilder() {
        return new CronTriggerFactoryBeanBuilder();
    }

    public static String getJobName(JobDataMap jobDataMap) {
        return (String) jobDataMap.get(JOB_NAME);
    }

    public static JobParameters getJobParameters(JobExecutionContext context) throws SchedulerException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        JobParameters jobParameters = getJobParametersMap(
                (JobParameters) jobDataMap.get(JOB_PARAMETERS_NAME_BY_CONFIG),
                (JobParameters) jobDataMap.get(JOB_PARAMETERS_NAME_BY_TRIGGER)
        );

        return new JobParametersBuilder(jobParameters)
                .addString(JOB_PARAMETERS_INSTANCE_ID, context.getScheduler().getSchedulerInstanceId())
                .addLong(JOB_PARAMETERS_TIMESTAMP, System.currentTimeMillis())
                .toJobParameters();
    }

    private static JobParameters getJobParametersMap(JobParameters jobParameter, JobParameters triggerJobParameter) {
        Map<String, JobParameter> jobParameterMap = new HashMap<>();

        jobParameterMap.putAll(StringUtils.isEmpty(jobParameter)?Collections.EMPTY_MAP:jobParameter.getParameters());
        jobParameterMap.putAll(StringUtils.isEmpty(triggerJobParameter)?Collections.EMPTY_MAP:triggerJobParameter.getParameters());

        return new JobParameters(jobParameterMap);
    }


    public static class JobDetailFactoryBeanBuilder {
        boolean durability = true;
        boolean recovery = true;
        private Map<String, Object> jobDetailMap; //jobDataMap의 역할, job과 1대 1관계
        private JobParametersBuilder jobParametersBuilder;

        public JobDetailFactoryBeanBuilder() {
            this.jobDetailMap = new HashMap<>();
            this.jobParametersBuilder = new JobParametersBuilder();
        }

        //jobDataMap 생성
        public JobDetailFactoryBeanBuilder job(Job job) {
            this.jobDetailMap.put(JOB_NAME, job.getName());
            return this;
        }

        public JobDetailFactoryBeanBuilder durability(boolean durability) {
            this.durability = durability;
            return this;
        }

        public JobDetailFactoryBeanBuilder recovery(boolean recovery) {
            this.recovery = recovery;
            return this;
        }

        public JobDetailFactoryBeanBuilder parameter(String key, Object value) {
            if(KEYWORDS.contains(key))
                throw new RuntimeException("Invalid Parameter.");
            this.addParameter(key, value);
            return this;
        }

        private void addParameter(String key, Object value) {
            if (value instanceof String) {
                this.jobParametersBuilder.addString(key, (String) value);
                return;
            } else if (value instanceof Float || value instanceof Double) {
                this.jobParametersBuilder.addDouble(key, ((Number) value).doubleValue());
                return;
            } else if (value instanceof Integer || value instanceof Long) {
                this.jobParametersBuilder.addLong(key, ((Number) value).longValue());
                return;
            } else if (value instanceof Date) {
                this.jobParametersBuilder.addDate(key, (Date) value);
                return;
            } else if (value instanceof JobParameter) {
                this.jobParametersBuilder.addParameter(key, (JobParameter) value);
                return;
            }
            throw new RuntimeException("Not Supported Parameter Type.");
        }

        public JobDetailFactoryBean build() {
            if(!jobDetailMap.containsKey(JOB_NAME)) {
                throw new RuntimeException("Not Found Job Name.");
            }
            jobDetailMap.put(JOB_PARAMETERS_NAME_BY_CONFIG, jobParametersBuilder.toJobParameters());

            JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
            jobDetailFactoryBean.setJobClass(BatchJobRunner.class); //execute() 실행
            jobDetailFactoryBean.setDurability(this.durability);
            jobDetailFactoryBean.setRequestsRecovery(this.recovery);
            jobDetailFactoryBean.setJobDataAsMap(this.jobDetailMap);

            return jobDetailFactoryBean;
        }
    }

    public static class CronTriggerFactoryBeanBuilder {
        private String name;
        private String cronExpression;
        private JobDetailFactoryBean jobDetailFactoryBean;

        public CronTriggerFactoryBeanBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CronTriggerFactoryBeanBuilder cronExpression(String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public CronTriggerFactoryBeanBuilder jobDetailFactoryBean(JobDetailFactoryBean jobDetailFactoryBean) {
            this.jobDetailFactoryBean = jobDetailFactoryBean;
            return this;
        }

        public CronTriggerFactoryBean build() {
            if(this.cronExpression == null || this.jobDetailFactoryBean == null) {
                throw new RuntimeException("Cannot get cronExpression and jobDetailFactoryBean");
            }
            CronTriggerFactoryBean cronTriggerFactoryBean = new CronTriggerFactoryBean();
            cronTriggerFactoryBean.setName(this.name);
            cronTriggerFactoryBean.setCronExpression(this.cronExpression);
            cronTriggerFactoryBean.setJobDetail(Objects.requireNonNull(this.jobDetailFactoryBean.getObject()));

            return cronTriggerFactoryBean;
        }
    }
}
