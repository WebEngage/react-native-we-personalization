package com.webengage.sdk.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

class YetAnotherIntentServiceManager {
    private static Map<String, YetAnotherIntentServiceConnection> serviceConnectionMap = new HashMap<>();

    public static void startService(Context context, Intent intent, BroadcastReceiver broadcastReceiver) {
        try {
            context.getApplicationContext().startService(intent);
        } catch (IllegalStateException e) {
            Logger.e(WebEngageConstant.TAG, "Exception while starting service: " + e.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    connect(context, intent, broadcastReceiver);
                } catch (Exception connectException) {
                    Logger.e(WebEngageConstant.TAG, "Exception while connecting to service: " + intent);
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception: " + e.toString());
        }
    }

    private static void connect(Context context, Intent intent, BroadcastReceiver broadcastReceiver) {
        BroadcastReceiver.PendingResult pendingResult = null;
        Logger.d(WebEngageConstant.TAG, "connect called");
        if (broadcastReceiver != null) {
            pendingResult = broadcastReceiver.goAsync();
        }

        YetAnotherIntentServiceConnection serviceConnection = getYetAnotherIntentServiceConnection(context.getApplicationContext(), intent);
        if (serviceConnection != null) {
            serviceConnection.submit(intent, pendingResult);
        }
    }

    private static synchronized YetAnotherIntentServiceConnection getYetAnotherIntentServiceConnection(Context context, Intent intent) {
        ResolveInfo resolveInfo = context.getApplicationContext().getPackageManager().resolveService(intent, 0);
        if (resolveInfo != null) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo != null) {
                String serviceName = serviceInfo.name;
                if (serviceConnectionMap.get(serviceName) == null) {
                    serviceConnectionMap.put(serviceName, new YetAnotherIntentServiceConnection(context, intent));
                }
                return serviceConnectionMap.get(serviceName);
            }
        }
        return null;
    }
}
