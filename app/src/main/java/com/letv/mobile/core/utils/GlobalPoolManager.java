package com.letv.mobile.core.utils;

public class GlobalPoolManager {

    public static void shutDownPool() {
        GlobalJsonThreadPool.shutdownPool();
    }

}
