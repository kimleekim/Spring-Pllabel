package org.webapp;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.webapp.config.RootContextConfiguration;
import org.webapp.model.Instaplace;
import org.webapp.model.Overall;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.LocalDate.now;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
@Transactional
public class MainApplicationTest {
    //Dao만 autowired 하기
    @Autowired
    DataSource dataSource;

    @Test
    @Rollback(false)
    public void VOConnection() throws Exception{

        Overall overall = new Overall();
        overall.setStation("영등포");
        overall.setInstaCNT(0);
        overall.setLikeCNT(555);
        overall.setYoutubeCNT(5);
        String sql = "insert into overall values(?, ?, ?, ?)";
        PreparedStatement pstmt;

//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//
//        jdbcTemplate.update(sql, overall.getStation(), overall.getInstaCNT(), overall.getYoutubeCNT(), overall.getLikeCNT());

        Connection connection = dataSource.getConnection();
        pstmt = connection.prepareStatement(sql);

        //pstmt.setString(1, new String(overall.getStation().getBytes("8859_1"), "utf-8"));
        pstmt.setString(1, overall.getStation());
        pstmt.setInt(2, overall.getInstaCNT());
        pstmt.setLong(3, overall.getLikeCNT());
        pstmt.setInt(4, overall.getYoutubeCNT());

        pstmt.executeUpdate();

    }

        
}
