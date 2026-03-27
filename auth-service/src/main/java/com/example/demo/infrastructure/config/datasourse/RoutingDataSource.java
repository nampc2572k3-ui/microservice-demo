package com.example.demo.infrastructure.config.datasourse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Random;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private final DataSourceHealthChecker healthChecker;
    private final List<String> slaves;
    private final Random random = new Random();

    public RoutingDataSource(DataSourceHealthChecker healthChecker, List<String> slaves) {
        this.healthChecker = healthChecker;
        this.slaves = slaves;
    }

    @Override
    protected Object determineCurrentLookupKey() {

        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (!isReadOnly) {
            return "MASTER";
        }

        if (healthChecker.isSlaveAlive()) {
            String slave = slaves.get(random.nextInt(slaves.size()));
            log.info("Routing to SLAVE: {}", slave);
            return slave;
        }

        log.warn("Slave down → fallback MASTER");
        return "MASTER";
    }
}
