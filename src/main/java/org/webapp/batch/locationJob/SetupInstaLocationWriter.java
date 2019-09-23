package org.webapp.batch.locationJob;

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
import org.webapp.model.Instaplace;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@StepScope
@Component
public class SetupInstaLocationWriter extends OldDataDeleter
                                        implements ItemWriter<List<Instaplace>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupInstaLocationWriter.class);
    private DataSource dataSource;
    private JdbcBatchItemWriter<Instaplace> delegate;
    private static final String sql = "INSERT INTO instaplace (station, post, likeCNT, date, hashtag, description) " +
                                        "VALUES (:station, :post, :likeCNT, :date, :hashtag, :description)";
    private boolean passFirstAccess;

    SetupInstaLocationWriter() {
        this.passFirstAccess = false;
    }

    @Autowired
    public SetupInstaLocationWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<Instaplace>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Instaplace>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Instaplace>> objects) throws Exception {
        logger.info("[SearchLocationJoc] : SetupInstaLocation-ItemWriter started.");

        if(!passFirstAccess) {
            passFirstAccess = true;
            super.setDataSource(this.dataSource);
            super.deleteLocationBeforeThreeMonths();
        }

        List<Instaplace> objectList = new ArrayList<>();
        for(List<Instaplace> object : objects) {
            objectList.addAll(object);
        }

        this.delegate.write(objectList);
    }

}
