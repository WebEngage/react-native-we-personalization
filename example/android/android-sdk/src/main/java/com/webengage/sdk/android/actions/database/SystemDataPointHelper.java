package com.webengage.sdk.android.actions.database;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.webengage.sdk.android.utils.ManifestUtils;

class SystemDataPointHelper {


    public static String getCarrier(Context context) {
        if (ManifestUtils.checkPermission(ManifestUtils.ACCESS_NETWORK_STATE, context)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkOperatorName();
        }
        return null;
    }

    public static String getCarrierType(Context context) {
        if (ManifestUtils.checkPermission(ManifestUtils.ACCESS_NETWORK_STATE, context)
                && ManifestUtils.checkPermission(ManifestUtils.READ_PHONE_STATE, context)) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return getNetworkTypeString(telephonyManager.getDataNetworkType());
            }else{
                return getNetworkTypeString(telephonyManager.getNetworkType());
            }
        }
        return null;
    }


    public static boolean getAirplaneModeStatus(Context context) {
        try {
            if (Build.VERSION.SDK_INT < 17) {
                return Settings.System.getInt(context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0) != 0;
            } else {
                return Settings.Global.getInt(context.getContentResolver(),
                        Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getNFCStatus(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= 10) {
                NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
                NfcAdapter adapter = manager.getDefaultAdapter();
                if (adapter != null && adapter.isEnabled()) {
                    return true;
                }
            }
        } catch (Exception e) {

        }
        return false;
    }

    public static Boolean getWifiStatus(Context context) {
        if (ManifestUtils.checkPermission(ManifestUtils.ACCESS_WIFI_STATE, context)) {
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiInfo.isConnected();
        }
        return null;
    }

    public static Boolean getBluetoothStatus(Context context) {
        if (ManifestUtils.checkPermission(ManifestUtils.BLUETOOTH, context)) {
            final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return (bluetoothAdapter != null && bluetoothAdapter.isEnabled());
        }
        return null;
    }

    public static Boolean getGPSStatus(Context context) {
        if (ManifestUtils.checkPermission(ManifestUtils.ACCESS_FINE_LOCATION, context)) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return null;
    }

    private static String getNetworkTypeString(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO_A";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";

            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "IDEN";

            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN";
        }
        return "UNKNOWN";
    }


}
