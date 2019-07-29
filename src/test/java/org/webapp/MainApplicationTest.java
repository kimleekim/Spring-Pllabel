package org.webapp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webapp.config.RootContextConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RootContextConfiguration.class)
public class MainApplicationTest {

    @Autowired
    DataSource dataSource;

    @Test
    public void connectionTest() throws Exception {
        Connection connection = dataSource.getConnection();
        Assert.assertNotNull(connection);
    }
}
