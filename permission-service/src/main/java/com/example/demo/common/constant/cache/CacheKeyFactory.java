package com.example.demo.common.constant.cache;

public class CacheKeyFactory {

    public static final String PREFIX = "microservice:permission:";

    public static String version(String accId) {
        return PREFIX + "version:" + accId;
    }

    public static String refreshLock(String accId) {
        return PREFIX + "refresh:lock:" + accId;
    }

    public static String permissionCheck(String accId, String version, String path, String method) {
        return String.format(PREFIX + "check:%s:%s:%s:%s", accId, version, path, method);
    }

    public static String menuTree(String accId, String version) {
        return String.format(PREFIX + "menutree:%s:%s", accId, version);
    }

    public static String accountRoles(String accId, String version) {
        return String.format(PREFIX + "roles:%s:%s", accId, version);
    }
}
