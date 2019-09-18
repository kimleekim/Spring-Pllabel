package org.webapp.batch.hotplaceJob;

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
import org.webapp.model.Instahot;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@StepScope
@Component
public class SetupInstaHotPlaceWriter extends ResetExistedHotPlaceWriter
                                            implements ItemWriter<List<Instahot>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupInstaHotPlaceWriter.class);
    private DataSource dataSource;
    private JdbcBatchItemWriter<Instahot> delegate;
    private static final String sql = "INSERT INTO Instahot (station, post, photoURL, date) " +
                                                    "values (:station, :post, :photoURL, :date)";

    SetupInstaHotPlaceWriter() {}

    @Autowired
    public SetupInstaHotPlaceWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Instahot>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Instahot>> objects) throws Exception {
        logger.info("[FindHotPlaceJob] : SetupInstaHotPlace-ItemWriter started.");

        super.deleteInstahotPerStation(objects);

        List<Instahot> objectList = new ArrayList<>();
        for(List<Instahot> object : objects) {
            objectList.addAll(object);
        }

        this.delegate.write(objectList);
    }
}
