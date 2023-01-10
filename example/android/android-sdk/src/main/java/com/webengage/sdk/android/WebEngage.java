package com.webengage.sdk.android;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.database.EventDataManager;
import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.actions.database.UserProfileDataManager;
import com.webengage.sdk.android.actions.render.CallToAction;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.actions.rules.RuleExecutor;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.callbacks.InAppNotificationCallbacks;
import com.webengage.sdk.android.callbacks.LifeCycleCallbacks;
import com.webengage.sdk.android.callbacks.PushNotificationCallbacks;
import com.webengage.sdk.android.callbacks.StateChangeCallbacks;
import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.HttpDataManager;
import com.webengage.sdk.android.utils.http.RequestExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static com.webengage.sdk.android.utils.Provider.FCM;
import static com.webengage.sdk.android.utils.Provider.HW;
import static com.webengage.sdk.android.utils.Provider.MI;

public class WebEngage extends AbstractWebEngage {
    private static Context applicationContext = null;
    private static ConfigPreferenceManager configPreferenceManager = null;
    private static WebEngageConfig webEngageConfig = null;
    private static volatile AbstractWebEngage self = null;
    private static QueuedWebEngageImpl queuedImpl = null;
    private static final Object engageLock = new Object();
    private static boolean isEngageCalled = false;

    private void deleteAllData(Context context) {
        try {
            boolean deleted = false;
            // Delete SharedPreferences
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                deleted = context.deleteSharedPreferences(BasePreferenceManager.DEFAULT_PREFS);
                deleted = context.deleteSharedPreferences(BasePreferenceManager.VOLATILE_PREFS) || deleted;
            } else {
                AnalyticsPreferenceManager analyticsPreferenceManager = analytics().getPreferenceManager();
                deleted = analyticsPreferenceManager.clear(BasePreferenceManager.DEFAULT_PREFS);
                deleted = analyticsPreferenceManager.clear(BasePreferenceManager.VOLATILE_PREFS) || deleted;
            }

            // Delete SQLite databases
            deleted = EventDataManager.deleteDatabase(context) || deleted;
            deleted = UserProfileDataManager.deleteDatabase(context) || deleted;
            deleted = HttpDataManager.deleteDatabase(context) || deleted;
            if (deleted) {
                Logger.w(WebEngageConstant.TAG, "Deleted all saved data");
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while clearing data", e);
        }
    }

