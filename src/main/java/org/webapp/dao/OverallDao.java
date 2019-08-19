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
import java.util.List;

@Repository
public class OverallDao implements Dao {
    @Autowired
    DataSourceContext dataSourceContext = new DataSourceContext();
    DataSource dataSource = null;

    @Override
    public void save(Object model) {
        dataSource = dataSourceContext.dataSource();
        String sql = "insert into overall values (?, 0, 0, 0)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(sql, model);
    }

    @Override
    public Object findByParam(Object parameter) {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from overall where station = ?";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            Overall row = jdbcTemplate.queryForObject(sql, new Object[] {parameter}, new OverallMapper());
            return row;
        } catch (EmptyResultDataAccessException d) {
            return null;
        }
    }

    @Override
    public void delete(Object parameter) {

    }

    @Override
    public void update(Object model) {
//        //model = [역명, instaCNT, youtubeCNT]
//        dataSource = dataSourceContext.dataSource();
//        String sql = "update overall set instaCNT = ?, youtubeCNT = ? where station = ?";
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        Overall row = jdbcTemplate.queryForObject(sql, new Object[]{}, new OverallMapper());
    }

    @Override
    public List findAll() {
        try {
            dataSource = dataSourceContext.dataSource();
            String sql = "select * from overall";
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            List<Overall> totlaRows = (List<Overall>) jdbcTemplate.queryForObject(sql, new OverallMapper());
            return totlaRows;
        } catch (EmptyResultDataAccessException d) {
            return null;
        }
    }
}
