package com.example.demo.common.constant;

public class CacheKey {

    private static final String PREFIX = "microservice:profile:";

    public static String getUserProfileKey(String slug) {
        return PREFIX + "userProfile:" + slug;
    }

}
