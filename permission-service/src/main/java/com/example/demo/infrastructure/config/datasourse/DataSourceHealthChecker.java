package com.example.demo.infrastructure.config.datasourse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DataSourceHealthChecker {

    private final DataSource slaveDataSource;
    @Getter
    private volatile boolean slaveAlive = true;

    @Scheduled(fixedDelay = 5000)
    public void check() {
        try (Connection conn = slaveDataSource.getConnection()) {
            slaveAlive = conn.isValid(1);
        } catch (Exception e) {
            slaveAlive = false;
        }
    }
}
