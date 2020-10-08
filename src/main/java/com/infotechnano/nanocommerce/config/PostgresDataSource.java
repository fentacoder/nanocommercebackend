package com.infotechnano.nanocommerce.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:application.yml")
public class PostgresDataSource {

    @Value("${spring.datasource.dbcp2.url}")
    private String dbHost;

    //@Autowired DataSource dataSource;
    //private final DataSource dataSource;

//    @Bean
//    @ConfigurationProperties("app.datasource")
//    public HikariDataSource hikariDataSource(){
//        return DataSourceBuilder
//                .create()
//                .type(HikariDataSource.class)
//                .build();
//    }

    @Bean
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        //"jdbc:postgresql://localhost:5432/nanocommercedb"
        driverManagerDataSource.setUrl("jdbc:postgresql://db:5432/nanocommercedb");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("smartman3");
        return driverManagerDataSource;
    }
}
