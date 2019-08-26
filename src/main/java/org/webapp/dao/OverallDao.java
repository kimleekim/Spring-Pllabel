package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Instahot;
import org.webapp.model.Instaranking;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OverallDao extends Dao<Overall> {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private String sql;
    private List<Overall> overallList;

    @Autowired
    public OverallDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    OverallDao() {
    }

    @Override
    public void save(Overall station) {    //model = station명
        try {
            String sql = "insert into overall values (?, \'[]\', 0, 0, 0)";
            jdbcTemplate.update(sql, station);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Overall> findByParam(Map<String, Object> parameter){
        sql = "select * from Overall where ";
        List<Overall> result;

        result = jdbcTemplate.query(selectTarget("Overall", parameter, sql)
                , new OverallMapper());
        return result;
    }

    @Override
    public void delete(Map<String, Object> parameter) {
        sql = "delete from Overall where ";
        jdbcTemplate.update(selectTarget("Overall", parameter, sql));
    }

    public void update(Object model) {  //model = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <"stationName", station명>
        try {
            Map<Object, Object> overall = (Map<Object, Object>) model;
            Set<Object> keySet = overall.keySet();
            Iterator key = keySet.iterator();
            String sql = "";
            String column = "";
            Overall overallObject = null;
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            if (overall.containsKey("station")) {
                column = "station";
                sql = "update overall set station = ? where station = ?";
            }
            else if (overall.containsKey("restaurants")) {
                column = "restaurants";
                List<String> restaurants = (List<String>) overall.get("restaurants");
                System.out.println(restaurants);
                overallObject = new Overall();
                overallObject.setRestaurants(restaurants);
                sql = "update overall set restaurants = ? where station = ?";
            }
            else if (overall.containsKey("instaCNT")) {
                column = "instaCNT";
                sql = "update overall set instaCNT = ? where station = ?";
            }
            else if (overall.containsKey("youtubeCNT")) {
                column = "youtubeCNT";
                sql = "update overall set youtubeCNT = ? where station = ?";
            }
            else if (overall.containsKey("likeCNT")) {
                column = "likeCNT";
                sql = "update overall set likeCNT = ? where station = ?";
            }
            if (key.hasNext()) {
                if (overall.containsKey("restaurants")) {
                    jdbcTemplate.update(sql, overallObject.getRestaurants(), overall.get("stationName"));
                }
                else {
                    jdbcTemplate.update(sql, overall.get(column), overall.get("stationName"));
                }
            }
            overall.clear();
        } catch (Exception e) {
            System.out.println("overall update fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Overall> findAll() {
        sql = "select * from Overall";
        overallList = jdbcTemplate.query(sql, new OverallMapper());

        return overallList;
    }
}
