package org.webapp;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webapp.config.RootContextConfiguration;
import org.webapp.model.Instafood;
import org.webapp.model.Instahot;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
public class VOtest {

    @Autowired
    DataSource dataSource;
    private Instafood instafood = new Instafood();
    private Instahot instahot = new Instahot();
    private Connection connection = null;

//  DB 연결
    public void connectionTest(Connection connection) throws Exception {
        connection = dataSource.getConnection();
        Assert.assertNotNull(connection);
    }

//    instafood의 set함수 test
    @Test
    public void setTest() throws Exception {
        instafood.setKey(15);
        instafood.setStation("sookmyung");
        instafood.setPost("숙대 맛집 탐방2");
        instafood.setPhotoURL("DFAFDSFS31DFA");
        instafood.setDate(java.sql.Date.valueOf("2019-08-05"));
        getTest(instafood);
    }

//    instafood의 get함수 test
    public void getTest(Instafood instafood) throws Exception {
        PreparedStatement preparedStatement = null;
        String sql = "insert into instafood values(?, ?, ?, ?, ?)";

//        connectionTest(connection);
        connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, instafood.getKey());
        preparedStatement.setString(2, instafood.getStation());
        preparedStatement.setString(3, instafood.getPost());
        preparedStatement.setString(4, instafood.getPhotoURL());
        preparedStatement.setDate(5, instafood.getDate());
        preparedStatement.executeUpdate();
        connection.commit();
    }
}
