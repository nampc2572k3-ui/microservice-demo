package com.example.demo.core.config.cache.redis;

import lombok.Data;

@Data
public class RedisProperties {

    private int port;
    private String host;
    private int timeout;
    private int database;

}