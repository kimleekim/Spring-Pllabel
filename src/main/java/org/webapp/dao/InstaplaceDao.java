package org.webapp.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.webapp.config.DataSourceContext;
import org.webapp.model.Instaplace;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class InstaplaceDao {
    private DataSource dataSource;
    private DataSourceContext dataSourceContext = new DataSourceContext();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

    public void insertInstaplace (List<Object> row) throws Exception {
        dataSource = dataSourceContext.dataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String sql = "insert into instaplace(station, post, date, hashtag) values (?, ?, ?, ?)";

        jdbcTemplate.update(sql, (String)row.get(0), (String)row.get(1), simpleDateFormat.parse((String) row.get(2)), gson.toJson(row.get(3)));
    }
}
