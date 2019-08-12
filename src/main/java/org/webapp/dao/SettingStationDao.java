package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.webapp.config.DataSourceContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class SettingStationDao {
    @Autowired
    DataSourceContext dataSourceContext = new DataSourceContext();
    DataSource dataSource = null;
    private Connection connection = null;

    public void setStation (List<String> stations) throws SQLException {
        dataSource = dataSourceContext.dataSource();
        connection = dataSource.getConnection();
        String sql = "insert into overall values (?, 0, 0, 0)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        int index = 0;

        while (index < stations.size()) {
            jdbcTemplate.update(sql, stations.get(index));
            index++;
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
