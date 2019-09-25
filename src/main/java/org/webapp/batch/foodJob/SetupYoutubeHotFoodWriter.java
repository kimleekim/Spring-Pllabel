package org.webapp.batch.foodJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.webapp.model.Instafood;
import org.webapp.model.Youtubefood;
import org.webapp.model.Youtubehot;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;


@StepScope
@Component
public class SetupYoutubeHotFoodWriter implements ItemWriter<List<Youtubefood>> {
    private static final Logger logger = LoggerFactory.getLogger(SetupYoutubeHotFoodWriter.class);
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private JdbcBatchItemWriter<Youtubefood> delegate;
    private String sql = "INSERT INTO youtubefood (station, keyword, title, content, totalview, creator, date, thumbnailURL, videoLink) " +
            "VALUES (:station, :keyword, :title, :content, :totalview, :creator, :date, :thumbnailURL, :videoLink)";

    SetupYoutubeHotFoodWriter() {}

    @Autowired
    public SetupYoutubeHotFoodWriter(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    }

    @BeforeStep
    public void prepareForUpdate() {
        this.delegate = new JdbcBatchItemWriter<>();
        this.delegate.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Youtubefood>());
        this.delegate.setDataSource(this.dataSource);
        this.delegate.setJdbcTemplate(new NamedParameterJdbcTemplate(this.dataSource));
        this.delegate.setSql(sql);
        this.delegate.afterPropertiesSet();
    }

    @Override
    public void write(List<? extends List<Youtubefood>> objectList) throws Exception {
        logger.info("[FindHotFoodJob] : SetupYoutubeHotFood-ItemWriter started.");

        deleteYoutubeFoodPerStation(objectList);

        List<Youtubefood> objects = new ArrayList<>();
        for(List<Youtubefood> object : objectList) {
            objects.addAll(object);
        }

        this.delegate.write(objects);
    }

    private void deleteYoutubeFoodPerStation(List<? extends List<Youtubefood>> objectList) {
        logger.info("[FindHotFoodJob] : DeleteYoutubeFood-Per-Station-privateMethod started.");
        String station;

        try {
            station = objectList.get(0).get(0).getStation();
        } catch(IndexOutOfBoundsException e) {
            return;
        }

        sql = "Delete from youtubefood where station=\"" + station + "\"";

        jdbcTemplate.update(sql);
    }
}
