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
import org.webapp.model.Youtubehot;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@StepScope
@Component
public class SetupYoutubeHotPlaceWriter extends ResetExistedHotPlaceWriter
                                                implements ItemWriter<List<Youtubehot>> {

    private static final Logger logger = LoggerFactory.getLogger(SetupYoutubeHotPlaceWriter.class);
    private DataSource dataSource;
    private JdbcBatchItemWriter<Youtubehot> delegate;
    private String sql = "INSERT INTO youtubehot (station, title, content, totalview, creator, date, thumbnailURL, videoLink) " +
            "VALUES (:station, :title, :content, :totalview, :creator, :date, :thumbnailURL, :videoLink)";

    SetupYoutubeHotPlaceWriter() {}

    @Autowired
    public SetupYoutubeHotPlaceWriter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Youtubehot>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Youtubehot>> objects) throws Exception {
        logger.info("[FindHotPlaceJob] : SetupYoutubeHotPlace-ItemWriter started.");

        super.deleteYoutubehotPerStation(objects);

        List<Youtubehot> objectList = new ArrayList<>();
        for(List<Youtubehot> object : objects) {
            objectList.addAll(object);
        }

        this.delegate.write(objectList);
    }
}