    private boolean isFirstLaunch(Context applicationContext) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            File dir = applicationContext.getNoBackupFilesDir();
            File testFile = new File(dir, "we_backup");
            if (testFile.exists()) {
                return false;
            } else {
                try {
                    return testFile.createNewFile();
                } catch (Exception e) {
                    // You are unlucky!
                }
            }
        }
        return false;
    }

    private void performCleanUp(Context context) {
        try {
            if (isFirstLaunch(context)) {
                deleteAllData(context);
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while clearing backed up data", e);
        }
    }

    private WebEngage(Context context) {
        Logger.d(WebEngageConstant.TAG, " WebEngage constructor called");
        String prevLicenseCode = configPreferenceManager.getLicenseCode();
        String currLicenseCode = webEngageConfig.getWebEngageKey();
        if (!WebEngageUtils.isEmpty(prevLicenseCode) && !prevLicenseCode.equals(currLicenseCode)) {
            Logger.w(WebEngageConstant.TAG, "Previous license-code (" + prevLicenseCode + ") did not match current license-code (" + currLicenseCode + "), hence deleting all saved data");
            deleteAllData(context);
        }
        configPreferenceManager.saveLicenseCode(webEngageConfig.getWebEngageKey());
        DataHolder.get().setBootUpCalled(true);
        RequestExecutor.addInterceptor(new NetworkInterceptor());
        SubscriberManager.get(applicationContext).addTopicInterceptor(new TopicInterceptorImpl(applicationContext));
        DataHolder.get().registerChangeListener(analytics().getPreferenceManager());
        DataHolder.get().registerChangeListener(UserProfileDataManager.getInstance(applicationContext));
        EventDataManager.getInstance(applicationContext).updateSyncingEvents();
        if (analytics().getPreferenceManager().getLUID().isEmpty()) {
            ((UserImpl) user()).generateLUID();
        }
        Intent intent = IntentFactory.newIntent(Topic.BOOT_UP, null, applicationContext);
        WebEngage.startService(intent, applicationContext);
        if (analytics().getPreferenceManager().getSUID().isEmpty()) {
            analytics().getSessionManager().postNewBackgroundSession();
        }
        CallbackDispatcher.init(applicationContext);

        if (webEngageConfig.isReportingStrategySet()) {
            setEventReportingStrategy(webEngageConfig.getEventReportingStrategy());
        }

        if (webEngageConfig.isLocationTrackingSet()) {
            if (webEngageConfig.isLocationTrackingStrategySet()) {
                setLocationTrackingStrategy(webEngageConfig.getLocationTrackingStrategy());
            } else {
                setLocationTracking(webEngageConfig.getLocationTrackingFlag());
            }
        } else {
            if (webEngageConfig.isLocationTrackingStrategySet()) {
                setLocationTrackingStrategy(webEngageConfig.getLocationTrackingStrategy());
            } else {
                //By default location would be disabled
                setLocationTrackingStrategy(LocationTrackingStrategy.DISABLED);
                //LocationManagerFactory.getLocationManager(applicationContext).registerLocationUpdates(WebEngageConstant.LOCATION_INTERVAL_CITY, WebEngageConstant.LOCATION_FASTEST_INTERVAL_CITY, WebEngageConstant.DISTANCE_THRESHOLD_CITY, 104);
            }
        }

        if (analytics().getPreferenceManager().getAppCrashedFlag()) {
            Logger.e(WebEngageConstant.TAG, "App was crashed last time, ");
            analytics().getPreferenceManager().removeVolatileData(BasePreferenceManager.APP_CRASHED_KEY);
            Intent appCrashedIntent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.APP_CRASHED, null, null, null, applicationContext), applicationContext);
            WebEngage.startService(appCrashedIntent, applicationContext);
        }

        if (webEngageConfig.isEnableCrashTracking()) {
            try {
                WebEngageUncaughtExceptionHandler uncaughtExceptionHandler = new WebEngageUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), applicationContext, analytics().getPreferenceManager());
                Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, "Exception occurred during registering exception handler: " + e.toString());
            }
        }


        try {
            if (webEngageConfig.getDefaultPushChannelConfiguration() != null) {
                PushChannelManager.registerPushChannel(webEngageConfig.getDefaultPushChannelConfiguration(), applicationContext);
                Logger.d(WebEngageConstant.TAG, "Default push channel registered");
            } else {
                Logger.w(WebEngageConstant.TAG, "Not registering for default push channel");
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Error during channel registration: " + e.toString());
        }
        Logger.d(WebEngageConstant.TAG, "WebEngage Successfully Initialized");
        Logger.d(WebEngageConstant.TAG, "Current interface_id: " + analytics().getPreferenceManager().getInterfaceID());
        Logger.d(WebEngageConstant.TAG, "Current luid: " + analytics().getPreferenceManager().getLUID());
        Logger.d(WebEngageConstant.TAG, "Current cuid: " + analytics().getPreferenceManager().getCUID());
        Logger.d(WebEngageConstant.TAG, "Current token FCM: " + analytics().getPreferenceManager().getRegistrationID());
        Logger.d(WebEngageConstant.TAG, "Current token MI: " + analytics().getPreferenceManager().getXiaomiRegistrationID());
        Logger.d(WebEngageConstant.TAG, "Current token HW: " + analytics().getPreferenceManager().getHuaweiRegistrationID());
        Logger.d(WebEngageConstant.TAG, "Current WebEngage Configuration: " + webEngageConfig.toString());
        Logger.d(WebEngageConstant.TAG, "SDT from app config: " + webEngageConfig.getSessionDestroyTime());


        if (!analytics().getPreferenceManager().isInstallReferrerSet() && ReflectionUtils.isInstallReferrerPresent()) {
            InstallReferrerHelper installReferrerHelper = new InstallReferrerHelper(context);
            installReferrerHelper.fetch();
        }
    }

    public static AbstractWebEngage get() {
        synchronized (engageLock) {
            if (self == null) {
                if (isEngageCalled) {
                    //this means that some exception occurred during initialization, no need to queue events
                    Logger.d(WebEngageConstant.TAG, "Returning no-op implementation of WebEngage");
                    if (webEngageConfig == null) {
                        webEngageConfig = new WebEngageConfig.Builder().build();
                    }
                    self = new WebEngageNoOpImpl(webEngageConfig);
                    return self;
                } else {
                    //queue events
                    Logger.d(WebEngageConstant.TAG, "Returning queued implementation of WebEngage");
                    if (queuedImpl == null) {
                        queuedImpl = new QueuedWebEngageImpl(new WebEngageConfig.Builder().build());
                    }
                    return queuedImpl;
                }
            } else {
                return self;
            }
        }
    }

    public static void registerPushNotificationCallback(PushNotificationCallbacks pushNotificationCallbacks) {
        CallbackDispatcher.registerPushNotificationCallback(pushNotificationCallbacks);
    }

    public static void registerCustomPushRenderCallback(CustomPushRender customPushRender) {
        CallbackDispatcher.registerCustomPushRenderCallback(customPushRender);
    }

    public static void registerCustomPushRerenderCallback(CustomPushRerender customPushRerender) {
        CallbackDispatcher.registerCustomPushRerenderCallback(customPushRerender);
    }

    public static void registerInAppNotificationCallback(InAppNotificationCallbacks inAppNotificationCallbacks) {
        CallbackDispatcher.registerInAppNotificationCallback(inAppNotificationCallbacks);
    }

    public static void registerLifeCycleCallback(LifeCycleCallbacks lifeCycleCallbacks) {
        CallbackDispatcher.registerLifeCycleCallback(lifeCycleCallbacks);
    }

    public static void registerStateChangeCallback(StateChangeCallbacks stateChangeCallbacks) {
        if (isEngaged()) {
            CallbackDispatcher.registerStateChangeCallback(stateChangeCallbacks, AnalyticsFactory.getAnalytics(applicationContext), applicationContext);
        } else {
            CallbackDispatcher.registerStateChangeCallback(stateChangeCallbacks, null, null);
        }
    }

    public static void unregisterPushNotificationCallback(PushNotificationCallbacks pushNotificationCallbacks) {
        CallbackDispatcher.unregisterPushNotificationCallback(pushNotificationCallbacks);
    }

    public static void unregisterInAppNotificationCallback(InAppNotificationCallbacks inAppNotificationCallbacks) {
        CallbackDispatcher.unregisterInAppNotificationCalback(inAppNotificationCallbacks);
    }

    public static void unregisterLifeCycleCallback(LifeCycleCallbacks lifeCycleCallbacks) {
        CallbackDispatcher.unregisterLifeCycleCallback(lifeCycleCallbacks);
    }

    public static void unregisterStateChangeCallback(StateChangeCallbacks stateChangeCallbacks) {
        CallbackDispatcher.unregisterStateChangeCallback(stateChangeCallbacks);
    }

    /**
     * WARNING: Auto generated, do not edit.All changes will be undone.
     *
     * @param context
     */
    public static void engage(Context context) {
        engage(context, null);
    }

    /**
     * checks whether WebEngage has been successfully intialised or not.
     *
     * @return
     */
    public static boolean isEngaged() {
        synchronized (engageLock) {
            return (isEngageCalled && self != null && self instanceof WebEngage);
        }
    }

    /**
     * Make sure that you have checked isEngaged() before
     * calling this else it can return null.
     *
     * @return
     */
    public static Context getApplicationContext() {
        return applicationContext;
    }


    public static void engage(Context context, WebEngageConfig dynamicConfig) {
        if (context == null) {
            throw new IllegalArgumentException("Context found null while initializing WebEngage SDK");
        }
        if (self == null) {
            synchronized (engageLock) {
                if (self == null) {
                    isEngageCalled = true;
                    applicationContext = context.getApplicationContext();
                    configPreferenceManager = new ConfigPreferenceManager(applicationContext);
                    try {
                        Logger.setLogLevel(Logger.QUIET);
                        WebEngageConfig manifestConfig = readAndLoadManifest(applicationContext);
                        webEngageConfig = mergeConfig(manifestConfig, dynamicConfig, configPreferenceManager);
                        if (webEngageConfig.getDebugMode()) {
                            Logger.setLogLevel(Logger.DEBUG);
                        } else {
                            Logger.setLogLevel(Logger.QUIET);
                        }
                        Logger.d(WebEngageConstant.TAG, "Initializing WebEngage SDK");
                        boolean isValidConfig = webEngageConfig.isValid(applicationContext);
                        if (isValidConfig) {
                            self = new WebEngage(applicationContext);
                        } else {
                            self = new WebEngageNoOpImpl(webEngageConfig);
                            Logger.e(WebEngageConstant.TAG, "Invalid WebEngage config");
                            Logger.e(WebEngageConstant.TAG, "WebEngage Initialization failed");
                        }
                        try {
                            if (queuedImpl != null) {
                                Queue<Task> taskQueue = queuedImpl.getQueue();
                                if (taskQueue != null && taskQueue.size() > 0) {
                                    Logger.d(WebEngageConstant.TAG, "Executing tasks that have been submitted before webengage initialization, count: " + taskQueue.size());
                                    while (!taskQueue.isEmpty()) {
                                        Task task = taskQueue.poll();
                                        if (task != null) {
                                            if (task instanceof WebEngageTask) {
                                                ((WebEngageTask) task).execute(self);
                                            } else if (task instanceof AnalyticsTask) {
                                                ((AnalyticsTask) task).execute(AnalyticsFactory.getAnalytics(applicationContext));
                                            } else if (task instanceof UserTask) {
                                                ((UserTask) task).execute(UserFactory.getUser(applicationContext, AnalyticsFactory.getAnalytics(applicationContext)));
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Logger.e(WebEngageConstant.TAG, "Some error occurred while executing all queued tasks: " + e.toString());
                        }

                    } catch (Exception e) {
                        Logger.e(WebEngageConstant.TAG, "Some Error occurred during initialization : " + e.toString());
                        Logger.e(WebEngageConstant.TAG, "WebEngage Initialization Failed");
                    }
                }
            }
        }
    }


    static WebEngageConfig readAndLoadManifest(Context context) {
        WebEngageConfig.Builder builder = new WebEngageConfig.Builder();
        Bundle appMetaDataBundle = WebEngageUtils.getApplicationMetaData(context);
        if (appMetaDataBundle != null) {

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_ENVIRONMENT)) {
                builder.setEnvironment(appMetaDataBundle.getString(WebEngageConstant.KEY_ENVIRONMENT));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_ALTERNATE_INTERFACE_ID)) {
                builder.setAlternateInterfaceIdFlag(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_ALTERNATE_INTERFACE_ID));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_PUSH_SMALL_ICON)) {
                builder.setPushSmallIcon(appMetaDataBundle.getInt(WebEngageConstant.KEY_PUSH_SMALL_ICON));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_PUSH_LARGE_ICON)) {
                builder.setPushLargeIcon(appMetaDataBundle.getInt(WebEngageConstant.KEY_PUSH_LARGE_ICON));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_ACCENT_COLOR)) {
                builder.setPushAccentColor(appMetaDataBundle.getInt(WebEngageConstant.KEY_ACCENT_COLOR));
            }
            String channelName = WebEngageConstant.DEFAULT_PUSH_CHANNEL_NAME;
            int importance = WebEngageConstant.DEFAULT_PUSH_CHANNEL_IMPORTANCE;
            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_NAME)) {
                channelName = appMetaDataBundle.getString(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_NAME);
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_IMPORTANCE)) {
                importance = appMetaDataBundle.getInt(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_IMPORTANCE);
            }

            PushChannelConfiguration.Builder pushChannelConfigBuilder = new PushChannelConfiguration.Builder();
            pushChannelConfigBuilder.setNotificationChannelName(channelName);
            pushChannelConfigBuilder.setNotificationChannelImportance(importance);

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_DESCRIPTION)) {
                pushChannelConfigBuilder.setNotificationChannelDescription(appMetaDataBundle.getString(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_DESCRIPTION));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_GROUP)) {
                pushChannelConfigBuilder.setNotificationChannelGroup(appMetaDataBundle.getString(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_GROUP));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_LIGHT_COLOR)) {
                pushChannelConfigBuilder.setNotificationChannelLightColor(appMetaDataBundle.getInt(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_LIGHT_COLOR));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_LOCK_SCREEN_VISIBILITY)) {
                pushChannelConfigBuilder.setNotificationChannelLockScreenVisibility(appMetaDataBundle.getInt(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_LOCK_SCREEN_VISIBILITY));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_SHOW_BADGE)) {
                pushChannelConfigBuilder.setNotificationChannelShowBadge(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_SHOW_BADGE));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_SOUND)) {
                pushChannelConfigBuilder.setNotificationChannelSound(appMetaDataBundle.getString(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_SOUND));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_VIBRATION)) {
                pushChannelConfigBuilder.setNotificationChannelVibration(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_NOTIFICATION_CHANNEL_VIBRATION));
            }

            PushChannelConfiguration pushChannelConfiguration = pushChannelConfigBuilder.build();


            if (pushChannelConfiguration != null) {
                builder.setDefaultPushChannelConfiguration(pushChannelConfiguration);
            }


            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_ENABLE_LOCATION_TRACKING)) {
                builder.setLocationTracking(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_ENABLE_LOCATION_TRACKING));
            }
            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_AUTO_GCM_REGISTRATION)) {
                builder.setAutoGCMRegistrationFlag(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_AUTO_GCM_REGISTRATION));
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_GCM_PROJECT_NUMBER)) {
                Object value = appMetaDataBundle.get(WebEngageConstant.KEY_GCM_PROJECT_NUMBER);
                if (value instanceof String && ((String) value).length() > 0) {
                    builder.setGCMProjectNumber(((String) value).substring(1));
                }
            }
            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_WEBENGAGE_KEY)) {
                Object value = appMetaDataBundle.get(WebEngageConstant.KEY_WEBENGAGE_KEY);
                if (value != null) {
                    builder.setWebEngageKey(value.toString());
                }
            }

            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_DEBUG_MODE)) {
                builder.setDebugMode(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_DEBUG_MODE));
            }
            if (appMetaDataBundle.containsKey(WebEngageConstant.KEY_ENABLE_CRASH_TRACKING)) {
                builder.setEnableCrashTracking(appMetaDataBundle.getBoolean(WebEngageConstant.KEY_ENABLE_CRASH_TRACKING));
            }
        }
        return builder.build();
    }

    protected static WebEngageConfig mergeConfig(WebEngageConfig staticConfig, WebEngageConfig dynamicConfig, ConfigPreferenceManager configPreferenceManager) {
        if (dynamicConfig == null) {
            return staticConfig;
        }
        if (staticConfig == null) {
            return dynamicConfig;
        }
        WebEngageConfig.Builder mergedConfigBuilder = new WebEngageConfig.Builder(configPreferenceManager);

        if (dynamicConfig.isLocationTrackingSet()) {
            mergedConfigBuilder.setLocationTracking(dynamicConfig.getLocationTrackingFlag());
        } else if (staticConfig.isLocationTrackingSet()) {
            mergedConfigBuilder.setLocationTracking(staticConfig.getLocationTrackingFlag());
        }

        if (dynamicConfig.isLocationTrackingStrategySet()) {
            mergedConfigBuilder.setLocationTrackingStrategy(dynamicConfig.getLocationTrackingStrategy());
        }

        if (dynamicConfig.isReportingStrategySet()) {
            mergedConfigBuilder.setEventReportingStrategy(dynamicConfig.getEventReportingStrategy());
        } else if (staticConfig.isReportingStrategySet()) {
            mergedConfigBuilder.setEventReportingStrategy(staticConfig.getEventReportingStrategy());
        }

        mergedConfigBuilder.setAutoGCMRegistrationFlag(dynamicConfig.isAutoGCMRegistrationSet() ? dynamicConfig.getAutoGCMRegistrationFlag() : staticConfig.getAutoGCMRegistrationFlag());
        mergedConfigBuilder.setWebEngageKey(dynamicConfig.isWebEngageKeySet() ? dynamicConfig.getWebEngageKey() : staticConfig.getWebEngageKey());
        mergedConfigBuilder.setGCMProjectNumber(dynamicConfig.isGCMProjectNumberSet() ? dynamicConfig.getGcmProjectNumber() : staticConfig.getGcmProjectNumber());
        mergedConfigBuilder.setWebEngageVersion(dynamicConfig.isWebEngageVersionSet() ? dynamicConfig.getWebEngageVersion() : staticConfig.getWebEngageVersion());
        mergedConfigBuilder.setDebugMode(dynamicConfig.isDebugModeSet() ? dynamicConfig.getDebugMode() : staticConfig.getDebugMode());
        mergedConfigBuilder.setEveryActivityIsScreen(dynamicConfig.isEveryActivityIsScreenSet() ? dynamicConfig.getEveryActivityIsScreen() : staticConfig.getEveryActivityIsScreen());
        mergedConfigBuilder.setEnvironment(dynamicConfig.isEnvironmentSet() ? dynamicConfig.getEnvironment() : staticConfig.getEnvironment());
        mergedConfigBuilder.setAlternateInterfaceIdFlag(dynamicConfig.isAlternateInterfaceIdFlagSet() ? dynamicConfig.getAlternateInterfaceIdFlag() : staticConfig.getAlternateInterfaceIdFlag());
        mergedConfigBuilder.setPushSmallIcon(dynamicConfig.isPushSmallIconSet() ? dynamicConfig.getPushSmallIcon() : staticConfig.getPushSmallIcon());
        mergedConfigBuilder.setPushLargeIcon(dynamicConfig.isPushLargeIconSet() ? dynamicConfig.getPushLargeIcon() : staticConfig.getPushLargeIcon());
        mergedConfigBuilder.setPushAccentColor(dynamicConfig.isAccentColorSet() ? dynamicConfig.getAccentColor() : staticConfig.getAccentColor());
        mergedConfigBuilder.setFilterCustomEvents(dynamicConfig.isFilterCustomEventsSet() ? dynamicConfig.getFilterCustomEvents() : staticConfig.getFilterCustomEvents());
        mergedConfigBuilder.setEnableCrashTracking(staticConfig.isEnableCrashTracking());
        mergedConfigBuilder.setSessionDestroyTime(dynamicConfig.getSessionDestroyTime());
        if (dynamicConfig.isDefaultPushChannelConfigurationSet()) {
            PushChannelConfiguration dynamicPushChannelConfig = dynamicConfig.getDefaultPushChannelConfiguration();
            PushChannelConfiguration.Builder mergedChannelBuilder = new PushChannelConfiguration.Builder();
            mergedChannelBuilder.setNotificationChannelName(dynamicPushChannelConfig.isNotificationChannelNameSet() ? dynamicPushChannelConfig.getNotificationChannelName() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelName());
            mergedChannelBuilder.setNotificationChannelImportance(dynamicPushChannelConfig.isNotificationChannelImportanceSet() ? dynamicPushChannelConfig.getNotificationChannelImportance() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelImportance());
            mergedChannelBuilder.setNotificationChannelDescription(dynamicPushChannelConfig.isNotificationChannelDescriptionSet() ? dynamicPushChannelConfig.getNotificationChannelDescription() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelDescription());
            mergedChannelBuilder.setNotificationChannelGroup(dynamicPushChannelConfig.isNotificationChannelGroupSet() ? dynamicPushChannelConfig.getNotificationChannelGroup() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelGroup());
            mergedChannelBuilder.setNotificationChannelLightColor(dynamicPushChannelConfig.isNotificationLightColorSet() ? dynamicPushChannelConfig.getNotificationChannelLightColor() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelLightColor());
            mergedChannelBuilder.setNotificationChannelLockScreenVisibility(dynamicPushChannelConfig.isNotificationLockScreenVisibilitySet() ? dynamicPushChannelConfig.getNotificationChannelLockScreenVisibility() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelLockScreenVisibility());
            mergedChannelBuilder.setNotificationChannelShowBadge(dynamicPushChannelConfig.isNotificationChannelShowBadgeSet() ? dynamicPushChannelConfig.isNotificationChannelShowBadge() : staticConfig.getDefaultPushChannelConfiguration().isNotificationChannelShowBadge());
            mergedChannelBuilder.setNotificationChannelSound(dynamicPushChannelConfig.isNotificationChannelSoundSet() ? dynamicPushChannelConfig.getNotificationChannelSound() : staticConfig.getDefaultPushChannelConfiguration().getNotificationChannelSound());
            mergedChannelBuilder.setNotificationChannelVibration(dynamicPushChannelConfig.isNotificationChannelVibrationSet() ? dynamicPushChannelConfig.isNotificationChannelVibration() : staticConfig.getDefaultPushChannelConfiguration().isNotificationChannelVibration());
            mergedConfigBuilder.setDefaultPushChannelConfiguration(mergedChannelBuilder.build());
        } else {
            mergedConfigBuilder.setDefaultPushChannelConfiguration(staticConfig.getDefaultPushChannelConfiguration());
        }

        return mergedConfigBuilder.build();

    }


    @Override
    public WebEngageConfig getWebEngageConfig() {
        return webEngageConfig;
    }


    void setWebEngageConfig(WebEngageConfig config) {
        webEngageConfig = config;
    }

    void setApplicationContext(Context context) {
        applicationContext = context.getApplicationContext();
    }

    @Override
    public void setLocationTracking(boolean state) {
        if (webEngageConfig == null) {
            return;
        }
        try {
            webEngageConfig = webEngageConfig.getCurrentState().setLocationTracking(state).build();
            if (state) {
                LocationManagerFactory.getLocationManager(applicationContext).registerLocationUpdates(WebEngageConstant.LOCATION_INTERVAL_CITY, WebEngageConstant.LOCATION_FASTEST_INTERVAL_CITY, WebEngageConstant.DISTANCE_THRESHOLD_CITY, 104);
            } else {
                LocationManagerFactory.getLocationManager(applicationContext).unregisterLocationUpdates();
            }
            configPreferenceManager.saveLocationTrackingFlag(state);
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred during changing location tracking flag : " + e.toString());
        }
    }

    @Override
    public void setLocationTrackingStrategy(LocationTrackingStrategy locationTrackingStrategy) {
        if (webEngageConfig == null || locationTrackingStrategy == null) {
            return;
        }
        try {
            webEngageConfig = webEngageConfig.getCurrentState().setLocationTrackingStrategy(locationTrackingStrategy).build();
            switch (locationTrackingStrategy) {
                case ACCURACY_BEST:
                    LocationManagerFactory.getLocationManager(applicationContext).registerLocationUpdates(WebEngageConstant.LOCATION_INTERVAL_BEST, WebEngageConstant.LOCATION_FASTEST_INTERVAL_BEST, WebEngageConstant.DISTANCE_THRESHOLD_BEST, 100);
                    break;
                case ACCURACY_CITY:
                    LocationManagerFactory.getLocationManager(applicationContext).registerLocationUpdates(WebEngageConstant.LOCATION_INTERVAL_CITY, WebEngageConstant.LOCATION_FASTEST_INTERVAL_CITY, WebEngageConstant.DISTANCE_THRESHOLD_CITY, 104);
                    break;
                case ACCURACY_COUNTRY:
                    LocationManagerFactory.getLocationManager(applicationContext).registerLocationUpdates(WebEngageConstant.LOCATION_INTERVAL_COUNTRY, WebEngageConstant.LOCATION_FASTEST_INTERVAL_COUNTRY, WebEngageConstant.DISTANCE_THRESHOLD_COUNTRY, 104);
                    break;
                case DISABLED:
                    LocationManagerFactory.getLocationManager(applicationContext).unregisterLocationUpdates();
                    break;
            }
            configPreferenceManager.saveLocationTrackingStrategy(locationTrackingStrategy);
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Some error occurred while setting location tracking strategy: " + e.toString());
        }
    }

    @Override
    public void setLogLevel(int logLevel) {
        Logger.setLogLevel(logLevel);
    }

    @Override
    public void setEventReportingStrategy(ReportingStrategy reportingStrategy) {
        if (reportingStrategy == null) {
            Logger.e(WebEngageConstant.TAG, "Reporting Strategy is null");
            return;
        }
        webEngageConfig = webEngageConfig.getCurrentState().setEventReportingStrategy(reportingStrategy).build();
        configPreferenceManager.saveEventReportingStrategy(reportingStrategy);
    }

    @Override
    public void filterCustomEvents(boolean filterCustomEvents) {
        webEngageConfig = webEngageConfig.getCurrentState().setFilterCustomEvents(filterCustomEvents).build();
    }

    @Override
    public void setEveryActivityIsScreen(boolean everyActivityIsScreen) {
        webEngageConfig = webEngageConfig.getCurrentState().setEveryActivityIsScreen(everyActivityIsScreen).build();
    }

    @Override
    public void setRegistrationID(String registrationID, String gcmProjectNumber) {
        if (registrationID == null || registrationID.isEmpty()) {
            Logger.e(WebEngageConstant.TAG, "Invalid GCM Parameters");
            return;
        }

        if (registrationID.equals(analytics().getPreferenceManager().getRegistrationID())) {
            return;
        } else {
            // shared preference is by default thread safe
            analytics().getPreferenceManager().saveRegistrationID(registrationID);
            webEngageConfig = webEngageConfig.getCurrentState().setGCMProjectNumber(gcmProjectNumber).build();
            Map<String, Object> eventData = new HashMap<String, Object>();
            eventData.put("gcm_regId", registrationID);
            if (gcmProjectNumber != null) {
                eventData.put("gcm_project_number", gcmProjectNumber);
            }
            eventData.put("provider", FCM.name());
            dispatchGCMRegisteredEvent(eventData);
        }

    }

    @Override
    public void setRegistrationID(String registrationID) {
        this.setRegistrationID(registrationID, null);
    }

    @Override
    public void setXiaomiRegistrationID(String registrationID) {
        if (registrationID == null || registrationID.isEmpty()) {
            Logger.e(WebEngageConstant.TAG, "Invalid Xiaomi Token");
            return;
        }
        if (registrationID.equals(analytics().getPreferenceManager().getXiaomiRegistrationID())) {
            return;
        } else {
            // shared preference is by default thread safe
            analytics().getPreferenceManager().saveXiaomiRegistrationID(registrationID);
            Map<String, Object> eventData = new HashMap<String, Object>();
            eventData.put("gcm_regId", registrationID);
            eventData.put("provider", MI.name());
            dispatchGCMRegisteredEvent(eventData);
        }
    }

    private void dispatchGCMRegisteredEvent(Map<String, Object> eventData) {
        Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.GCM_REGISTERED,
                null, eventData, null, applicationContext), applicationContext);
        WebEngage.startService(intent, applicationContext);
    }

    @Override
    public void setHuaweiRegistrationID(String registrationID) {
        if (registrationID == null || registrationID.isEmpty()) {
            Logger.e(WebEngageConstant.TAG, "Invalid Huawei Token");
            return;
        }
        if (registrationID.equals(analytics().getPreferenceManager().getHuaweiRegistrationID())) {
            return;
        } else {
            // shared preference is by default thread safe
            analytics().getPreferenceManager().saveHuaweiRegistrationID(registrationID);
            Map<String, Object> eventData = new HashMap<String, Object>();
            eventData.put("gcm_regId", registrationID);
            eventData.put("provider", HW.name());
            dispatchGCMRegisteredEvent(eventData);
        }
    }

    @Override
    public void receive(Intent intent) {
        if (intent == null) {
            Logger.e(WebEngageConstant.TAG, "Push intent is null");
            return;
        }
        dispatchGCMMessage(intent);
    }

    @Override
    public void receive(Bundle data) {
        if (data == null) {
            Logger.e(WebEngageConstant.TAG, "Push bundle is null");
        } else {
            Intent intent = new Intent();
            intent.putExtras(data);
            this.receive(intent);
        }
    }

    @Override
    public void receive(Map<String, String> data) {
        if (data == null) {
            Logger.e(WebEngageConstant.TAG, "Push data is null");
        } else {
            Bundle bundle = WebEngageUtils.convertMapToBundle(data);
            if (bundle != null) {
                this.receive(bundle);
            }
        }
    }

    @Override
    public Analytics analytics() {
        return AnalyticsFactory.getAnalytics(applicationContext);
    }

    @Override
    public User user() {
        return UserFactory.getUser(applicationContext, analytics());
    }

    @Override
    public RuleExecutor ruleExecutor() {
        return RuleExecutorFactory.getRuleExecutor();
    }

    @Override
    protected void dispatchGCMMessage(Intent intent) {
        Intent gcmIntent = IntentFactory.newIntent(Topic.GCM_MESSAGE, intent.getExtras(), applicationContext);
        WebEngage.startService(gcmIntent, applicationContext);
    }

    @Override
    protected void dispatchDeeplinkIntent(Intent intent, BroadcastReceiver broadcastReceiver) {
        try {
            boolean isClickHandledByClient = false;
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    boolean isPushDeeplink = extras.containsKey(WebEngageConstant.HASHED_NOTIFICATION_ID);
                    if (isPushDeeplink) {
                        PushNotificationData pushNotificationData = null;
                        try {
                            JSONObject properties = new JSONObject(analytics().getPreferenceManager().getVolatileData(extras.getString(WebEngageConstant.NOTIFICATION_ID)));
                            pushNotificationData = new PushNotificationData(properties, applicationContext);
                        } catch (JSONException e) {
                            Logger.e(WebEngageConstant.TAG, "Exception while getting push notification data from sharedprefs", e);
                            analytics().dispatchExceptionTopic(e);
                        }

                        boolean isClick = extras.containsKey(WebEngageConstant.NOTIFICATION_MAIN_INTENT);
                        if (isClick) {
                            // dismiss notification
                            boolean shouldDismissOnClick = extras.getBoolean(WebEngageConstant.DISMISS_ON_CLICK, true);
                            if (shouldDismissOnClick) {
                                NotificationManager manager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
                                manager.cancel(extras.getInt(WebEngageConstant.HASHED_NOTIFICATION_ID, -1));

                                //close system tray
                            }
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                                Intent closeSystemTray = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                                applicationContext.sendBroadcast(closeSystemTray);
                            }
                            String eventName = EventName.PUSH_NOTIFICATION_CLICK;
                            if (extras.containsKey(WebEngageConstant.EVENT)) {
                                eventName = extras.getString(WebEngageConstant.EVENT);
                            }

                            Map<String, Object> systemData = new HashMap<String, Object>();
                            systemData.put(WebEngageConstant.NOTIFICATION_ID, extras.getString(WebEngageConstant.NOTIFICATION_ID));
                            systemData.put(WebEngageConstant.CTA_ID, extras.getString(WebEngageConstant.CTA_ID));
                            systemData.put(WebEngageConstant.EXPERIMENT_ID, extras.getString(WebEngageConstant.EXPERIMENT_ID));

                            Map<String, Object> extraData = new HashMap<String, Object>();
                            extraData.put(WebEngageConstant.DISMISS_ON_CLICK, shouldDismissOnClick);

                            Map<String, Object> eventData = WebEngageUtils.bundleToMap(extras.getBundle(WebEngageConstant.EVENT_DATA));

                            if (pushNotificationData != null) {
                                if (eventData == null) {
                                    eventData = new HashMap<String, Object>();
                                }
                                eventData.put(WebEngageConstant.AMPLIFIED, pushNotificationData.isAmplified());
                            }

                            analytics().dispatchEventTopic(EventFactory.newSystemEvent(eventName, systemData, eventData, extraData, applicationContext));

                            if (pushNotificationData != null) {
                                if (extras.getBoolean(WebEngageConstant.NOTIFICATION_MAIN_INTENT)) {
                                    isClickHandledByClient = CallbackDispatcher.init(applicationContext).onPushNotificationClicked(applicationContext, pushNotificationData);
                                } else if (!extras.getBoolean(WebEngageConstant.NOTIFICATION_MAIN_INTENT)) {
                                    if (EventName.PUSH_NOTIFICATION_RATING_SUBMITTED.equals(eventName)) {
                                        pushNotificationData.getRatingV1().setRateValue((int) eventData.get(WebEngageConstant.RATE_VALUE));
                                    }
                                    isClickHandledByClient = CallbackDispatcher.init(applicationContext).onPushNotificationActionClicked(applicationContext, pushNotificationData, extras.getString(WebEngageConstant.CTA_ID));
                                }
                                if (!isClickHandledByClient) {
                                    if (ReflectionUtils.isCorodvaPresent()) {
                                        String ctaId = extras.getString(WebEngageConstant.CTA_ID);
                                        Bundle customData = pushNotificationData.getCustomData();
                                        customData.putString("we_pushPayload",pushNotificationData.getPushPayloadJSON().toString());
                                        String uri = null;
                                        if (ctaId != null) {
                                            CallToAction callToAction = pushNotificationData.getCallToActionById(ctaId);
                                            if (callToAction != null) {
                                                uri = callToAction.getAction();
                                            }
                                        }
                                        ReflectionUtils.invokeStaticMethod("com.webengage.cordova.WebEngagePlugin", "handlePushClick", new Class[]{String.class, Bundle.class}, new Object[]{uri, customData});
                                    }
                                }
                            }

                            if (!isClickHandledByClient) {
                                Intent deeplinkIntent = IntentFactory.newIntent(Topic.DEEPLINK, intent, applicationContext);
                                WebEngage.startService(deeplinkIntent, applicationContext, broadcastReceiver);
                            }
                        } else {
                            //push notification close
                            if (pushNotificationData != null) {
                                Map<String, Object> systemData = new HashMap<String, Object>();
                                systemData.put(WebEngageConstant.NOTIFICATION_ID, pushNotificationData.getVariationId());
                                systemData.put(WebEngageConstant.EXPERIMENT_ID, pushNotificationData.getExperimentId());
                                if (extras.containsKey(WebEngageConstant.CTA_ID)) {
                                    systemData.put(WebEngageConstant.CTA_ID, extras.getString(WebEngageConstant.CTA_ID));
                                }

                                Map<String, Object> eventData = new HashMap<String, Object>();
                                eventData.put(WebEngageConstant.AMPLIFIED, pushNotificationData.isAmplified());

                                analytics().dispatchEventTopic(EventFactory.newSystemEvent(EventName.PUSH_NOTIFICATION_CLOSE, systemData, eventData, null, applicationContext));
                                CallbackDispatcher.init(applicationContext).onPushNotificationDismissed(applicationContext, pushNotificationData);
                            }
                        }
                    } else {
                        //in app click deep-link
                        Intent deeplinkIntent = IntentFactory.newIntent(Topic.DEEPLINK, intent, applicationContext);
                        WebEngage.startService(deeplinkIntent, applicationContext, broadcastReceiver);
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while handling notification click/close", e);
        }
    }

    @Override
    protected void dispatchLocation(Location location, BroadcastReceiver broadcastReceiver) {
        Map<String, Object> systemData = new HashMap<String, Object>();
        systemData.put(UserDeviceAttribute.LATITUDE, location.getLatitude());
        systemData.put(UserDeviceAttribute.LONGITUDE, location.getLongitude());
        Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.USER_UPDATE_GEO_INFO, systemData, null, null, applicationContext), applicationContext);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchSessionDestroy(BroadcastReceiver broadcastReceiver) {
        Intent intent = new Intent(applicationContext, ExecutorService.class);
        intent.putExtra(ExecutorService.ACTION_NAME, ExecutorService.SESSION_DESTROY);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchFlushAction(BroadcastReceiver broadcastReceiver) {
        Intent intent = IntentFactory.newIntent(Topic.REPORT, null, applicationContext);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchConfigRefreshPing(BroadcastReceiver broadcastReceiver) {
        Intent intent = IntentFactory.newIntent(Topic.CONFIG_REFRESH, null, applicationContext);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchSessionDelay(Intent intent, BroadcastReceiver broadcastReceiver) {
        long delayValue = intent.getLongExtra(WebEngageReceiver.DELAY_VALUE, 0l);
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("value", delayValue + 1);
        Intent sessionDelayIntent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_SESSION_DELAY, null, eventData, null, applicationContext), applicationContext);
        WebEngage.startService(sessionDelayIntent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchPageDelay(Intent intent, BroadcastReceiver broadcastReceiver) {
        long delayValue = intent.getLongExtra(WebEngageReceiver.DELAY_VALUE, 0l);
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("value", delayValue + 1);
        Intent pageDelayIntent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_PAGE_DELAY, null, eventData, null, applicationContext), applicationContext);
        WebEngage.startService(pageDelayIntent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchLeaveIntent(Intent intent) {
        Map<String, Object> eventData = new HashMap<String, Object>();
        eventData.put("value", true);
        Intent leaveIntent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_LEAVE_INTENT, null, eventData, null, applicationContext), applicationContext);
        WebEngage.startService(leaveIntent, applicationContext);
    }

    @Override
    protected void dispatchGeoFenceTransition(LocationManagerImpl.GeoFenceTransition geoFenceTransition, BroadcastReceiver broadcastReceiver) {
        if (geoFenceTransition != null) {
            Map<String, Object> systemData = new HashMap<String, Object>();
            systemData.put("geofence_id", geoFenceTransition.getId());
            systemData.put("transition_type", geoFenceTransition.getTransition());
            if (geoFenceTransition.getLocation() != null) {
                systemData.put(UserDeviceAttribute.LATITUDE, geoFenceTransition.getLocation().getLatitude());
                systemData.put(UserDeviceAttribute.LONGITUDE, geoFenceTransition.getLocation().getLongitude());
            }
            Intent intent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.GEOFENCE_TRANSITION, systemData, null, null, applicationContext), applicationContext);
            WebEngage.startService(intent, applicationContext, broadcastReceiver);
        }
    }

    @Override
    protected void dispatchUserProfileFetchCall(BroadcastReceiver broadcastReceiver) {
        Intent intent = IntentFactory.newIntent(Topic.FETCH_PROFILE, null, applicationContext);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    protected void dispatchJourneyContext(BroadcastReceiver broadcastReceiver) {
        Intent intent = IntentFactory.newIntent(Topic.JOURNEY_CONTEXT, null, applicationContext);
        WebEngage.startService(intent, applicationContext, broadcastReceiver);
    }

    @Override
    protected void dispatchPushNotificationRerender(Intent intent) {
        Map<String, Object> systemData = null;
        Map<String, Object> eventData = null;
        Map<String, Object> extraData = null;

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String notificationId = bundle.getString(WebEngageConstant.NOTIFICATION_ID);
            String experimentId = bundle.getString(WebEngageConstant.EXPERIMENT_ID);

            systemData = new HashMap<>();
            systemData.put(WebEngageConstant.NOTIFICATION_ID, notificationId);
            systemData.put(WebEngageConstant.EXPERIMENT_ID, experimentId);

            eventData = WebEngageUtils.bundleToMap(bundle.getBundle(WebEngageConstant.EVENT_DATA));
            extraData = WebEngageUtils.bundleToMap(bundle.getBundle(WebEngageConstant.EXTRA_DATA));
        }

        Intent notificationRerenderIntent = IntentFactory.newIntent(Topic.INTERNAL_EVENT, EventFactory.newSystemEvent(EventName.WE_WK_PUSH_NOTIFICATION_RERENDER, systemData, eventData, extraData, applicationContext), applicationContext);
        WebEngage.startService(notificationRerenderIntent, applicationContext, null);
    }

    @Override
    protected void dispatchAmplify(BroadcastReceiver broadcastReceiver) {
        Intent intent = IntentFactory.newIntent(Topic.AMPLIFY, null, applicationContext);
        startService(intent, applicationContext, broadcastReceiver);
    }


    public static void setInlinePersonalizationListener(InLinePersonalizationListener inlinePersonalizationListener) {
       Logger.d(WebEngageConstant.TAG, " setInlinePersonalizationListener ");
        CallbackDispatcher.registerInlinePersonalizationListener(inlinePersonalizationListener);
    }

    public static void startService(Intent intent, Context context) {
        startService(intent, context, null);
    }

    public static void startService(Intent intent, Context context, BroadcastReceiver broadcastReceiver) {
        if (context == null || intent == null) {
            Logger.e(WebEngageConstant.TAG, "Invalid Parameters to start a service");
            return;
        }
        YetAnotherIntentServiceManager.startService(context, intent, broadcastReceiver);
    }

}

