package org.webapp.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;


@Component("OldTableDataDeleter")
public class OldDataDeleter {

    private static final Logger logger = LoggerFactory.getLogger(OldDataDeleter.class);
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    protected void deleteLocationBeforeThreeMonths() {
        logger.info("[SearchLocationJob] : DeleteLocationBeforeThreeMonths-SuperMethod started.");

        String sql;
        sql = "DELETE FROM instaplace WHERE instaplace.date < date_add(now(), interval -3 month)";

        jdbcTemplate.update(sql);
    }

    protected void deleteFoodBeforeTwoMonths() {
        logger.info("[FindHotFoodJob] : DeleteFoodBeforeTwoMonths-SuperMethod started.");

        String foodSql;
        foodSql = "DELETE FROM instafood WHERE instafood.date < date_add(now(), interval -2 month)";

        jdbcTemplate.update(foodSql);
    }
}
