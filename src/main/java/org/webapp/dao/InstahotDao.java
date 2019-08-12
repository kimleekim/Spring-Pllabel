package org.webapp.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.webapp.config.DataSourceContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.text.ParseException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class InstahotDao {
    private DataSource dataSource;
    private DataSourceContext dataSourceContext = new DataSourceContext();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    public void insertInstahot (List<Object> row) throws SQLException, ParseException {
        dataSource = dataSourceContext.dataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "insert into instahot(station, post, photoURL, date) values (?, ?, ?, ?)";

        jdbcTemplate.update(sql, (String)row.get(0), (String)row.get(1), (String)row.get(4), simpleDateFormat.parse((String) row.get(2)));
    }
}
