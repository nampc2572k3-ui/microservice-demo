package com.example.demo.core.config.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.TransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.util.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class Neo4jMigrationRunner {
    private static final Logger log = LoggerFactory.getLogger(Neo4jMigrationRunner.class);
    private final Driver driver;
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    @Autowired
    public Neo4jMigrationRunner(Driver driver) {
        this.driver = driver;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runMigrations() {
        try {
            Resource[] resources = resolver.getResources("classpath:neo4j/migration/*.cypher");
            if (resources.length == 0) {
                log.info("No cypher migration files found in classpath:neo4j/migration");
                return;
            }

            Arrays.sort(resources, (a, b) -> a.getFilename().compareTo(b.getFilename()));

            for (Resource r : resources) {
                String name = r.getFilename();
                log.info("Applying migration: {}", name);
                String content = StreamUtils.copyToString(r.getInputStream(), StandardCharsets.UTF_8);
                String[] statements = content.split(";");
                try (Session session = driver.session()) {
                    for (String raw : statements) {
                        String stmt = raw.trim();
                        if (stmt.isEmpty()) continue;
                        try {
                            session.executeWrite((TransactionContext tx) -> {
                                tx.run(stmt);
                                return null;
                            });
                            log.debug("Executed statement from {}: {}", name, stmt.replaceAll("\\s+", " "));
                        } catch (Exception ex) {
                            log.error("Failed to execute statement in {}: {} -> {}", name, stmt, ex.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("Failed to load cypher migration files: {}", e.getMessage(), e);
        }
    }
}
