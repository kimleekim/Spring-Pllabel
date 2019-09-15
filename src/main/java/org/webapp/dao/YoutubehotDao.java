package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.dao.mapper.YoutubehotMapper;
import org.webapp.model.Youtubehot;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class YoutubehotDao extends Dao<Youtubehot> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Youtubehot> youtubehotList;

    @Autowired
    public YoutubehotDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public YoutubehotDao() {}

    @Override
    public void save(Youtubehot youtubehot) {
        try {
            String sql = "insert into youtubehot(station, title, content, totalview, creator, youtubehot.date, thumbnailURL, videoLink) values (?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    youtubehot.getStation(),
                    youtubehot.getTitle(),
                    youtubehot.getContent(),
                    youtubehot.getTotalview(),
                    youtubehot.getCreator(),
                    youtubehot.getDate(),
                    youtubehot.getThumbnailURL(),
                    youtubehot.getVideoLink());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Youtubehot> findByParam(Map<String, Object> parameter) {
        sql = "select * from youtubehot where ";
        List<Youtubehot> result;

        result = jdbcTemplate.query(selectTarget("youtubehot", parameter, sql)
                , new YoutubehotMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from youtubehot where ";
        jdbcTemplate.update(selectTarget("youtubehot", parameter, sql));
    }

    @Override
    public List<Youtubehot> findAll() {
        sql = "select * from youtubehot";
        youtubehotList = jdbcTemplate.query(sql, new YoutubehotMapper());

        return youtubehotList;
    }
}
