package com.mylearn.agentgate.utils;

public class HistoryIdUtils {
    private static final ThreadLocal<Long> historyIdThreadLocal = new ThreadLocal<>();

    public static void set(Long historyId) {
        historyIdThreadLocal.set(historyId);
    }

    public static Long get() {
        return historyIdThreadLocal.get();
    }

    public static void remove() {
        historyIdThreadLocal.remove();
    }
}
