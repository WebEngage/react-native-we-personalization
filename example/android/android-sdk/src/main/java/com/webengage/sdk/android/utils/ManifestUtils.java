package com.webengage.sdk.android.utils;

import android.content.Context;

public class ManifestUtils {
    public static final String INTERNET = "android.permission.INTERNET";
    public static final String READ_PHONE_STATE = "android.permission.READ_PHONE_STATE";
    public static final String ACCESS_WIFI_STATE = "android.permission.ACCESS_WIFI_STATE";
    public static final String BLUETOOTH = "android.permission.BLUETOOTH";
    public static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    public static final String ACCESS_NETWORK_STATE = "android.permission.ACCESS_NETWORK_STATE";
    public static final String WAKE_LOCK = "android.permission.WAKE_LOCK";
    public static final String GCM_RECEIVE = "com.google.android.c2dm.permission.RECEIVE";
    public static final String VIBRATE = "android.permission.VIBRATE";
    public static final String C2D_MESSAGE = ".permission.C2D_MESSAGE";
    public static final String RECEIVE_BOOT_COMPLETED = "android.permission.RECEIVE_BOOT_COMPLETED";
    public static boolean checkPermission(String permission, Context context) {
        int value = context.getPackageManager().checkPermission(permission, context.getPackageName());
        return value == 0;
    }
}
