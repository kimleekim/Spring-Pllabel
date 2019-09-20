package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.List;
import java.util.Map;


@StepScope
@Component
public class GetLatestCrawlDateTasklet implements Tasklet {
    private Logger logger = LoggerFactory.getLogger(GetLatestCrawlDateTasklet.class);
    private LocationStepsDataShareBean dataShareBean;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private static final String sql = "select station, max(instaplace.date) from instaplace " +
                                        "group by station";

    GetLatestCrawlDateTasklet() {}

    public GetLatestCrawlDateTasklet(DataSource dataSource,
                                     LocationStepsDataShareBean dataShareBean) {
        this.dataSource = dataSource;
        jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.dataShareBean = dataShareBean;
    }


    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) {
        logger.info("[SearchLocationJob] : GetLatestCrawlDate-Tasklet started.");
        List<Map<String, Object>> resultList;

        resultList = jdbcTemplate.queryForList(sql);

        for(Map<String, Object> result : resultList) {
            this.dataShareBean.putLatestDatePerStation(
                    (String) result.get("station"), (Date) result.get("max(instaplace.date)")
            );
        }

        return RepeatStatus.FINISHED;
    }
}
