package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Youtubefood;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class YoutubefoodDao implements Dao {
    @Autowired
    DataSource dataSource;
    @Autowired
    DataSourceContext dataSourceContext;

    @Override
    public void save(Object model) {    //model = 셋팅된 Youtubefood객체
        try {
            Youtubefood youtubefood = (Youtubefood) model;
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into youtubefood(station, title, creator, youtubefood.date, thumbnailURL, videoLink) values (?, ?, ?, ?, ?, ?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, youtubefood.getStation(), youtubefood.getTitle(), youtubefood.getCreator(), youtubefood.getDate(),
                    youtubefood.getThumbnailURL(), youtubefood.getVideoLink());
        } catch (Exception e) {
            System.out.println("youtubefood save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = List<Object> parameter [0] = station명, [1] = date날짜
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from youtubefood where station = ? AND youtubefood.date = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Object> parameterList = (List<Object>) parameter;

            List<Object> returnList = new ArrayList<>();
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameterList.get(0), parameterList.get(1)},
                    new YoutubefoodMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("youtubefood find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter =
//        try {
//            dataSource = dataSourceContext.dataSource();
//            String sql = "delete from youtubefood where youtubefood.key = ?";
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.update(sql, parameter);
//        } catch (Exception e) {
//            System.out.println("youtubefood delete fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(Object model) {  //parameter = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <key, null>
//        try {
//            Map<Object, Object> youtubefood = (Map<Object, Object>) model;
//            Set<Object> keySet = youtubefood.keySet();
//            Iterator<Object> key = keySet.iterator();
//            String sql = "";
//
//            dataSource = dataSourceContext.dataSource();
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            if (youtubefood.containsKey("title")) {
//                sql = "update youtubefood set title = ? where youtubefood.key = ?";
//            }
//            else if (youtubefood.containsKey("creator")) {
//                sql = "update youtubefood set creator = ? where youtubefood.key = ?";
//            }
//            else if (youtubefood.containsKey("date")) {
//                sql = "update youtubefood set youtubefood.date = ? where youtubefood.key = ?";
//            }
//            else if (youtubefood.containsKey("thumbnailURL")) {
//                sql = "update youtubefood set thumbnailURL = ? where youtubefood.key = ?";
//            }
//            else if (youtubefood.containsKey("videoURL")) {
//                sql = "update youtubefood set videoURL = ? where youtubefood.key = ?";
//            }
//            if (key.hasNext()) {
//                jdbcTemplate.update(sql, youtubefood.get(key.next()), key.next());
//            }
//        } catch (Exception e) {
//            System.out.println("youtubefood update fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from youtubefood";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Youtubefood> totlaRows = jdbcTemplate.query(sql, new YoutubefoodMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("youtubefood findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
