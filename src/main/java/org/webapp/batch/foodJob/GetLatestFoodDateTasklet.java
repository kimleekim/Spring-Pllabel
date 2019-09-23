package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;
import java.util.Map;


@StepScope
@Component
public class GetLatestFoodDateTasklet implements Tasklet {
    private Logger logger = LoggerFactory.getLogger(GetLatestFoodDateTasklet.class);
    private FoodStepsDataShareBean dataShareBean;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private static final String sql = "SELECT station, max(instafood.date) FROM instafood " +
                                        "GROUP BY station";

    GetLatestFoodDateTasklet() {}

    public GetLatestFoodDateTasklet(DataSource dataSource, FoodStepsDataShareBean dataShareBean) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.dataShareBean = dataShareBean;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        logger.info("[FindHotFoodJob] : GetLatestFoodDate-Tasklet started.");
        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList(sql);

        for(Map<String, Object> result : resultList) {
            this.dataShareBean.putLatestFoodDatePerStation(
                    (String) result.get("station"), (Date) result.get("max(instafood.date)")
            );
        }

        return RepeatStatus.FINISHED;
    }
}
