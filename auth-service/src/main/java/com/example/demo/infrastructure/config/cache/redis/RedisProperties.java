package com.example.demo.infrastructure.config.cache.redis;

import lombok.Data;

import java.util.List;

@Data
public class RedisProperties {

    private int timeout;
    private int database;

    private Cluster cluster;
    private Lettuce lettuce;

    @Data
    public static class Cluster {
        private List<String> nodes;
        private int maxRedirects;
    }

    @Data
    public static class Lettuce {
        private Pool pool;
        private ClusterRefresh cluster;

        @Data
        public static class ClusterRefresh {
            private boolean adaptive;
            private String period;
        }
    }

    @Data
    public static class Pool {
        private int maxActive;
        private int maxIdle;
        private int minIdle;
        private String maxWait;
    }
}
