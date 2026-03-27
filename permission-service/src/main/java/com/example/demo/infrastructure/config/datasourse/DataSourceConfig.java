package com.example.demo.infrastructure.config.datasourse;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.master")
    public DataSource masterDataSource() {
        return new HikariDataSource();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.slave")
    public DataSource slaveDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public DataSource routingDataSource(
            DataSource masterDataSource,
            DataSource slaveDataSource,
            DataSourceHealthChecker checker
    ) {

        Map<Object, Object> map = new HashMap<>();
        map.put("MASTER", masterDataSource);
        map.put("SLAVE1", slaveDataSource);

        RoutingDataSource routing = new RoutingDataSource(
                checker,
                List.of("SLAVE1")
        );

        routing.setTargetDataSources(map);
        routing.setDefaultTargetDataSource(masterDataSource);

        return routing;
    }

    @Primary
    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
