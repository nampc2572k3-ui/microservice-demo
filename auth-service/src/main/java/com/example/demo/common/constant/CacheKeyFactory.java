package com.example.demo.common.constant;

public class CacheKeyFactory {

    public static final String PREFIX = "microservice:auth:";

    public static final String TOKEN_BLACKLIST_PREFIX = PREFIX + "token:blacklist:";

    public static final String REFRESH_TOKEN_PREFIX = PREFIX + "refresh-token:";

    public static String refreshToken(String token) {
        return REFRESH_TOKEN_PREFIX + token;
    }

}