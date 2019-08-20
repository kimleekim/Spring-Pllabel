package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Instaranking;

import javax.sql.DataSource;

import java.util.*;

@Repository
public class InstarankingDao implements Dao {
    @Autowired
    DataSource dataSource;
    @Autowired
    DataSourceContext dataSourceContext;

    @Override
    public void save(Object model) {    //model = 셋팅된 Instaranking객체
        try {
            Instaranking instaranking = (Instaranking) model;
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into instaranking(station, placetag, placetagCNT, likeCNT) values (?, ?, 1, ?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, instaranking.getStation(), instaranking.getPlacetag(), instaranking.getLikeCNT());
        } catch (Exception e) {
            System.out.println("instaranking save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = List<Object> parameter [0] = station명, [1] = date날짜
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instaranking where station = ? AND placetag = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Object> parameterList = (List<Object>) parameter;

            List<Object> returnList = new ArrayList<>();
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameterList.get(0), parameterList.get(1)},
                    new InstarankingMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("instaranking find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter =
//        try {
//            dataSource = dataSourceContext.dataSource();
//            String sql = "delete from instaranking where instaranking.key = ?";
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.update(sql, parameter);
//        } catch (Exception e) {
//            System.out.println("instaranking delete fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(Object model) {  //model은 사이즈2인 Map ==> [0] = <바꿀column, udpate할 값>, [1] = <station명, placetag명>
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Map<Object, Object> parameterList = (Map<Object, Object>) model;
            Set<Object> keySet = parameterList.keySet();
            Iterator<Object> key = keySet.iterator();

            if (parameterList.containsKey("placetag")) {
                sql = "update instaranking set placetag = ? where station = ?  AND placetag = ?";
            }
            else if (parameterList.containsKey("placetagCNT")) {
                sql = "update instaranking set placetagCNT = ? where station = ?  AND placetag = ?";
            }
            else if (parameterList.containsKey("likeCNT")) {
                sql = "update instaranking set likeCNT = ? where station = ?  AND placetag = ?";
            }
            if (key.hasNext()) {
                Object changedColumn = key.next();
                Object station = key.next();

                jdbcTemplate.update(sql, parameterList.get(changedColumn), station, parameterList.get(station));
            }
        } catch (Exception e) {
            System.out.println("instaranking update fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from instaranking";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Instaranking> totlaRows = jdbcTemplate.query(sql, new InstarankingMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("instaranking findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
