package com.mylearn.agentgate.utils;

public class UserIdUtils {
    final static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static String getUserId(){
        return threadLocal.get();
    }

    public static void setUserId(String userId) {
        threadLocal.set(userId);
    }

    public static void removeUserId() {
        threadLocal.remove();
    }
}
