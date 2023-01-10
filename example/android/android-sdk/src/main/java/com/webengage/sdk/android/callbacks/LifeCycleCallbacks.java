package com.webengage.sdk.android.callbacks;


import android.content.Context;
import android.content.Intent;

public interface LifeCycleCallbacks {

    void onGCMRegistered(Context context, final String regID);

    void onGCMMessageReceived(Context context, final Intent intent);

    void onAppInstalled(Context context, final Intent intent);

    void onAppUpgraded(Context context, final int oldVersion, final int newVersion);

    void onNewSessionStarted();
}
