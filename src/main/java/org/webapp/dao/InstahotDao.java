package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.model.Instahot;
import javax.sql.DataSource;
import java.util.*;


@Repository
public class InstahotDao extends Dao<Instahot> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Instahot> instahotList;

    @Autowired
    public InstahotDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public InstahotDao() {}

    @Override
    public void save(Instahot instahot) {
        try {
            String sql = "insert into instahot(station, post, photoURL, instahot.date) values (?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                                instahot.getStation(),
                                instahot.getPost(),
                                instahot.getPhotoURL(),
                                instahot.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Instahot> findByParam(Map<String, Object> parameter){
        sql = "select * from Instahot where ";
        List<Instahot> result;

        result = jdbcTemplate.query(selectTarget("Instahot", parameter, sql)
                , new InstahotMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from Instahot where ";
        jdbcTemplate.update(selectTarget("Instahot", parameter, sql));
    }

    @Override
    public List<Instahot> findAll() {
        sql = "select * from Instahot";
        instahotList = jdbcTemplate.query(sql, new InstahotMapper());

        return instahotList;
    }
}


