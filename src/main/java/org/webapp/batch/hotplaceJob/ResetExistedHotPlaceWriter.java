package org.webapp.batch.hotplaceJob;

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
import org.webapp.dao.InstahotDao;
import org.webapp.dao.YoutubehotDao;
import org.webapp.model.Instahot;
import org.webapp.model.Youtubehot;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Component
public class ResetExistedHotPlaceWriter<T> {

    private static final Logger logger = LoggerFactory.getLogger(ResetExistedHotPlaceWriter.class);
    private DataSource dataSource;
    private JdbcTemplate jdbc;

    ResetExistedHotPlaceWriter() {}

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        jdbc = new JdbcTemplate(this.dataSource);
    }

    protected void deleteInstahotPerStation(List<? extends List<Instahot>> objects) {
        logger.info("[FindHotPlaceJob] : ResetInstaHotPlace-SuperMethod started.");
        String station;
        String sql;

        try {
            station = objects.get(0).get(0).getStation();
        } catch(IndexOutOfBoundsException e) {
            return;
        }

        sql = "Delete from instahot where station=\"" + station + "\"";

        jdbc.update(sql);
    }

    protected void deleteYoutubehotPerStation(List<? extends List<Youtubehot>> objects) {
        logger.info("[FindHotPlaceJob] : ResetYoutubeHotPlace-SuperMethod started.");

        String station;
        String sql;

        try {
            System.out.println("지우게 될 역 : " + objects.get(0).get(0).getStation());
            station = objects.get(0).get(0).getStation();
        } catch(IndexOutOfBoundsException e) {
            return;
        }

        sql = "Delete from youtubehot where station=\"" + station + "\"";

        jdbc.update(sql);
    }

}
