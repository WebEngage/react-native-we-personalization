package com.webengage.sdk.android.utils;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutor {

    public static ThreadExecutor instance = new ThreadExecutor();
    private ExecutorService executorService;

    private ThreadExecutor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public static ThreadExecutor get() {
        return instance;
    }

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void shutDown(){
        executorService.shutdownNow();
    }
}
