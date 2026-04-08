package com.example.demo.common.cache.key;

public class CacheKeyFactory {

    public static final String PREFIX = "microservice:auth:";

    public static String session(String jti, String accId) {
        return PREFIX + "session:" + jti + ":" + accId;
    }

    public static String blacklistToken(String token) {
        return PREFIX + "blacklist:" + token;
    }

}
