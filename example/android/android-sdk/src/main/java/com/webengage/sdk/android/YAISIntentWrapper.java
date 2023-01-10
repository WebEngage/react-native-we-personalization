package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Intent;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by shahrukhimam on 21/09/18.
 */

class YAISIntentWrapper implements Runnable {

    private boolean finish = false;

    private ScheduledFuture scheduledFuture = null;
    private BroadcastReceiver.PendingResult pendingResult = null;
    private Intent intent  =null;

    private YAISIntentWrapper() {

    }

    YAISIntentWrapper(Intent intent, ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, BroadcastReceiver.PendingResult pendingResult) {
        this.intent = intent;
        this.scheduledFuture = scheduledThreadPoolExecutor.schedule(this, WebEngageConstant.GOASYNC_LIMIT, TimeUnit.MILLISECONDS);
        this.pendingResult = pendingResult;
    }


    public Intent getIntent() {
        return this.intent;
    }

    @Override
    public void run() {
        cancel();
    }

    public synchronized void cancel() {
        if (pendingResult != null && !finish) {
            try {
                this.pendingResult.finish();
                scheduledFuture.cancel(false);
                finish = true;
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, e.toString());
            }
        }
    }
}
