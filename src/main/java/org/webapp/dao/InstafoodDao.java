package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.dao.mapper.InstafoodMapper;
import org.webapp.model.Instafood;
import javax.sql.DataSource;
import java.util.*;


@Repository
public class InstafoodDao extends Dao<Instafood> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Instafood> instafoodList;

    @Autowired
    public InstafoodDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public InstafoodDao() {}

    @Override
    public void save(Instafood instafood) {
        try {
            String sql = "insert into instafood(station, post, instafood.date, likeCNT, myRestaurant, photoURL) values (?, ?, ?, ?, ?, ?)";

            jdbcTemplate.update(sql,
                                instafood.getStation(),
                                instafood.getPost(),
                                instafood.getDate(),
                                instafood.getLikeCNT(),
                                instafood.getMyRestaurant(),
                                instafood.getPhotoURL());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Instafood> findByParam(Map<String, Object> parameter){
        sql = "select * from instafood where ";
        List<Instafood> result;

        result = jdbcTemplate.query(selectTarget("Instafood", parameter, sql)
                , new InstafoodMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from instafood where ";
        jdbcTemplate.update(selectTarget("Instafood", parameter, sql));
    }

    @Override
    public List<Instafood> findAll() {
        sql = "select * from instafood";
        instafoodList = jdbcTemplate.query(sql, new InstafoodMapper());

        return instafoodList;
    }
}
