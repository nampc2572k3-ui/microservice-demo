package com.example.demo.infrastructure.config.cache.redis;

import lombok.Data;

@Data
public class RedisProperties {

    private int port;
    private String host;
    private int timeout;
    private int database;

    private Lettuce lettuce;

    @Data
    public static class Lettuce {
        private Pool pool;
    }

    @Data
    public static class Pool {
        private int maxActive;
        private int maxIdle;
        private int minIdle;
    }
}
