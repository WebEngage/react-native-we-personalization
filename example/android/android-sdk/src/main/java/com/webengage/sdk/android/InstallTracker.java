package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InstallTracker extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            WebEngage.get().analytics().installed(intent);
            CallbackDispatcher.init(context.getApplicationContext()).onAppInstalled(context, intent);
        } catch (Exception e) {

        }
    }
}
