package com.webengage.sdk.android;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.DataContainer;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.database.ReportingStatistics;
import com.webengage.sdk.android.actions.database.UserProfileDataManager;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.webengage.sdk.android.utils.Provider.FCM;
import static com.webengage.sdk.android.utils.Provider.HW;
import static com.webengage.sdk.android.utils.Provider.MI;

class TopicInterceptorImpl implements TopicInterceptor {
    Context applicationContext = null;
    private ScheduledThreadPoolExecutor scheduledExecutor = null;

    TopicInterceptorImpl(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public boolean preCall(Topic topic, Object data) {
        Analytics analytics = AnalyticsFactory.getAnalytics(this.applicationContext);
        //Generate Interface ID if needed
        String interfaceId = analytics.getPreferenceManager().getInterfaceID();
        if (interfaceId.isEmpty()) {
            interfaceId = applicationContext.getPackageName() + "|" + WebEngageUtils.generateInterfaceID(applicationContext);
            analytics.getPreferenceManager().setInstallReferrer(false);
            Logger.d(WebEngageConstant.TAG, "preAnalyzeSystemEvent INTERFACE_ID: " + interfaceId);
            analytics.getPreferenceManager().saveInterfaceID(interfaceId);
        }
        //TODO remove logger
        switch (topic) {
            case EVENT:
            case INTERNAL_EVENT:
                EventPayload eventPayload = (EventPayload) data;
                if (WebEngageConstant.SYSTEM.equals(eventPayload.getCategory())) {
                    return preAnalyzeSystemEvent(eventPayload);
                }
                break;

            case DATA:
                //clear
                break;

            case GCM_MESSAGE:
                Bundle bundle = (Bundle) data;
                String messageAction = bundle.getString(WebEngageConstant.GCM_MESSAGE_ACTION_KEY);
                if (WebEngageConstant.SHOW_SYSTEM_TRAY_NOTIFICATION.equalsIgnoreCase(messageAction)) {
                    String notificationProperties = bundle.getString("message_data");

                    JSONObject json = null;
                    try {
                        json = new JSONObject(notificationProperties);
                    } catch (JSONException e) {
                        Logger.e(WebEngageConstant.TAG, "Exception while parsing push message_data for deduping", e);
                    }

                    if (json != null) {
                        String experimentId = json.optString("experimentId");
                        String id = json.optString("identifier");

                        AnalyticsPreferenceManager preferenceManager = AnalyticsFactory.getAnalytics(this.applicationContext).getPreferenceManager();
                        Set<String> shownPush = preferenceManager.getShownPush();
                        if (shownPush != null && shownPush.contains(experimentId)) {
                            Logger.d(WebEngageConstant.TAG, "Push {id: " + id + ", experiment-id: " + experimentId + "} is already shown, hence not rendering.");
                            return false;
                        } else {
                            preferenceManager.saveShownPush(experimentId);
                        }
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void postCall(Topic topic, Object data) {
        final Analytics analytics = AnalyticsFactory.getAnalytics(this.applicationContext);
        switch (topic) {
            case EVENT:
            case INTERNAL_EVENT:
                EventPayload eventPayload = (EventPayload) data;
                if (eventPayload != null && BuildConfig.DEBUG) {
                    Logger.d(WebEngageConstant.TAG, "Processed event: " + eventPayload.getEventName() + "\n" + String.valueOf(eventPayload) + "\n");
                }
                if (WebEngageConstant.SYSTEM.equals(eventPayload.getCategory())) {
                    postAnalyzeSystemEvent(eventPayload);

                    // Flush any bg system event
                    if (Topic.EVENT.equals(topic)) {
                        String latestSessionType = DataHolder.get().getLatestSessionType();
                        if ("background".equals(latestSessionType)) {
                            WebEngage.get().dispatchFlushAction(null);
                        }
                    }
                }
                break;

            case CONFIG_REFRESH:
                if (DataHolder.get().isAppForeground()) {
                    analytics.getScheduler().scheduleConfigRefresh(System.currentTimeMillis() + WebEngageConstant.CONFIG_REFRESH_INTERVAL);
                }
                if (DataHolder.get().isConfigUpdated()) {
                    //Update session rules only when the response code is 200
                    analytics.getSessionManager().executeSessionAndPageRules();
                    DataHolder.get().silentSetData(WebEngageConstant.REFRESH_CONFIG_RULE, false);
                }
                break;

            case REPORT:
                if (DataHolder.get().isAppForeground()) {
                    if (DataHolder.get().getUpfc() != null) {
                        analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_JCX_INTERVAL);
                    } else {
                        analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_INTERVAL);
                    }
                } else {
                    analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.BACKGROUND_SYNC_INTERVAL);
                }
                break;

            case FETCH_PROFILE:
                List<Object> list = new ArrayList<Object>();
                list.add(DataContainer.USER.toString());
                list.add("cuid");
                String cuidFromUserProfile = (String) DataHolder.get().getData(list);
                String localCuid = analytics.getPreferenceManager().getCUID();
                if (!localCuid.isEmpty() && (cuidFromUserProfile == null || cuidFromUserProfile.isEmpty()) && (DataHolder.get().isAppForeground())) {
                    analytics.getScheduler().scheduleUserProfileCall(System.currentTimeMillis() + WebEngageConstant.USER_PROFILE_CALL_INTERVAL);
                }
                analytics.getSessionManager().executeSessionAndPageRules();
                break;

            case JOURNEY_CONTEXT:
                analytics.getSessionManager().executeSessionAndPageRules();
                break;

            case BOOT_UP:
                analytics.getSessionManager().executeSessionAndPageRules();
                try {
                    checkForAppInstalledEvent(analytics);
                    checkForAppUpgradedEvent(analytics);
                } catch (Exception e) {
                    Logger.e(WebEngageConstant.TAG, "Exception while checking for app install and app upgrade events", e);
                }

                Scheduler scheduler = analytics.getScheduler();
                if (!scheduler.isAmplifyScheduled()) {
                    long amplifyInterval = analytics.getPreferenceManager().getAmplifyInterval();
                    scheduler.scheduleAmplify(amplifyInterval);
                }
                break;

            case DATA:
                analytics.getSessionManager().executePageRules();
                break;

            case AMPLIFY:
                analytics.getPreferenceManager().clearOldShownPush();
                if (scheduledExecutor == null) {
                    scheduledExecutor = new ScheduledThreadPoolExecutor(1);
                }
                scheduledExecutor.schedule(new Runnable() {
                    @Override
                    public void run() {
                        long amplifyInterval = analytics.getPreferenceManager().getAmplifyInterval();
                        ReportingStatistics.setRescheduleAmplify(false);
                        analytics.getScheduler().scheduleAmplify(amplifyInterval);
                    }
                }, WebEngageConstant.AMPLIFY_JOB_FINISH_DELAY, TimeUnit.MILLISECONDS);
                break;
        }
    }

    private void postAnalyzeSystemEvent(EventPayload eventPayload) {
        String event = eventPayload.getEventName();
        Analytics analytics = AnalyticsFactory.getAnalytics(this.applicationContext);
        if (event != null) {
            if (EventName.USER_LOGGED_OUT.equals(event)) {
                if (DataHolder.get().isAppForeground()) {
                    long firstAcitivtyStart = DataHolder.get().getFirstActivityStartEpoch();
                    if (firstAcitivtyStart != -1) {
                        DataHolder.get().setFirstActivityStartTime(System.currentTimeMillis());
                        analytics.getSessionManager().sentTimeSpentEvent(System.currentTimeMillis() - firstAcitivtyStart);
                    }
                }
                analytics.getSessionManager().destroyCurrentSession();
                analytics.getPreferenceManager().saveCUID("");
                ((UserImpl) UserFactory.getUser(this.applicationContext, analytics)).generateLUID();

                String cuid = analytics.getPreferenceManager().getCUID();
                Map<String, Object> allUserData = UserProfileDataManager.getInstance(this.applicationContext).getAllUserData(cuid.isEmpty() ? analytics.getPreferenceManager().getLUID() : cuid);
                if (allUserData != null) {
                    if (allUserData.size() > 0) {
                        DataHolder.get().silentSetData(allUserData);
                    }
                }

                if (DataHolder.get().isAppForeground()) {
                    analytics.getSessionManager().createNewForegroundSession();
                } else {
                    analytics.getSessionManager().createNewBackgroundSession();
                }
                analytics.getScheduler().cancelUserProfileCall();
            } else if (EventName.VISITOR_SESSION_CLOSE.equals(event)) {


            } else if (EventName.USER_LOGGED_IN.equals(event)) {
                analytics.getScheduler().scheduleUserProfileCall(System.currentTimeMillis() + WebEngageConstant.USER_PROFILE_CALL_INTERVAL);
            } else if (EventName.NOTIFICATION_CONTROL_GROUP.equals(event)) {
                DataHolder.get().setEntityRunningState(false);
            } else if (EventName.APP_UPGRADED.equals(event)) {
                //force fully firing GCM registered event on app upgraded
                // if auto GCM registration is true and regID changes then 2 GCM registered event will be fired with same regID
                Map<String, Object> eventData = new HashMap<String, Object>();
                String regId = analytics.getPreferenceManager().getRegistrationID();
                if (!WebEngageUtils.isEmpty(regId)) {
                    eventData.put("gcm_regId", regId);
                    eventData.put("gcm_project_number", null);
                    eventData.put("provider", FCM.name());
                    Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.GCM_REGISTERED, null, eventData, null, applicationContext), applicationContext);
                    WebEngage.startService(intent, applicationContext);
                }

                //Pushing MI token
                Map<String, Object> eventDataMI = new HashMap<String, Object>();
                String regIdMI = analytics.getPreferenceManager().getXiaomiRegistrationID();
                if (!WebEngageUtils.isEmpty(regIdMI)) {
                    eventData.put("gcm_regId", regIdMI);
                    eventData.put("gcm_project_number", null);
                    eventData.put("provider", MI.name());
                    Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(
                            EventName.GCM_REGISTERED, null, eventDataMI,
                            null, applicationContext), applicationContext);
                    WebEngage.startService(intent, applicationContext);
                }

                //Pushing Huawei token
                Map<String, Object> eventDataHW = new HashMap<String, Object>();
                String regIdHW = analytics.getPreferenceManager().getHuaweiRegistrationID();
                if (!WebEngageUtils.isEmpty(regIdHW)) {
                    eventData.put("gcm_regId", regIdHW);
                    eventData.put("gcm_project_number", null);
                    eventData.put("provider", HW.name());
                    Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(
                            EventName.GCM_REGISTERED, null, eventDataHW,
                            null, applicationContext), applicationContext);
                    WebEngage.startService(intent, applicationContext);
                }
            }
        }
    }

    private boolean preAnalyzeSystemEvent(EventPayload eventPayload) {
        String event = eventPayload.getEventName();
        Analytics analytics = AnalyticsFactory.getAnalytics(this.applicationContext);
        if (event != null) {
            if (EventName.WE_WK_ACTIVITY_START.equals(event)) {
                Map<String, Object> systemData = eventPayload.getSystemData();
                if (systemData != null) {
                    String screenPath = (String) systemData.get("screen_path");
                    if (screenPath != null && screenPath.equals("com.webengage.sdk.android.actions.render.WebEngageActivity")) {
                        return false;
                    }
                }
                String latestSessionType = DataHolder.get().getLatestSessionType();
                Map<String, Object> extraData = eventPayload.getExtraData();
                if (extraData != null && extraData.containsKey(WebEngageConstant.ACTIVITY_COUNT)) {
                    int count = ((Number) extraData.get(WebEngageConstant.ACTIVITY_COUNT)).intValue();
                    if (count == 1) {
                        if (DataHolder.get().getUpfc() != null) {
                            analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_JCX_INTERVAL);
                        } else {
                            analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_INTERVAL);
                        }
                        analytics.getScheduler().scheduleConfigRefresh(System.currentTimeMillis() + WebEngageConstant.CONFIG_REFRESH_INTERVAL);
                        DataHolder.get().setFirstActivityStartTime(System.currentTimeMillis());
                        if (DataHolder.get().isBootUpCalled()) {
                            DataHolder.get().setBootUpCalled(false);
                            if ("background".equals(latestSessionType)) {
                                analytics.getSessionManager().destroyCurrentSession();
                            }
                            analytics.getSessionManager().createNewForegroundSession();
                        } else {
                            if ("background".equals(latestSessionType)) {
                                analytics.getSessionManager().destroyCurrentSession();
                                analytics.getSessionManager().createNewForegroundSession();
                            }
                        }
                    }
                }
            } else if (EventName.WE_WK_ACTIVITY_STOP.equals(event)) {
                Map<String, Object> systemData = eventPayload.getSystemData();
                if (systemData != null) {
                    String screenPath = (String) systemData.get("screen_path");
                    if (screenPath != null && screenPath.equals("com.webengage.sdk.android.actions.render.WebEngageActivity")) {
                        return false;
                    }
                }
                Map<String, Object> extraData = eventPayload.getExtraData();
                if (extraData != null && extraData.containsKey(WebEngageConstant.ACTIVITY_COUNT)) {
                    int count = ((Number) extraData.get(WebEngageConstant.ACTIVITY_COUNT)).intValue();
                    if (count == 0) {
                        long activityStartEpoch = DataHolder.get().getFirstActivityStartEpoch();
                        if (activityStartEpoch != -1) {
                            DataHolder.get().setFirstActivityStartTime(-1);
                            analytics.getSessionManager().sentTimeSpentEvent(System.currentTimeMillis() - activityStartEpoch);
                        }
                        WebEngage.get().dispatchFlushAction(null);
                    }
                }
            } else if (EventName.VISITOR_NEW_SESSION.equals(event)) {
                analytics.getScheduler().cancelAllSessionDelayEvents(DataHolder.get().getSessionDelayValues());
                String userIdentifer = analytics.getPreferenceManager().getCUID().isEmpty() ? analytics.getPreferenceManager().getLUID() : analytics.getPreferenceManager().getCUID();
                DataHolder.get().clearSessionLevelData(userIdentifer, DataContainer.ANDROID);
                DataHolder.get().clearSessionLevelData(userIdentifer, DataContainer.SCOPES);
                analytics.getPreferenceManager().saveSessionEvaluatedIds(null);
                for (DataContainer dataContainer : DataContainer.values()) {
                    if (!dataContainer.canBeStored()) {
                        DataHolder.get().setData(dataContainer.toString(), null);
                    }
                }
                analytics.getSessionManager().generateSUID();
                Map<String, Object> systemData = eventPayload.getSystemData();

                if ("online".equals(systemData.get("session_type").toString())) {
                    try {
                        SubscriberManager.get(this.applicationContext).callSubscribers(Topic.CONFIG_REFRESH, null);
                        SubscriberManager.get(this.applicationContext).callSubscribers(Topic.FETCH_PROFILE, null);
                        SubscriberManager.get(this.applicationContext).callSubscribers(Topic.JOURNEY_CONTEXT, null);
                    } catch (Exception e) {
                        try {
                            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
                        } catch (Exception e1) {

                        }
                    }
                    if (DataHolder.get().getUpfc() != null) {
                        analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_JCX_INTERVAL);
                    } else {
                        analytics.getScheduler().scheduleNextSync(System.currentTimeMillis() + WebEngageConstant.FOREGROUND_SYNC_INTERVAL);
                    }
                    analytics.getScheduler().scheduleConfigRefresh(System.currentTimeMillis() + WebEngageConstant.CONFIG_REFRESH_INTERVAL);
                    analytics.getScheduler().scheduleSessionDelayEvents(DataHolder.get().getSessionDelayValues());
                    analytics.getScheduler().scheduleAmplify(WebEngageConstant.AMPLIFY_INITIAL_DELAY);
                } else {
                    analytics.getScheduler().scheduleSessionDestroy(System.currentTimeMillis() + WebEngageConstant.BACKGROUND_SESSION_CREATION_INTERVAL);
                }
            } else if (EventName.VISITOR_SESSION_CLOSE.equals(event)) {

            } else if (EventName.USER_LOGGED_IN.equals(event)) {
                String cuid = eventPayload.getExtraData().get("cuid").toString();
                if (analytics.getPreferenceManager().getCUID().equals(cuid)) {
                    Logger.e(WebEngageConstant.TAG, "INVALID OPERATION: User: " + cuid + " is Already Logged-in");
                    return false;
                }
                if (!analytics.getPreferenceManager().getCUID().equals(cuid) && !analytics.getPreferenceManager().getCUID().isEmpty()) {
                    try {
                        SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EVENT, EventFactory.newSystemEvent(EventName.USER_LOGGED_OUT, null, null, null, applicationContext));
                    } catch (Exception e) {
                        try {
                            SubscriberManager.get(this.applicationContext).callSubscribers(Topic.EXCEPTION, e);
                        } catch (Exception e1) {

                        }
                    }
                }
                analytics.getPreferenceManager().saveCUID(cuid);
            } else if (EventName.USER_LOGGED_OUT.equals(event)) {
                if (analytics.getPreferenceManager().getCUID().isEmpty()) {
                    Logger.e(WebEngageConstant.TAG, "INVALID OPERATION: User Not Logged-in");
                    return false;
                }
            } else if (EventName.WE_WK_SCREEN_NAVIGATED.equals(event)) {
                analytics.getScheduler().cancelAllPageDelayEvents(DataHolder.get().getPageDelayValues());
                analytics.getScheduler().schedulePageDelayEvents(DataHolder.get().getPageDelayValues());
                WeakReference<Activity> activityWeakReference = analytics.getActivity();
                if (activityWeakReference != null && activityWeakReference.get() != null) {
                    FragmentManager fragmentManager = activityWeakReference.get().getFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(WebEngageConstant.TAG);
                    if (fragment != null) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        try {
                            fragmentTransaction.remove(fragment).commitAllowingStateLoss();
                        } catch (Exception e) {

                        }
                    }
                    try {
                        Map<String, Object> screenData = eventPayload.getEventData();
                        Bundle extras = activityWeakReference.get().getIntent().getExtras();
                        if (extras != null) {
                            String shouldSetScreenData = extras.getString(WebEngageConstant.WE_ADD_TO_SCREEN_DATA);
                            if (shouldSetScreenData != null && Boolean.valueOf(shouldSetScreenData)) {
                                if (screenData == null) {
                                    screenData = new HashMap<String, Object>();
                                }
                                screenData.putAll((Map<String, Object>) DataType.cloneExternal(event, WebEngageUtils.bundleToMap(extras)));
                                activityWeakReference.get().getIntent().removeExtra(WebEngageConstant.WE_ADD_TO_SCREEN_DATA);
                                eventPayload.setEventData(screenData);
                            }
                        }
                    } catch (Exception e) {

                    }

                }
            } else if (EventName.USER_UPDATE.equals(event)) {
                try {
                    Map<String, Object> systemData = eventPayload.getSystemData();
                    if (systemData != null && !systemData.isEmpty()) {
                        List<Object> list = new ArrayList<>();
                        list.add(DataContainer.USER.toString());
                        Map<String, Object> user = null;
                        try {
                            user = (Map<String, Object>) DataHolder.get().getData(list);
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Exception while getting user-map from data-holder", e);
                        }

                        list.clear();
                        list.add(DataContainer.ANDROID.toString());
                        Map<String, Object> android = null;
                        try {
                            android = (Map<String, Object>) DataHolder.get().getData(list);
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Exception while getting android-map from data-holder", e);
                        }

                        for (Map.Entry<String, Object> entry : systemData.entrySet()) {
                            String key = entry.getKey();
                            Object newVal = entry.getValue();

                            // User attribute
                            if (UserSystemAttribute.valueByString(key) != null) {
                                if (user == null || user.isEmpty()) {
                                    return true;
                                }

                                Object prevVal = user.get(key);
                                if (!WebEngageUtils.areEqual(prevVal, newVal)) {
                                    return true;
                                }
                            }

                            // Device attribute
                            if (UserDeviceAttribute.isDeviceAttribute(key)) {
                                if (android == null || android.isEmpty()) {
                                    return true;
                                }

                                Object prevVal = android.get(key);
                                if (!WebEngageUtils.areEqual(prevVal, newVal)) {
                                    return true;
                                }
                            }
                        }
                    }

                    Map<String, Object> eventData = eventPayload.getEventData();
                    if (eventData != null && !eventData.isEmpty()) {
                        List<Object> list = new ArrayList<>();
                        list.add(DataContainer.ATTR.toString());

                        Map<String, Object> attr = null;
                        try {
                            attr = (Map<String, Object>) DataHolder.get().getData(list);
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Exception while getting attr-map from data-holder", e);
                        }

                        for (Map.Entry<String, Object> entry : eventData.entrySet()) {
                            if (attr == null || attr.isEmpty()) {
                                return true;
                            }

                            String key = entry.getKey();
                            Object newVal = entry.getValue();
                            Object prevVal = attr.get(key);
                            if (!WebEngageUtils.areEqual(prevVal, newVal)) {
                                return true;
                            }
                        }
                    }

                    Logger.w(WebEngageConstant.TAG, "User profile is up-to-date, hence not updating");
                    return false;
                } catch (Exception e) {
                    Logger.e(WebEngageConstant.TAG, "Exception while pre-analyzing user-update", e);
                    return true;
                }
            }
        }
        return true;
    }

    private void checkForAppInstalledEvent(Analytics analytics) {
        if (analytics.getPreferenceManager().getPreferenceFile(BasePreferenceManager.VOLATILE_PREFS).contains("referrer")) {
            String referrer = "";
            try {
                referrer = URLDecoder.decode(analytics.getPreferenceManager().getVolatileData("referrer"), "UTF-8");
            } catch (UnsupportedEncodingException e) {

            }
            analytics.getPreferenceManager().removeVolatileData("referrer");
            Map<String, Object> map = new HashMap<String, Object>();
            if (!referrer.isEmpty()) {
                map.put("referrer", referrer);
                map.putAll(new GAAttributionTransformer().transform(referrer));
            }
            Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.APP_INSTALLED, map, null, null, applicationContext), this.applicationContext);
            WebEngage.startService(intent, applicationContext);
        }
    }

    private void checkForAppUpgradedEvent(Analytics analytics) {
        int oldVersion = analytics.getPreferenceManager().getVersionCode();
        int newVersion = -1;
        PackageInfo pi = WebEngageUtils.getPackageInfo(this.applicationContext);
        if (pi != null) {
            newVersion = pi.versionCode;
            if (oldVersion != -1 && oldVersion != newVersion) {
                Map<String, Object> eventData = new HashMap<String, Object>();
                eventData.put("app_version_code_old", oldVersion);
                eventData.put("app_version_code_new", newVersion);
                Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.APP_UPGRADED, null, eventData, null, applicationContext), this.applicationContext);
                WebEngage.startService(intent, applicationContext);
                CallbackDispatcher.init(this.applicationContext).onAppUpgraded(this.applicationContext, oldVersion, newVersion);
            }
            analytics.getPreferenceManager().saveVersionCode(pi.versionCode);
        }
    }
}
