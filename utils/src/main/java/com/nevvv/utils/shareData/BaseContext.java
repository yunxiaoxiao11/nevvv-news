package com.nevvv.utils.shareData;

public class BaseContext {
    private static final ThreadLocal<Long> THREAD_ID = new ThreadLocal<>();

    public static void setThreadId(Long id) {
        THREAD_ID.set(id);
    }

    public static Long getThreadId() {
        return THREAD_ID.get();
    }

    public static void removeThreadId() {
        THREAD_ID.remove();
    }
}
