package com.webengage.sdk.android.actions.database;


import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.UserDeviceAttribute;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.exception.AdvertisingIdException;
import com.webengage.sdk.android.utils.OptHashMap;
import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class SystemDataFactory {
    private Context applicationContext = null;

    SystemDataFactory(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    Map<String, Object> generateNewSessionData() {
        Map<String, Object> map = new HashMap<String, Object>();
        PackageInfo pi = WebEngageUtils.getPackageInfo(this.applicationContext);
        map.put("status_airplane_mode", SystemDataPointHelper.getAirplaneModeStatus(this.applicationContext));
        map.put("status_nfc", SystemDataPointHelper.getNFCStatus(this.applicationContext));
        map.put("status_wifi", SystemDataPointHelper.getWifiStatus(this.applicationContext));
        map.put("status_bluetooth", SystemDataPointHelper.getBluetoothStatus(this.applicationContext));
        map.put("status_gps", SystemDataPointHelper.getGPSStatus(this.applicationContext));
        map.put(UserDeviceAttribute.OPT_IN_PUSH, checkForPushOptIn());
        String GAID = WebEngageUtils.fetchGAID(applicationContext);
        if (!GAID.isEmpty()) {
            map.put("advertising_id", GAID);
        }
        if (pi != null) {
            map.put("app_installed_on", new Date(pi.firstInstallTime));
        }
        map.put("viewport_height", getDisplayMetrics().heightPixels);
        map.put("viewport_width", getDisplayMetrics().widthPixels);
        map.put("language", Locale.getDefault().getDisplayLanguage(Locale.US));
        String carrier = SystemDataPointHelper.getCarrier(this.applicationContext);
        if (carrier != null && !carrier.isEmpty()) {
            map.put("carrier", carrier.toUpperCase());
        }
        String carrierType = SystemDataPointHelper.getCarrierType(this.applicationContext);
        if (carrierType != null && !carrierType.isEmpty() && !carrierType.equalsIgnoreCase("UNKNOWN")) {
            map.put("carrier_type", carrierType);
        }
        map.put("model", Build.MODEL);
        map.put("brand", Build.BRAND);
        map.put("device", Build.DEVICE);
        map.put("manufacturer", Build.MANUFACTURER);
        map.put("release", Build.VERSION.RELEASE);
        map.put("api_version", Build.VERSION.SDK_INT);
        map.put("os_name", BuildConfig.SDK_PLATFORM);
        map.put("os_version", Build.VERSION.RELEASE);
        map.put("device_type", deviceType());
        map.put("locale", Locale.getDefault().toString());
        map.put("time_zone", WebEngageUtils.getTimezone());
        map.put("tz_name", WebEngageUtils.getTimezoneId());
        if (pi != null) {
            map.put("app_version", pi.versionName);
            map.put("app_version_code", pi.versionCode);
        }

        return map;
    }


    Map<String, Object> generateSystemData(Map<String, Object> systemData, boolean isNewSession) {
        PackageInfo pi = WebEngageUtils.getPackageInfo(this.applicationContext);
        OptHashMap<String, Object> optHashMap = new OptHashMap<String, Object>();
        if (systemData != null) {
            optHashMap.putOptAll(systemData);
        }
        if (isNewSession) {
            optHashMap.putOpt("status_airplane_mode", DataHolder.get().getDeviceData("status_airplane_mode"));
            optHashMap.putOpt("status_nfc", DataHolder.get().getDeviceData("status_nfc"));
            optHashMap.putOpt("status_wifi", DataHolder.get().getDeviceData("status_wifi"));
            optHashMap.putOpt("status_bluetooth", DataHolder.get().getDeviceData("status_bluetooth"));
            optHashMap.putOpt("status_gps", DataHolder.get().getDeviceData("status_gps"));
            optHashMap.putOpt("app_installed_on", DataHolder.get().getDeviceData("app_installed_on"));
            optHashMap.putOpt("android_id", DataHolder.get().getDeviceData("android_id"));
            optHashMap.putOpt("advertising_id", DataHolder.get().getDeviceData("advertising_id"));
            optHashMap.put("tz_name", DataHolder.get().getDeviceData("tz_name"));
            optHashMap.putOpt(UserDeviceAttribute.OPT_IN_PUSH, DataHolder.get().getDeviceData(UserDeviceAttribute.OPT_IN_PUSH));
        }
        optHashMap.putOpt(UserDeviceAttribute.LATITUDE.toString(), DataHolder.get().getLatitude());
        optHashMap.putOpt(UserDeviceAttribute.LONGITUDE.toString(), DataHolder.get().getLongitude());
        optHashMap.putOpt(UserDeviceAttribute.CITY.toString(), DataHolder.get().getCity());
        optHashMap.putOpt(UserDeviceAttribute.COUNTRY.toString(), DataHolder.get().getCountry());
        optHashMap.putOpt(UserDeviceAttribute.REGION.toString(), DataHolder.get().getRegion());
        optHashMap.putOpt(UserDeviceAttribute.LOCALITY.toString(), DataHolder.get().getLocality());
        optHashMap.putOpt(UserDeviceAttribute.POSTAL_CODE.toString(), DataHolder.get().getPostalCode());

        optHashMap.putDefault("total_page_view_count", DataHolder.get().getTotalPageViewCount(), 0L);
        optHashMap.putDefault("page_view_count_session", DataHolder.get().getSessionPageViewCount(), 0L);

        if ("online".equalsIgnoreCase(DataHolder.get().getLatestSessionType())) {
            optHashMap.putOpt("session_type", "online");
            optHashMap.putDefault("session_count", DataHolder.get().getForegroundSessionCount(), 0L);
            optHashMap.putOpt("screen_name", DataHolder.get().getScreenName());
            optHashMap.putOpt("screen_title", DataHolder.get().getScreenTitle());
            optHashMap.putOpt("screen_path", DataHolder.get().getScreenPath());
        } else {
            optHashMap.putOpt("session_type", "background");
            optHashMap.putDefault("session_count", DataHolder.get().getBackgroundSessionCount(), 0L);
        }
        optHashMap.putOpt("viewport_height", DataHolder.get().getDeviceData("viewport_height"));
        optHashMap.putOpt("viewport_width", DataHolder.get().getDeviceData("viewport_width"));
        optHashMap.putOpt("language", DataHolder.get().getDeviceData("language"));
        optHashMap.putOpt("carrier", DataHolder.get().getDeviceData("carrier"));
        optHashMap.putOpt("carrier_type", DataHolder.get().getDeviceData("carrier_type"));
        optHashMap.putOpt("model", DataHolder.get().getDeviceData("model"));
        optHashMap.putOpt("brand", DataHolder.get().getDeviceData("brand"));
        optHashMap.putOpt("device", DataHolder.get().getDeviceData("device"));
        optHashMap.putOpt("manufacturer", DataHolder.get().getDeviceData("manufacturer"));
        optHashMap.putOpt("release", DataHolder.get().getDeviceData("release"));
        optHashMap.putOpt("api_version", DataHolder.get().getDeviceData("api_version"));
        optHashMap.putOpt("os_name", DataHolder.get().getDeviceData("os_name"));
        optHashMap.putOpt("os_version", DataHolder.get().getDeviceData("os_version"));
        optHashMap.putOpt("device_type", DataHolder.get().getDeviceData("device_type"));
        optHashMap.putOpt("locale", DataHolder.get().getDeviceData("locale"));
        optHashMap.putOpt("time_zone", DataHolder.get().getDeviceData("time_zone"));
        optHashMap.putOpt("tzo", DataHolder.get().getTZO());
        optHashMap.putOpt("app_version", DataHolder.get().getDeviceData("app_version"));
        optHashMap.putOpt("app_version_code", DataHolder.get().getDeviceData("app_version_code"));
        return optHashMap;
    }


    Map<String, Object> getLocationAddress(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return null;
        }
        Geocoder geocoder = new Geocoder(this.applicationContext, Locale.ENGLISH);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address address = addresses.get(0);

            Map<String, Object> addressMap = new HashMap<String, Object>();
            if (address != null) {
                if (address.getLocality() != null && !address.getLocality().isEmpty()) {
                    addressMap.put(UserDeviceAttribute.CITY.toString(), address.getLocality());
                }
                if (address.getCountryName() != null && !address.getCountryName().isEmpty()) {
                    addressMap.put(UserDeviceAttribute.COUNTRY.toString(), address.getCountryName());
                }
                if (address.getAdminArea() != null && !address.getAdminArea().isEmpty()) {
                    addressMap.put(UserDeviceAttribute.REGION.toString(), address.getAdminArea());
                }
                if (address.getPostalCode() != null && !address.getPostalCode().isEmpty()) {
                    addressMap.put(UserDeviceAttribute.POSTAL_CODE.toString(), address.getPostalCode());
                }
                if (address.getFeatureName() != null && !address.getFeatureName().isEmpty()) {
                    addressMap.put(UserDeviceAttribute.LOCALITY.toString(), address.getSubLocality());
                }
            }
            return addressMap;
        } catch (Exception e) {
            return null;
        }

    }


    DisplayMetrics getDisplayMetrics() {
        WindowManager windowManager = (WindowManager) this.applicationContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;

    }

    String deviceType() {
        try {
            DisplayMetrics metrics = getDisplayMetrics();
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            double diagonalInches = Math.sqrt(
                    (widthPixels * widthPixels)
                            + (heightPixels * heightPixels)) / metrics.densityDpi;

            if (diagonalInches < 7) {
                return WebEngageConstant.DEVICE_TYPE_MOBILE;
            } else {
                return WebEngageConstant.DEVICE_TYPE_TABLET;
            }
        } catch (Exception e) {
        }


        return WebEngageConstant.DEVICE_TYPE_DEFAULT;
    }


    private boolean checkForPushOptIn() {
        try {
            if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 24) {
                AppOpsManager appOps = (AppOpsManager) applicationContext.getSystemService(Context.APP_OPS_SERVICE);
                ApplicationInfo appInfo = applicationContext.getApplicationInfo();
                String pkg = applicationContext.getApplicationContext().getPackageName();
                int uid = appInfo.uid;
                try {
                    Class<?> appOpsClass = Class.forName(AppOpsManager.class.getName());
                    Method checkOpNoThrowMethod = appOpsClass.getMethod("checkOpNoThrow", Integer.TYPE,
                            Integer.TYPE, String.class);
                    Field opPostNotificationValue = appOpsClass.getDeclaredField("OP_POST_NOTIFICATION");
                    int value = (int) opPostNotificationValue.get(Integer.class);
                    return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg)
                            == AppOpsManager.MODE_ALLOWED);
                } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException |
                        InvocationTargetException | IllegalAccessException | RuntimeException e) {
                    return true;
                }

            } else if (Build.VERSION.SDK_INT >= 24) {
                NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                return notificationManager.areNotificationsEnabled();
            }
        } catch (Exception e) {

        }
        return true;
    }
}

