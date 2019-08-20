package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Youtubehot;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class YoutubehotDao implements Dao {
    @Autowired
    DataSource dataSource;
    @Autowired
    DataSourceContext dataSourceContext;

    @Override
    public void save(Object model) {    //model = 셋팅된 Youtubehot객체
        try {
            Youtubehot youtubehot = (Youtubehot) model;
            dataSource = dataSourceContext.dataSource();
            String sql = "insert into youtubehot(station, title, creator, youtubehot.date, thumbnailURL, videoLink) values (?, ?, ?, ?, ?, ?)";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update(sql, youtubehot.getStation(), youtubehot.getTitle(), youtubehot.getCreator(), youtubehot.getDate(),
                    youtubehot.getThumbnailURL(), youtubehot.getVideoLink());
        } catch (Exception e) {
            System.out.println("youtubehot save fail!");
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> findByParam(Object parameter) {   //parameter = List<Object> parameter [0] = station명, [1] = date날짜
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from youtubehot where station = ? AND youtubehot.date = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Object> parameterList = (List<Object>) parameter;

            List<Object> returnList = new ArrayList<>();
            returnList.add(jdbcTemplate.queryForObject(sql, new Object[] {parameterList.get(0), parameterList.get(1)},
                    new YoutubehotMapper()));
            return returnList;
        } catch (Exception e) {
            System.out.println("youtubehot find fail!");
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {  //parameter =
//        try {
//            dataSource = dataSourceContext.dataSource();
//            String sql = "delete from youtubehot where youtubehot.key = ?";
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            jdbcTemplate.update(sql, parameter);
//        } catch (Exception e) {
//            System.out.println("youtubehot delete fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(Object model) {  //parameter = 사이즈2인 Map ===> Map[0] = <바꿀column, udpate할 값>, Map[1] = <key, null>
//        try {
//            Map<Object, Object> youtubehot = (Map<Object, Object>) model;
//            Set<Object> keySet = youtubehot.keySet();
//            Iterator<Object> key = keySet.iterator();
//            String sql = "";
//
//            dataSource = dataSourceContext.dataSource();
//            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//            if (youtubehot.containsKey("title")) {
//                sql = "update youtubehot set title = ? where youtubehot.key = ?";
//            }
//            else if (youtubehot.containsKey("creator")) {
//                sql = "update youtubehot set creator = ? where youtubehot.key = ?";
//            }
//            else if (youtubehot.containsKey("date")) {
//                sql = "update youtubehot set youtubehot.date = ? where youtubehot.key = ?";
//            }
//            else if (youtubehot.containsKey("thumbnailURL")) {
//                sql = "update youtubehot set thumbnailURL = ? where youtubehot.key = ?";
//            }
//            else if (youtubehot.containsKey("videoURL")) {
//                sql = "update youtubehot set videoURL = ? where youtubehot.key = ?";
//            }
//            if (key.hasNext()) {
//                jdbcTemplate.update(sql, youtubehot.get(key.next()), key.next());
//            }
//        } catch (Exception e) {
//            System.out.println("youtubehot update fail!");
//            e.printStackTrace();
//        }
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from youtubehot";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Youtubehot> totlaRows = jdbcTemplate.query(sql, new YoutubehotMapper());
            return totlaRows;
        } catch (Exception e) {
            System.out.println("youtubehot findAll fail!");
            e.printStackTrace();
            return null;
        }
    }
}
