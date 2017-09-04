package com.letv.mobile.core.utils;


/**
 * 开启线程
 */
public class ThreadUtils {

    /**
     * start run sth in thread pool
     * @param doSthRunnable
     */
    public static void startRunInThread(Runnable doSthRunnable) {
        GlobalJsonThreadPool.startRunInThread(doSthRunnable);
    }

    public static void startRunInSinleThreadPool(Runnable doSthRunnable) {
        GlobalSingleThreadPool.startRunInSingleThreadPool(doSthRunnable);
    }

    public static void startRunInSingleThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void startRunInThreadForClearQueue(Runnable doSthRunnable) {
        GlobalSingleThread.getInstance(ContextProvider.getApplicationContext())
                .startRunAndClearQueue(doSthRunnable);
    }

}
