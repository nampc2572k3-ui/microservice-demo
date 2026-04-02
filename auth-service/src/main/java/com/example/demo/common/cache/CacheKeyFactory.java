package com.example.demo.common.cache;

public class CacheKeyFactory {

    public static final String PREFIX = "microservice:auth:";


    // build token key
    public static final String TOKEN_BLACKLIST_PREFIX = PREFIX + "token:blacklist:";

    public static final String SESSION_PREFIX = PREFIX + "session:";

    public static String buildSessionKey(String token) {
        return SESSION_PREFIX + token;
    }



    // build login key for prevent spam login
    public static final String LOGIN_PREFIX = PREFIX + "login:";

    public static String buildIpKey(String ip) {
        return LOGIN_PREFIX + ip;
    }

    public static String buildUserKey(String username) {
        return LOGIN_PREFIX + username;
    }

    public static String buildIpUserKey(String ip, String username) {
        return LOGIN_PREFIX + ip + ":" + username;
    }



}