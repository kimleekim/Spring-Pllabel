package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.webapp.dao.mapper.OverallMapper;
import org.webapp.model.Overall;
import javax.sql.DataSource;
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
    public void save(Overall overall) {
        try {
            String sql = "insert into overall values (?, \'[]\', 0, 0, 0, 0)";
            jdbcTemplate.update(sql, overall.getStation());
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

    public void update(Map<Object, String> parameter, int conditionPosition) {
//        conditionPosition = n번째 데이터부터가 set절에 대한 조건이다 라는 표시
        List<Map<String, Object>> parameters = separateParameter(parameter, conditionPosition);
        sql = "update overall set ";
        sql = selectTarget("Overall", parameters.get(0), sql);
        sql += " where ";
        jdbcTemplate.update(selectTarget("Overall", parameters.get(1), sql));
    }

    private List<Map<String, Object>> separateParameter (Map<Object, String> parameter, int conditionPosition) {
        List<Map<String, Object>> parameters = new ArrayList<>();
        Map<String, Object> setParameter = new LinkedHashMap<>();
        Map<String, Object> whereParameter = new LinkedHashMap<>();
        Set<Object> keySet = parameter.keySet();
        Iterator<Object> keys = keySet.iterator();

        for (int i = 0; i < parameter.size(); i++) {
            Object key = keys.next();
            if (i < conditionPosition - 1) {
                whereParameter.put(parameter.get(key), key);
            }
            else {
                setParameter.put(parameter.get(key), key);
            }
        }
        parameters.add(setParameter);
        parameters.add(whereParameter);
        return parameters;
    }

    @Override
    public List<Overall> findAll() {
        sql = "select * from Overall";
        overallList = jdbcTemplate.query(sql, new OverallMapper());

        return overallList;
    }
}
