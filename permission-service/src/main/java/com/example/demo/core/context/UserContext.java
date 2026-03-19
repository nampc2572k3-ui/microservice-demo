package com.example.demo.core.context;

public class UserContext {

    private static final ThreadLocal<String> usernameHolder = new ThreadLocal<>();

    public static void set(String username) {
        usernameHolder.set(username);
    }

    public static String getUsername() {
        return usernameHolder.get();
    }

    public static void clear() {
        usernameHolder.remove();
    }
}
