package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

/**
 * Created by shahrukhimam on 09/10/17.
 */

public class WebEngagePushReceiver extends BroadcastReceiver {

    protected static final String GCM_MESSAGE_ACTION = "com.google.android.c2dm.intent.RECEIVE";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            Logger.d(WebEngageConstant.TAG, "WebEngagePushReceiver received intent with action : " + action);
            if (GCM_MESSAGE_ACTION.equals(action)) {
                if (!ReflectionUtils.isGoogleCloudMessagingDependencyAdded()) {
                    Logger.e(WebEngageConstant.TAG, "Google Play Services library missing");
                    return;
                }
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    WebEngage.get().dispatchGCMMessage(intent);
                }
                CallbackDispatcher.init(context.getApplicationContext()).onGCMMessageReceived(context, intent);
            } else {

            }

        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, e.toString());
        }
    }
}
