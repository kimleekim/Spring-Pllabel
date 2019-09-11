package org.webapp.batch.overallJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemSqlParameterSourceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import java.util.List;

@StepScope
public class SetupRemovedRestaurantsWriter implements ItemWriter<Overall> {

    private final Logger logger = LoggerFactory.getLogger(SetupRemovedRestaurantsWriter.class);
    private DataSource dataSource;
    private ItemSqlParameterSourceProvider<Overall> itemSqlParameterSourceProvider;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private OverallStepsDataShareBean<Overall> overallDataShareBean;
    private int index;


    @Autowired
    private SetupNewRestaurantsWriter setupNewRestaurantsWriter;

    public SetupRemovedRestaurantsWriter() {}

    public SetupRemovedRestaurantsWriter(DataSource dataSource, OverallStepsDataShareBean<Overall> overallDataShareBean) {
        this.dataSource = dataSource;
        this.overallDataShareBean = overallDataShareBean;
    }


    @BeforeStep
    public void prepareForUpdate() {

        this.itemSqlParameterSourceProvider = new BeanPropertyItemSqlParameterSourceProvider<Overall>();
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

    }

    @Override
    public void write(List<? extends Overall> items) throws Exception {
        logger.info("[SetupOverallJob] : SetupRemovedRestaurants-ItemWriter started.");

        SqlParameterSource batchSqlArgs;
        String sql;
        List<String> existedList;
        List<String> removedList;

        for(Overall item : items) {
            batchSqlArgs = itemSqlParameterSourceProvider.createSqlParameterSource(item);

            existedList = overallDataShareBean.getExistedRestaurants(item.getStation());
            removedList = overallDataShareBean.getRemovedRestaurants(item.getStation());
            for(String removed : removedList) {
                this.index = existedList.indexOf(removed);
                sql = setQuery(this.index);

                existedList.remove(removed);

                namedParameterJdbcTemplate.update(sql, batchSqlArgs);
            }
        }

        setupNewRestaurantsWriter.write(items);
    }

    private String setQuery(int index) {
        String removeSql;

        removeSql = "UPDATE overall " +
                "SET restaurants = json_remove(restaurants, '$[" + index + "]') " +
                "WHERE station=:station";

        return removeSql;
    }
}
