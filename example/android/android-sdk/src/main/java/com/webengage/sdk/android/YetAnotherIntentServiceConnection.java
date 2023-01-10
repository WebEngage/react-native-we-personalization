package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ScheduledThreadPoolExecutor;

class YetAnotherIntentServiceConnection implements ServiceConnection {

    private ScheduledThreadPoolExecutor scheduledFutureExecutor = new ScheduledThreadPoolExecutor(1);
    private Messenger boundMessenger = null;
    private Context applicationContext = null;
    private volatile boolean isBound = false;
    private volatile boolean isBinding = false;
    private Queue<YAISIntentWrapper> intentWrappers = null;
    private Intent connectingIntent = null;
    private int startId = 0;
    private int latestStartId = 0;
    private Map<Integer, YAISIntentWrapper> intentWrapperMap = null;
    private String serviceName = null;
    private final Object lock = new Object();

    private class IncomingHandler extends Handler {

        public IncomingHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // This code will execute in main thread since main looper is given
            switch (msg.what) {
                case WebEngageConstant.FINISH:
                    synchronized (lock) {
                        int startId = msg.arg1;
                        Logger.d(WebEngageConstant.TAG, "YAIS: processing complete, service: " + serviceName + ", startId: " + startId + ", Thread id: " + getLooper().getThread().getId());
                        try {
                            if (intentWrapperMap.get(startId) != null) {
                                intentWrapperMap.get(startId).cancel();
                                intentWrapperMap.remove(startId);
                            }
                            if (startId == latestStartId) {
                                Logger.d(WebEngageConstant.TAG, "YAIS: disconnecting from service: " + serviceName);
                                YetAnotherIntentServiceConnection.this.applicationContext.unbindService(YetAnotherIntentServiceConnection.this);
                                isBound = false;
                            }
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "YAIS: Some exception occurred while handling incoming messages from service: "+serviceName, e);
                        }
                    }
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final IncomingHandler incomingHandler = new IncomingHandler(Looper.getMainLooper());
    private Messenger incomingMessenger = new Messenger(incomingHandler);

    private YetAnotherIntentServiceConnection() {

    }

    public YetAnotherIntentServiceConnection(Context context, Intent connectingIntent) {
        this.applicationContext = context.getApplicationContext();
        this.intentWrappers = new ArrayDeque<>();
        this.connectingIntent = connectingIntent;
        this.intentWrapperMap = new HashMap<>();
        this.serviceName = connectingIntent.getComponent().getClassName();
    }

    public synchronized void submit(Intent intent, BroadcastReceiver.PendingResult pendingResult) {
        Logger.d(WebEngageConstant.TAG, "YAIS: Adding task to service: " + serviceName);
        this.intentWrappers.add(new YAISIntentWrapper(intent, scheduledFutureExecutor, pendingResult));
        this.loopTillEndOfHumanity();
    }

    private synchronized void loopTillEndOfHumanity() {
        while (!this.intentWrappers.isEmpty()) {
            if (isBound && boundMessenger != null && boundMessenger.getBinder().isBinderAlive()) {
                YAISIntentWrapper yaisIntentWrapper = this.intentWrappers.poll();
                sendMessage(yaisIntentWrapper);
            } else {
                if (!isBinding) {
                    isBinding = true;
                    try {
                        this.applicationContext.bindService(connectingIntent, this, Context.BIND_AUTO_CREATE);
                        return;
                    } catch (Exception e) {
                        Logger.e(WebEngageConstant.TAG, "YAIS: Exception while binding to service: " + connectingIntent, e);
                    }
                    isBinding = false;
                    consumeAllIntents();
                    return;
                } else {
                    return;
                }
            }
        }
    }

    private synchronized void consumeAllIntents() {
        while (!this.intentWrappers.isEmpty()) {
            this.intentWrappers.poll().cancel();
        }
    }

    private void sendMessage(YAISIntentWrapper yaisIntentWrapper) {
        synchronized (lock) {
            Logger.d(WebEngageConstant.TAG, "YAIS: sending message to service: " + serviceName + ", startId: " + startId);
            Message message = Message.obtain();
            message.obj = yaisIntentWrapper.getIntent();
            message.arg1 = startId;
            message.replyTo = incomingMessenger;
            try {
                this.boundMessenger.send(message);
                this.intentWrapperMap.put(startId, yaisIntentWrapper);
                latestStartId = startId;
                startId += 1;
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, "YAIS: Exception while sending message to service: " + yaisIntentWrapper.getIntent(), e);
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        synchronized (lock) {
            if (iBinder != null) {
                Logger.d(WebEngageConstant.TAG, "YAIS: service connected: " + serviceName);
                boundMessenger = new Messenger(iBinder);
                isBound = true;
                isBinding = false;
                loopTillEndOfHumanity();
            } else {
                consumeAllIntents();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        synchronized (lock) {
            Logger.d(WebEngageConstant.TAG, "YAIS: service disconnected: " + serviceName);
            isBound = false;
            boundMessenger = null;
            loopTillEndOfHumanity();
        }
    }
}
