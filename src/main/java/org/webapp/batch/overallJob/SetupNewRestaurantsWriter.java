package org.webapp.batch.overallJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.webapp.model.Overall;
import javax.sql.DataSource;
import java.util.List;


@StepScope
public class SetupNewRestaurantsWriter implements ItemWriter<Overall> {

    private final Logger logger = LoggerFactory.getLogger(SetupNewRestaurantsWriter.class);
    private JdbcBatchItemWriter<Overall> updateDelegate;
    private DataSource dataSource;
    private static final String updateSql
            = "UPDATE overall " +
            "SET restaurants = json_merge_preserve(restaurants, :restaurants) " +
            "WHERE station=:station";


    public SetupNewRestaurantsWriter() {}

    public SetupNewRestaurantsWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.updateDelegate = new JdbcBatchItemWriter<Overall>();
        this.updateDelegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Overall>());
        this.updateDelegate.setDataSource(dataSource);
        this.updateDelegate.setJdbcTemplate(new NamedParameterJdbcTemplate(dataSource));
        this.updateDelegate.setSql(updateSql);
        this.updateDelegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends Overall> items) throws Exception {
        logger.info("[SetupOverallJob] : SetupNewRestaurants-ItemWriter started.");
        this.updateDelegate.write(items);
    }

    public JdbcBatchItemWriter<Overall> getDelegate() {
        return this.updateDelegate;
    }

}