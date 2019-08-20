package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Instahot;

import javax.sql.DataSource;

import java.util.*;

@Repository
public class InstahotDao implements Dao {
    @Autowired
    DataSource dataSource;
    @Autowired
    DataSourceContext dataSourceContext;

    @Override
    public void save(Object model) {    //model = 셋팅된 Instahot객체
        try {
            Instahot instahot = (Instahot) model;
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into instahot(station, post, photoURL, instahot.date) values (?, ?, ?, ?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, instahot.getStation(), instahot.getPost(), instahot.getPhotoURL(), instahot.getDate());
        } catch (Exception e) {
            System.out.println("instahot save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = List<Object> parameter [0] = station명, [1] = date날짜
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instahot where station = ? AND instahot.date = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Object> parameterList = (List<Object>) parameter;

            List<Object> returnList = new ArrayList<>();
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameterList.get(0), parameterList.get(1)},
                    new InstahotMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("instahot find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter =
//        try {
//            dataSource = dataSourceContext.dataSource();
//            String sql = "delete from instahot where instahot.key = ?";
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.update(sql, parameter);
//        } catch (Exception e) {
//            System.out.println("instahot delete fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(Object model) {  //parameter = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <key, null>
//        try {
//            Map<Object, Object> instahot = (Map<Object, Object>) model;
//            Set<Object> keySet = instahot.keySet();
//            Iterator<Object> key = keySet.iterator();
//            String sql = "";
//
//            dataSource = dataSourceContext.dataSource();
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            if (instahot.containsKey("post")) {
//                sql = "update instahot set post = ? where instahot.key = ?";
//            }
//            else if (instahot.containsKey("photoURL")) {
//                sql = "update instahot set photoURL = ? where instahot.key = ?";
//            }
//            else if (instahot.containsKey("date")) {
//                sql = "update instahot set instahot.date = ? where instahot.key = ?";
//            }
//            if (key.hasNext()) {
//                jdbcTemplate.update(sql, instahot.get(key.next()), key.next());
//            }
//        } catch (Exception e) {
//            System.out.println("instahot update fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instahot";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Instahot> totlaRows = jdbcTemplate.query(sql, new InstahotMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("instahot findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
