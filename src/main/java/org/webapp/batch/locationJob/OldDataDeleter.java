package org.webapp.batch.locationJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;


@Component
class OldDataDeleter {

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
}
