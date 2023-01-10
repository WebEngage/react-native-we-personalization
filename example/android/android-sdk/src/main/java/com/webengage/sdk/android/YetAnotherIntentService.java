package com.webengage.sdk.android;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.lang.ref.WeakReference;

class MessageHandler extends Handler {

    private WeakReference<YetAnotherIntentService> mService;

    public MessageHandler(Looper looper, WeakReference<YetAnotherIntentService> mService) {
        super(looper);
        this.mService = mService;
    }

    @Override
    public void handleMessage(Message msg) {
        mService.get().onHandleIntent((Intent) msg.obj);
        switch (msg.what) {
            case WebEngageConstant.BOUND:
                if (msg.replyTo != null) {
                    try {
                        Message reply = Message.obtain();
                        reply.what = WebEngageConstant.FINISH;
                        reply.arg1 = msg.arg1;
                        msg.replyTo.send(reply);
                    } catch (Exception e) {
                        Logger.e(WebEngageConstant.TAG, "YAIS: Exception while replying to remote service", e);
                    }
                }
                break;

            case WebEngageConstant.UNBOUND:
                mService.get().stopSelf(msg.arg1);
                break;
        }
    }
}

class ClientMessageHandler extends Handler {
    WeakReference<MessageHandler> messageHandlerWeakReference = null;

    public ClientMessageHandler(WeakReference<MessageHandler> messageHandlerWeakReference, Looper lopper) {
        super(lopper);
        this.messageHandlerWeakReference = messageHandlerWeakReference;
    }

    @Override
    public void handleMessage(Message msg) {
        Logger.d(WebEngageConstant.TAG, "YAIS: Received bound message service YetAnotherIntentService " +
                " start-id: " + msg.arg1 + ", thread-id: " + Thread.currentThread().getId());
        try {
            // This code will execute in main thread since main looper is given
            Message message = Message.obtain();
            message.copyFrom(msg);
            message.what = WebEngageConstant.BOUND;
            messageHandlerWeakReference.get().sendMessage(message);
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "YAIS: Exception while sending message from Messenger to Service: " + e.toString(), e);
        }
    }
}

abstract class YetAnotherIntentService extends Service {
    private MessageHandler messageHandler = null;
    private HandlerThread handlerThread = null;
    private Messenger clientMessenger = null;
    private ClientMessageHandler clientMessageHandler = null;

    @Override
    public void onCreate() {
        super.onCreate();
        handlerThread = new HandlerThread(this.getClass().getSimpleName());
        handlerThread.start();
        messageHandler = new MessageHandler(handlerThread.getLooper(), new WeakReference<>(this));
        clientMessageHandler = new ClientMessageHandler(new WeakReference<>(messageHandler), Looper.getMainLooper());
        clientMessenger = new Messenger(clientMessageHandler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Logger.d(WebEngageConstant.TAG, "YAIS: Inside serivce onBind: " + clientMessenger.getBinder());
        return clientMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message message = Message.obtain();
        message.arg1 = startId;
        message.obj = intent;
        message.what = WebEngageConstant.UNBOUND;
        messageHandler.sendMessage(message);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    protected abstract void onHandleIntent(Intent intent);
}
