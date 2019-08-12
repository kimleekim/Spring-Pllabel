package org.webapp.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.webapp.config.DataSourceContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class SettingStationDao {
    @Autowired
    DataSourceContext dataSourceContext = new DataSourceContext();
    DataSource dataSource = null;
    private Connection connection = null;

    public void setStation (List<String> stations) throws SQLException {
        String sql = "insert into overall values (?, 0, 0, 0)";
        PreparedStatement preparedStatement = null;
        int index = 0;

        dataSource = dataSourceContext.dataSource();
        connection = dataSource.getConnection();

        while (index < stations.size()) {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, stations.get(index));
            preparedStatement.executeUpdate();
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
