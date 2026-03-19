package com.example.demo.common.constant;

public class CacheKeyFactory {

    public static final String PREFIX = "microservice:permission:";

    public static String permissionCheck(String accountId, String path, String method) {
        return String.format(PREFIX + "check:%s:%s:%s", accountId, path, method);
    }

    public static String permissionCheckPattern(String accountId) {
        return String.format(PREFIX + "check:%s:*", accountId);
    }


    public static String menuTree(String accountId) {
        return String.format(PREFIX + "menutree:%s", accountId);
    }


    public static String accountRoles(String accountId) {
        return String.format(PREFIX + "roles:%s", accountId);
    }

}
