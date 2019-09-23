package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.webapp.batch.OldDataDeleter;
import org.webapp.model.Instafood;
import org.webapp.model.Instaranking;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@StepScope
@Component
public class GetInstaFoodPostsWriter extends OldDataDeleter
                                        implements ItemWriter<List<Instafood>> {

    private static final Logger logger = LoggerFactory.getLogger(GetInstaFoodPostsWriter.class);
    private DataSource dataSource;
    private JdbcBatchItemWriter<Instafood> delegate;
    private String sql = "INSERT INTO instafood (station, post, date, likeCNT, myRestaurant, photoURL) " +
                            "VALUES (:station, :post, :date, :likeCNT, :myRestaurant, :photoURL)";
    private boolean checkedFirst;

    GetInstaFoodPostsWriter() {
        checkedFirst = false;
    }

    @Autowired
    public GetInstaFoodPostsWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    private void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Instafood>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(this.sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Instafood>> objects) throws Exception {
        logger.info("[FindHotFoodJob] : GetInstaFoodPosts-ItemWriter started.");

        if(!checkedFirst) {
            checkedFirst = true;
            super.setDataSource(this.dataSource);
            super.deleteFoodBeforeTwoMonths();
        }

        List<Instafood> objectList = new ArrayList<>();
        for(List<Instafood> object : objects) {
            objectList.addAll(object);
        }

        this.delegate.write(objectList);
    }
}
