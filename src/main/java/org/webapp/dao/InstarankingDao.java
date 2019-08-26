package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.model.Instaranking;
import javax.sql.DataSource;
import java.util.*;

@Repository
public class InstarankingDao extends Dao<Instaranking> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Instaranking> instarankingList;

    @Autowired
    public InstarankingDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public InstarankingDao() {}

    @Override
    public void save(Instaranking instaranking) {
        try {
            String sql = "insert into instaranking(station, placetag, placetagCNT, likeCNT) values (?, ?, 1, ?)";

            jdbcTemplate.update(sql,
                                instaranking.getStation(),
                                instaranking.getPlacetag(),
                                instaranking.getLikeCNT());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Instaranking> findByParam(Map<String, Object> parameter){
        sql = "select * from Instaranking where ";
        List<Instaranking> result;

        result = jdbcTemplate.query(selectTarget("Instaranking", parameter, sql)
                , new InstarankingMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from Instaranking where ";
        jdbcTemplate.update(selectTarget("Instaranking", parameter, sql));
    }

    @Override
    public List<Instaranking> findAll() {
        sql = "select * from Instaranking";
        instarankingList = jdbcTemplate.query(sql, new InstarankingMapper());

        return instarankingList;
    }
}
