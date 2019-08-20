package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Overall;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class OverallDao implements Dao {
    @Autowired
    DataSourceContext dataSourceContext = new DataSourceContext();
    DataSource dataSource = null;

    @Override
    public void save(Object model) {    //model = station명
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into overall values (?, 0, 0, 0)";
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
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from overall where station = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

            List<Object> returnList = new ArrayList<>();
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
            dataSource = dataSourceContext.dataSource();
            String sql = "delete from overall where station = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, parameter);
        } catch (Exception e) {
            System.out.println("overall delete fail!");
            e.printStackTrace();
        }
    }

    @Override
    public void update(Object model) {  //model = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <station명, null>
        try {
            Map<Object, Object> overall = (Map<Object, Object>) model;
            Set<Object> keySet = overall.keySet();
            Iterator<Object> key = keySet.iterator();
            String sql = "";

            dataSource = dataSourceContext.dataSource();
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            if (overall.containsKey("station")) {
                sql = "update overall set station = ? where station = ?";
            }
            else if (overall.containsKey("instaCNT")) {
                sql = "update overall set instaCNT = ? where station = ?";
            }
            else if (overall.containsKey("youtubeCNT")) {
                sql = "update overall set youtubeCNT = ? where station = ?";
            }
            else if (overall.containsKey("likeCNT")) {
                sql = "update overall set likeCNT = ? where station = ?";
            }
            if (key.hasNext()) {
                jdbcTemplate.update(sql, overall.get(key.next()), key.next());
            }
        } catch (Exception e) {
            System.out.println("overall update fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
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
