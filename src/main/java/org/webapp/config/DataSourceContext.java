package org.webapp.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class DataSourceContext {

    @Value("${datasource.pllabeldb.driver}")
    private String driverClassName;

    @Value("${datasource.pllabeldb.url}")
    private String url;

    @Value("${datasource.batchjobs.url}")
    private String quartzSchemaUrl;

    @Value("${datasource.pllabeldb.account}")
    private String account;

    @Value("${datasource.pllabeldb.password}")
    private String password;


    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(this.driverClassName);
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.account);
        dataSource.setPassword(this.password);
        dataSource.setDefaultAutoCommit(true);
        return dataSource;
    }

    @Bean
    public DataSource dataSourceForBatchJobs() {
        BasicDataSource batchDataSource = new BasicDataSource();
        batchDataSource.setDriverClassName(this.driverClassName);
        batchDataSource.setUrl(this.quartzSchemaUrl);
        batchDataSource.setUsername(this.account);
        batchDataSource.setPassword(this.password);
        batchDataSource.setDefaultAutoCommit(true);
        return batchDataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean DataSourceTransactionManager transactionManagerForQuartz() {
        return new DataSourceTransactionManager(dataSourceForBatchJobs());
    }

}
