package com.webengage.sdk.android;


import android.content.Context;
import android.content.SharedPreferences;

import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class BasePreferenceManager {
    public Context applicationContext = null;
    public static final String DEFAULT_PREFS = "webengage_prefs.txt";
    public static final String VOLATILE_PREFS = "webengage_volatile_prefs.txt";
    public static final String PERSISTENT_PREFS = "webengage_persistent_prefs.txt";

    public static final String REG_ID_KEY = "com.webengage.static.regID";
    public static final String REG_ID_KEY_MI = "com.webengage.static.mIregID";
    public static final String REG_ID_KEY_HW = "com.webengage.static.hwRegID";

    public static final String CUID_KEY = "com.webengage.static.cuid";

    public static final String INTERFACE_ID_KEY = "com.webengage.static.interfaceID";
    public static final String SUID_KEY = "com.webengage.session.suid";
    public static final String LUID_KEY = "com.webengage.static.luid";
    public static final String VERSION_CODE_KEY = "com.webengage.static.version_code";
    public static final String DEVICE_TYPE_KEY = "com.webengage.static.deviceType";
    public static final String SESSION_EVALUATED_IDS = "com.webengage.session.evaluatedIds";
    public static final String VARIATION_MAP_KEY = "com.webengage.session.variations";
    public static final String APP_CRASHED_KEY = "com.webengage.static.app_crashed";
    public static final String ONLINE_SESSION_CREATION_TIMESTAMP = "com.webengage.session.create_time";
    private static final String TS_SEPARATOR = " ";
    private static final String PUSH_SHOWN = "push_shown";
    private static final String AMPLIFY_INTERVAL = "amplify_interval";
    static final String INSTALL_REFERRER_SET = "install_referrer_set";
    private static final String SESSION_DESTROY_TIME = "session_destroy_time";
    private static final List<String> PERSISTED_KEYS = new ArrayList<>();
    static {
        PERSISTED_KEYS.add(INTERFACE_ID_KEY);
    }
    protected BasePreferenceManager(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    public SharedPreferences getPreferenceFile(String fileName) {
        return this.applicationContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public boolean clear(String filename) {
        SharedPreferences preferences = getPreferenceFile(filename);
        return preferences.edit().clear().commit();
    }

    public boolean saveToPreferences(String fileName, String key, Object value, boolean shouldOverride) {
        SharedPreferences preferences = getPreferenceFile(fileName);

        if (preferences != null) {
            if (!shouldOverride) {
                if (preferences.contains(key)) {
                    return true;
                }
            }

            SharedPreferences.Editor edit = preferences.edit();
            if (value instanceof String) {
                edit.putString(key, String.valueOf(value));
            } else if (value instanceof Integer) {
                edit.putInt(key, (Integer) value);
            } else if (value instanceof Float) {
                edit.putFloat(key, (Float) value);
            } else if (value instanceof Boolean) {
                edit.putBoolean(key, (Boolean) value);
            } else if (value instanceof Long) {
                edit.putLong(key, (Long) value);
            } else if (value instanceof Set) {
                if (android.os.Build.VERSION.SDK_INT >= 11) {
                    edit.putStringSet(key, (Set<String>) value);
                } else {
                    return false;
                }
            }
            edit.apply();

            return true;
        } else {
            return false;
        }
    }

    public boolean saveToPreferences(String key, Object value, boolean shouldOverride) {
        if (PERSISTED_KEYS.contains(key)) {
            return this.saveToPreferences(BasePreferenceManager.PERSISTENT_PREFS,
                    key, value, shouldOverride);
        }
        return this.saveToPreferences(BasePreferenceManager.DEFAULT_PREFS, key, value, shouldOverride);
    }

    public boolean saveToPreferences(String key, Object value) {
        return saveToPreferences(key, value, true);
    }

    public boolean saveToPreferences(String fileName, String key, Object value) {
        return this.saveToPreferences(fileName, key, value, true);
    }


    public void saveSessionEvaluatedIds(List<String> evaluatedIds) {
        try {
            saveToPreferences(SESSION_EVALUATED_IDS, DataType.convert(evaluatedIds, DataType.STRING, false));
        } catch (Exception e) {

        }
    }

    public List<String> getSessionEvaluatedIds() {
        String str = getPreferenceFile(DEFAULT_PREFS).getString(SESSION_EVALUATED_IDS, "");
        try {
            return (List) DataType.convert(str, DataType.LIST, false);
        } catch (Exception e) {
            return null;
        }
    }


    public void saveVariationMap(Map<String, Map<String, String>> variationMap) {
        try {
            saveToPreferences(VARIATION_MAP_KEY, DataType.convert(variationMap, DataType.STRING, false));
        } catch (Exception e) {

        }
    }

    public Map<String, Map<String, String>> getVariationMap() {
        String str = getPreferenceFile(DEFAULT_PREFS).getString(VARIATION_MAP_KEY, "");
        try {
            return (Map) DataType.convert(str, DataType.MAP, false);
        } catch (Exception e) {
            return null;
        }
    }


    public void saveRegistrationID(String regID) {
        Logger.d(WebEngageConstant.TAG, "Push token: " + regID);
        saveToPreferences(REG_ID_KEY, regID);
    }

    public String getRegistrationID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(REG_ID_KEY, "");
    }

    public void saveXiaomiRegistrationID(String regID) {
        Logger.d(WebEngageConstant.TAG, "MI token: " + regID);
        saveToPreferences(REG_ID_KEY_MI, regID);
    }

    public String getXiaomiRegistrationID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(REG_ID_KEY_MI, "");
    }

    public void saveHuaweiRegistrationID(String regID) {
        Logger.d(WebEngageConstant.TAG, "HW token: " + regID);
        saveToPreferences(REG_ID_KEY_HW, regID);
    }

    public String getHuaweiRegistrationID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(REG_ID_KEY_HW, "");
    }

    public int getVersionCode() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getInt(VERSION_CODE_KEY, -1);
    }


    public String getInterfaceID() {
        SharedPreferences preferences = getPreferenceFile(PERSISTENT_PREFS);
        String interfaceId = preferences.getString(INTERFACE_ID_KEY, "");
        if (interfaceId.isEmpty()) {
            preferences = getPreferenceFile(DEFAULT_PREFS);
            interfaceId = preferences.getString(INTERFACE_ID_KEY, "");
        }

        return interfaceId;
    }


    public void removeVolatileData(String key) {
        SharedPreferences preferences = getPreferenceFile(VOLATILE_PREFS);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();

    }

    public void saveVolatileData(String key, String data) {
        saveToPreferences(VOLATILE_PREFS, key, data, true);
    }


    public String getVolatileData(String key) {
        SharedPreferences preferences = getPreferenceFile(VOLATILE_PREFS);
        return preferences.getString(key, "");
    }

    public boolean getAppCrashedFlag() {
        SharedPreferences preferences = getPreferenceFile(VOLATILE_PREFS);
        return preferences.getBoolean(APP_CRASHED_KEY, false);
    }


    boolean isInstallReferrerSet() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getBoolean(INSTALL_REFERRER_SET, true);
    }

    public String getSUID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(SUID_KEY, "");
    }

    public String getCUID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(CUID_KEY, "");
    }


    public String getLUID() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(LUID_KEY, "");
    }

    public void saveDeviceType(String deviceType) {
        saveToPreferences(DEVICE_TYPE_KEY, deviceType);
    }

    public String getDeviceType() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getString(DEVICE_TYPE_KEY, "");
    }

    public void saveControlMessages(JSONObject jsonObject) {
        if (jsonObject == null) {
            return;
        }
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                Object value = jsonObject.isNull(key) ? null : jsonObject.get(key);
                if (value != null) {
                    saveToPreferences(key, value);
                }
            } catch (JSONException e) {

            }
        }
    }

    public void saveShutDownState(boolean state) {
        saveToPreferences(WebEngageConstant.SHUTDOWN, state);
    }

    public boolean getShutDownState() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getBoolean(WebEngageConstant.SHUTDOWN, false);
    }

    void saveShownPush(String expId) {
        if (expId != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebEngageConstant.DATE_ISO_FORMAT, Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String strDate = simpleDateFormat.format(new Date());

            String pushWithDate = expId + TS_SEPARATOR + strDate;
            Set<String> newSet = getShownPushWithDate();
            newSet.add(pushWithDate);
            saveToPreferences(PUSH_SHOWN, newSet);
        }
    }

    Set<String> getShownPush() {
        Set<String> savedSet = getShownPushWithDate();
        Set<String> expIdSet = new HashSet<>();
        for (String pushWithDate : savedSet) {
            String expId = pushWithDate.substring(0, pushWithDate.lastIndexOf(TS_SEPARATOR));
            expIdSet.add(expId);
        }
        return expIdSet;
    }

    private Set<String> getShownPushWithDate() {
        Set<String> def = new HashSet<String>();
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return new HashSet<String>(preferences.getStringSet(PUSH_SHOWN, def));
    }

    void clearOldShownPush() {
        Set<String> savedSet = getShownPushWithDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(WebEngageConstant.DATE_ISO_FORMAT, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Set<String> pushWithDateToSave = new HashSet<>();

        for (String pushWithDate : savedSet) {
            String strDate = pushWithDate.substring(pushWithDate.lastIndexOf(TS_SEPARATOR) + 1);

            Date pushShownDate = null;
            try {
                pushShownDate = simpleDateFormat.parse(strDate);

                long pushShownTime = pushShownDate.getTime();
                long currentTime = System.currentTimeMillis();

                long diff = currentTime - pushShownTime;
                if (diff < WebEngageConstant.SHOWN_PUSH_STORAGE_LIFETIME) {
                    pushWithDateToSave.add(pushWithDate);
                }
            } catch (ParseException e) {
                Logger.e(WebEngageConstant.TAG, "Exception while parsing push shown date", e);
            }
        }

        saveToPreferences(PUSH_SHOWN, pushWithDateToSave);
    }

    protected void saveAmplifyInterval(long interval) {
        saveToPreferences(AMPLIFY_INTERVAL, interval);
    }

    protected long getAmplifyInterval() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getLong(AMPLIFY_INTERVAL, WebEngageConstant.AMPLIFY_DEFAULT_INTERVAL);
    }

    protected void saveSessionDestroyTime(long interval) {
        saveToPreferences(SESSION_DESTROY_TIME, interval);
    }

    protected long getSessionDestroyTime() {
        SharedPreferences preferences = getPreferenceFile(DEFAULT_PREFS);
        return preferences.getLong(SESSION_DESTROY_TIME, -1);
    }

}
