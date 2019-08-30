package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.dao.mapper.YoutubefoodMapper;
import org.webapp.model.Youtubefood;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class YoutubefoodDao extends Dao<Youtubefood> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Youtubefood> youtubefoodList;

    @Autowired
    public YoutubefoodDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public YoutubefoodDao() {}

    @Override
    public void save(Youtubefood youtubefood) {
        try {
            String sql = "insert into youtubefood(station, title, content, totalview, creator, youtubefood.date, thumbnailURL, videoLink) values (?, ?, ?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                    youtubefood.getStation(),
                    youtubefood.getTitle(),
                    youtubefood.getContent(),
                    youtubefood.getTotalview(),
                    youtubefood.getCreator(),
                    youtubefood.getDate(),
                    youtubefood.getThumbnailURL(),
                    youtubefood.getVideoLink());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Youtubefood> findByParam(Map<String, Object> parameter) {
        sql = "select * from youtubefood where";
        List<Youtubefood> result;

        result = jdbcTemplate.query(selectTarget("youtubefood", parameter, sql)
                , new YoutubefoodMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from youtubefood where ";
        jdbcTemplate.update(selectTarget("youtubefood", parameter, sql));
    }

    @Override
    public List<Youtubefood> findAll() {
        sql = "select * from youtubefood";
        youtubefoodList = jdbcTemplate.query(sql, new YoutubefoodMapper());

        return youtubefoodList;
    }
}
