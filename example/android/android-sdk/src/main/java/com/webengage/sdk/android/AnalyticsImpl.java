package com.webengage.sdk.android;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;

import com.webengage.sdk.android.actions.database.DataContainer;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class AnalyticsImpl extends Analytics {
    Context applicationContext = null;
    AnalyticsPreferenceManager preferenceManager = null;
    WeakReference<Activity> activityWeakReference = null;
    SessionManager sessionManager = null;
    Scheduler scheduler = null;
    AtomicInteger activityCount = null;

    AnalyticsImpl(Context context, AnalyticsPreferenceManager analyticsPreferenceManager, SessionManager sessionManager, Scheduler scheduler) {
        this.applicationContext = context.getApplicationContext();
        this.preferenceManager = analyticsPreferenceManager;
        this.scheduler = scheduler;
        this.sessionManager = sessionManager;
        activityCount = new AtomicInteger(0);
    }

    @Override
    public void track(String eventName) {
        track(eventName, null, null);
    }

    @Override
    public void track(String eventName, Options options) {
        track(eventName, null, options);
    }

    @Override
    public void track(String eventName, Map<String, ? extends Object> attributes) {
        track(eventName, attributes, null);
    }

    @Override
    public void track(String eventName, Map<String, ?> attributes, Options options) {
        if (!isValidEvent(eventName)) {
            return;
        }
        dispatchEventTopic(EventFactory.newApplicationEvent(eventName, null, (Map<String, Object>) attributes, ((options != null) ? (Map<String, Object>) options.toMap() : null), applicationContext));
    }

    @Override
    public void trackSystem(String eventName, Map<String, ?> systemAttributes, Map<String, ?> attributes) {
        if (!EventName.SYSTEM_EVENTS.contains(eventName)) {
            Logger.e(WebEngageConstant.TAG, "Event name: " + eventName + " is not a system event");
            return;
        }
        dispatchEventTopic(EventFactory.newSystemEvent(eventName,
                (Map<String, Object>) systemAttributes,
                (attributes != null) ? (Map<String, Object>) attributes: null,
                null,
                applicationContext));
    }


    private boolean isValidEvent(String eventName) {
        if (WebEngageUtils.isBlank(eventName)) {
            Logger.e(WebEngageConstant.TAG, "Event Name is Invalid");
            return false;
        }
        if (eventName.startsWith("we_")) {
            Logger.e(WebEngageConstant.TAG, "Found prefix \"we_\" in event name : " + eventName);
            return false;
        }
        return true;
    }

    @Override
    public void start(Activity activity) {
        try {
            scheduler.cancelSessionDestroy();
            int count = activityCount.incrementAndGet();
            DataHolder.get().setAppForeground(true);
            activityWeakReference = new WeakReference<Activity>(activity);
            if (activity != null) {
                Logger.d(WebEngageConstant.TAG, " Activity start: " + activity.getClass().getName());
                Map<String, Object> systemData = new HashMap<String, Object>();
                if (activity.getClass() != null) {
                    systemData.put("screen_path", activity.getClass().getName());
                }
                if (activity.getTitle() != null) {
                    systemData.put("screen_title", activity.getTitle().toString());
                }
                Map<String,Object> extraData = new HashMap<>();
                extraData.put(WebEngageConstant.ACTIVITY_COUNT, count);
                Intent intent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_ACTIVITY_START, systemData, null, extraData, applicationContext), applicationContext);
                WebEngage.startService(intent, applicationContext);
                if (WebEngage.get().getWebEngageConfig().getEveryActivityIsScreen()) {
                    screenNavigated(activity.getClass().getName());
                }
            }
        } catch (Exception e) {
            dispatchExceptionTopic(e);
        }
    }

    @Override
    public void stop(Activity activity) {
        try {
            if (activity != null) {
                Logger.d(WebEngageConstant.TAG, " Activity stop: " + activity.getClass().getName());
                try {
                    FragmentManager fragmentManager = activity.getFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(WebEngageConstant.TAG);
                    if (fragment != null) {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.remove(fragment).commitAllowingStateLoss();
                    }
                } catch (Exception e) {
                    Logger.e(WebEngageConstant.TAG, "Unable to remove attached in-app fragment from stopped activity.");
                }
            }

            int count = activityCount.decrementAndGet();
            Map<String,Object> extraData = new HashMap<>();
            extraData.put(WebEngageConstant.ACTIVITY_COUNT, count);
            if (count == 0) {
                DataHolder.get().setAppForeground(false);
                //Fetching session destroy time from v4 config
                long sessionDestroyTime = getPreferenceManager().getSessionDestroyTime();
                if (sessionDestroyTime == -1) {
                    //Fetching session destroy time from local config when not present in v4 config
                    sessionDestroyTime = WebEngage.get().getWebEngageConfig().getSessionDestroyTime();
                }
                if (sessionDestroyTime == -1) {
                    //Initializing with default value when both v4 and local config does not contain session destroy time.
                    sessionDestroyTime = WebEngageConstant.APP_SESSION_DESTROY_TIMEOUT;
                } else {
                    if (sessionDestroyTime > (60*60*60)) {
                        //Checking upper limit
                        sessionDestroyTime =  WebEngageConstant.ONE_HOUR;
                    } else if (sessionDestroyTime < 15) {
                        //Checking lower limit
                        sessionDestroyTime = WebEngageConstant.APP_SESSION_DESTROY_TIMEOUT;
                    } else {
                        sessionDestroyTime *= WebEngageConstant.ONE_SECOND;
                    }
                }
                Logger.d(WebEngageConstant.TAG, "Visitor session timeout: " + sessionDestroyTime);
                scheduler.scheduleSessionDestroy(System.currentTimeMillis() + sessionDestroyTime);
            }
            Intent intent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_ACTIVITY_STOP, null, null, extraData, applicationContext), applicationContext);
            WebEngage.startService(intent, applicationContext);
        } catch (Exception e) {
            dispatchExceptionTopic(e);
        }

    }

    @Override
    public void screenNavigated(String screenName) {
        this.screenNavigated(screenName, null);
    }

    @Override
    public void screenNavigated(String screenName, Map<String, ? extends Object> screenData) {
        try {
            Logger.d(WebEngageConstant.TAG, "Screen navigated: " +screenName + " with data: "+screenData);
            Map<String, Object> systemData = new HashMap<String, Object>();
            systemData.put("screen_name", screenName);
            Intent intent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_SCREEN_NAVIGATED, systemData, (Map<String, Object>) screenData, null, applicationContext), applicationContext);
            WebEngage.startService(intent, applicationContext);
        } catch (Exception e) {
            dispatchExceptionTopic(e);
        }
    }

    @Override
    public void setScreenData(Map<String, ? extends Object> screenData) {
        try {
            Logger.d(WebEngageConstant.TAG, "Set screen data: "+screenData);
            List<Object> containerPath = new ArrayList<Object>();
            containerPath.add(DataContainer.PAGE.toString());
            containerPath.add(WebEngageConstant.CUSTOM);
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> clonedData = null;
            try {
                clonedData = (Map<String, Object>) DataType.cloneExternal(screenData);
            } catch (Exception e) {

            }
            map.put("data", clonedData);
            map.put("path", containerPath);

            Intent intent = IntentFactory.newIntent(Topic.DATA, map, applicationContext);
            WebEngage.startService(intent, applicationContext);
        } catch (Exception e) {
            dispatchExceptionTopic(e);
        }
    }

    @Override
    public WeakReference<Activity> getActivity() {
        return activityWeakReference;
    }


    @Override
    public void installed(Intent intent) {
        try {
            if (intent == null) {
                Logger.e(WebEngageConstant.TAG, "Intent is Null");
                return;
            }

            if (!preferenceManager.isInstallReferrerSet()) {
                preferenceManager.setInstallReferrer(true);
                if (intent.hasExtra("referrer")) {
                    preferenceManager.saveVolatileData("referrer", intent.getStringExtra("referrer"));
                } else {
                    preferenceManager.saveVolatileData("referrer", "");
                }
            }
        } catch (Exception e) {
            dispatchExceptionTopic(e);
        }
    }


    @Override
    protected AnalyticsPreferenceManager getPreferenceManager() {
        return preferenceManager;
    }

    @Override
    protected SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    protected Scheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    protected void dispatchEventTopic(Object data) {
        Intent eventIntent = IntentFactory.newIntent(Topic.EVENT, data, applicationContext);
        WebEngage.startService(eventIntent, applicationContext);
    }

    @Override
    protected void dispatchExceptionTopic(Object data) {
        if (data != null) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred : " + data.toString());
        }
        Intent eventIntent = IntentFactory.newIntent(Topic.EXCEPTION, data, applicationContext);
        WebEngage.startService(eventIntent, applicationContext);
    }
}
