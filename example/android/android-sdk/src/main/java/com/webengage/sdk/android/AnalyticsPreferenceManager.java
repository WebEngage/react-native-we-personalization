package com.webengage.sdk.android;


import android.content.Context;

import com.webengage.sdk.android.actions.database.OnDataHolderChangeListener;
import com.webengage.sdk.android.actions.database.Operation;

import java.util.List;

class AnalyticsPreferenceManager extends BasePreferenceManager implements OnDataHolderChangeListener {

    public AnalyticsPreferenceManager(Context context) {
        super(context);
    }


    public void saveSUID(String suid) {
        saveToPreferences(SUID_KEY, suid);
    }


    public void saveCUID(String cuid) {
        saveToPreferences(CUID_KEY, cuid);
    }


    public void saveLUID(String luid) {
        saveToPreferences(LUID_KEY, luid);
    }


    public void saveInterfaceID(String interfaceID) {
        saveToPreferences(INTERFACE_ID_KEY, interfaceID);
    }

    public void saveVersionCode(int versionCode) {
        saveToPreferences(VERSION_CODE_KEY, versionCode);
    }

    public void saveAppCrashedFlag(boolean flag) {
        saveToPreferences(VOLATILE_PREFS, APP_CRASHED_KEY, flag, true);
    }

    void setInstallReferrer(boolean b) {
        saveToPreferences(INSTALL_REFERRER_SET, b);
    }

    @Override
    public void onChange(List<Object> key, Object value, String userIdentifier, Operation operation) {
    }

}
