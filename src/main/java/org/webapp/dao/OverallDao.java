package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OverallDao implements Dao {
    private DataSource dataSource;

    @Autowired
    OverallDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    OverallDao() {
    }

    @Override
    public void save(Object model) {    //model = station명
        try {
            String sql = "insert into overall2 values (?, \'[]\', 0, 0, 0)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, model);
        } catch (Exception e) {
            System.out.println("overall save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = station명
        try {
            String sql = "select * from overall where station = ?";
            List<Object> returnList = new ArrayList<>();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameter},
                    new OverallMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("overall find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter = station명
        try {
            String sql = "delete from overall where station = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, parameter);
        } catch (Exception e) {
            System.out.println("overall delete fail!");
            e.printStackTrace();
        }
    }

    @Override
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
    public List findAll() {
        try {
            String sql = "select * from overall";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Overall> totlaRows = jdbcTemplate.query(sql, new OverallMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("overall findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
