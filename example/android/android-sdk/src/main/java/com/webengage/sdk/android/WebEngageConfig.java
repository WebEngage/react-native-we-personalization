package com.webengage.sdk.android;


import android.content.Context;
import android.os.Build;

import com.webengage.sdk.android.actions.database.ReportingStrategy;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public class WebEngageConfig {
    private boolean locationTracking;
    private LocationTrackingStrategy locationTrackingStrategy;
    private boolean autoGCMRegistration;
    private String webEngageKey;
    private String gcmProjectNumber;
    private ReportingStrategy reportingStrategy;
    private String webEngageVersion;
    private boolean debugMode;
    private boolean everyActivityIsScreen;
    private String environment;
    private boolean alternateInterfaceIdFlag;
    private int pushSmallIcon;
    private int pushLargeIcon;
    private int accentColor;
    private boolean filterCustomEvents;
    private PushChannelConfiguration defaultPushChannelConfiguration;

    private boolean isLocationTrackingSet;
    private boolean isLocationTrackingStrategySet;
    private boolean isAutoGCMRegistrationSet;
    private boolean isWebEngageKeySet;
    private boolean isGCMProjectNumberSet;
    private boolean isWebEngageVersionSet;
    private boolean isReportingStrategySet;
    private boolean isDebugModeSet;
    private boolean isEveryActivityIsScreenSet;
    private boolean isEnvironmentSet;
    private boolean isAlternateInterfaceIdFlagSet;
    private boolean isPushSmallIconSet;
    private boolean isPushLargeIconSet;
    private boolean isAccentColorSet;
    private boolean isFilterCustomEventsSet;
    private boolean isDefaultPushChannelConfigurationSet;
    private boolean isEnableCrashTracking;
    private long sessionDestroyTime = -1;

    public long getSessionDestroyTime() {
        return sessionDestroyTime;
    }

    private WebEngageConfig(Builder builder) {
        this.locationTracking = builder.locationTracking.get();
        this.locationTrackingStrategy = builder.locationTrackingStrategy;
        this.autoGCMRegistration = builder.autoGCMRegistration;
        this.webEngageKey = builder.webEngageKey;
        this.gcmProjectNumber = builder.gcmProjectNumber;
        this.reportingStrategy = builder.reportingStrategy;
        this.webEngageVersion = builder.webEngageVersion;
        this.debugMode = builder.debugMode;
        this.everyActivityIsScreen = builder.everyActivityIsScreen;
        this.environment = builder.environment;
        this.alternateInterfaceIdFlag = builder.alternateInterfaceIdFlag;
        this.pushSmallIcon = builder.pushSmallIcon;
        this.pushLargeIcon = builder.pushLargeIcon;
        this.accentColor = builder.accentColor;
        this.filterCustomEvents = builder.filterCustomEvents;
        this.defaultPushChannelConfiguration = builder.defaultPushChannelConfiguration;

        this.isLocationTrackingSet = builder.isLocationTrackingSet;
        this.isLocationTrackingStrategySet = builder.isLocationTrackingStrategySet;
        this.isAutoGCMRegistrationSet = builder.isAutoGCMRegistrationSet;
        this.isWebEngageKeySet = builder.isWebEngageKeySet;
        this.isGCMProjectNumberSet = builder.isGCMProjectNumberSet;
        this.isWebEngageVersionSet = builder.isWebEngageVersionSet;
        this.isReportingStrategySet = builder.isReportingStrategySet;
        this.isDebugModeSet = builder.isDebugModeSet;
        this.isEveryActivityIsScreenSet = builder.isEveryActivityIsScreenSet;
        this.isEnvironmentSet = builder.isEnvironmentSet;
        this.isAlternateInterfaceIdFlagSet = builder.isAlternateInterfaceIdFlagSet;
        this.isPushSmallIconSet = builder.isPushSmallIconSet;
        this.isPushLargeIconSet = builder.isPushLargeIconSet;
        this.isAccentColorSet = builder.isAccentColorSet;
        this.isFilterCustomEventsSet = builder.isFilterCustomEventsSet;
        this.isDefaultPushChannelConfigurationSet = builder.isDefaultPushChannelConfigurationSet;
        this.isEnableCrashTracking = builder.isEnableCrashTracking;
        this.sessionDestroyTime = builder.sessionDestroyTime;
    }

    @Deprecated
    public boolean getLocationTrackingFlag() {
        return this.locationTracking;
    }

    public LocationTrackingStrategy getLocationTrackingStrategy() {
        return this.locationTrackingStrategy;
    }

    public boolean getAutoGCMRegistrationFlag() {
        return this.autoGCMRegistration;
    }

    public String getWebEngageKey() {
        return this.webEngageKey;
    }

    public String getGcmProjectNumber() {
        return this.gcmProjectNumber;
    }

    public String getWebEngageVersion() {
        return this.webEngageVersion;
    }

    public ReportingStrategy getEventReportingStrategy() {
        return this.reportingStrategy;
    }

    public boolean getDebugMode() {
        return this.debugMode;
    }

    public boolean getEveryActivityIsScreen() {
        return this.everyActivityIsScreen;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public boolean getAlternateInterfaceIdFlag() {
        return this.alternateInterfaceIdFlag;
    }

    public int getPushSmallIcon() {
        return this.pushSmallIcon;
    }

    public int getPushLargeIcon() {
        return this.pushLargeIcon;
    }

    public int getAccentColor() {
        return this.accentColor;
    }

    public boolean getFilterCustomEvents() {
        return this.filterCustomEvents;
    }

    public PushChannelConfiguration getDefaultPushChannelConfiguration() {
        return this.defaultPushChannelConfiguration;
    }

    public boolean isLocationTrackingEnabled() {
        if (this.isLocationTrackingStrategySet()) {
            return this.getLocationTrackingStrategy() != LocationTrackingStrategy.DISABLED;
        } else {
            return this.getLocationTrackingFlag();
        }
    }

    protected boolean isLocationTrackingSet() {
        return this.isLocationTrackingSet;
    }

    protected boolean isLocationTrackingStrategySet() {
        return this.isLocationTrackingStrategySet;
    }

    protected boolean isAutoGCMRegistrationSet() {
        return this.isAutoGCMRegistrationSet;
    }

    protected boolean isWebEngageKeySet() {
        return this.isWebEngageKeySet;
    }

    protected boolean isGCMProjectNumberSet() {
        return this.isGCMProjectNumberSet;
    }

    protected boolean isWebEngageVersionSet() {
        return this.isWebEngageVersionSet;
    }

    protected boolean isReportingStrategySet() {
        return this.isReportingStrategySet;
    }

    protected boolean isDebugModeSet() {
        return this.isDebugModeSet;
    }

    protected boolean isEveryActivityIsScreenSet() {
        return this.isEveryActivityIsScreenSet;
    }

    protected boolean isEnvironmentSet() {
        return this.isEnvironmentSet;
    }

    protected boolean isAlternateInterfaceIdFlagSet() {
        return this.isAlternateInterfaceIdFlagSet;
    }

    protected boolean isPushSmallIconSet() {
        return this.isPushSmallIconSet;
    }

    protected boolean isPushLargeIconSet() {
        return this.isPushLargeIconSet;
    }

    protected boolean isAccentColorSet() {
        return this.isAccentColorSet;
    }

    protected boolean isFilterCustomEventsSet() {
        return this.isFilterCustomEventsSet;
    }

    protected boolean isDefaultPushChannelConfigurationSet() {
        return this.isDefaultPushChannelConfigurationSet;
    }

    public boolean isEnableCrashTracking() {
        return isEnableCrashTracking;
    }

    public Builder getCurrentState() {
        Builder builder = new Builder();
        if (this.isLocationTrackingSet()) {
            builder.setLocationTracking(this.getLocationTrackingFlag());
        }
        if (this.isLocationTrackingStrategySet()) {
            builder.setLocationTrackingStrategy(this.getLocationTrackingStrategy());
        }
        if (this.isAutoGCMRegistrationSet()) {
            builder.setAutoGCMRegistrationFlag(this.getAutoGCMRegistrationFlag());
        }
        if (this.isWebEngageKeySet()) {
            builder.setWebEngageKey(this.getWebEngageKey());
        }
        if (this.isGCMProjectNumberSet()) {
            builder.setGCMProjectNumber(this.getGcmProjectNumber());
        }
        if (this.isWebEngageVersionSet()) {
            builder.setWebEngageVersion(this.getWebEngageVersion());
        }

        if (this.isReportingStrategySet()) {
            builder.setEventReportingStrategy(this.getEventReportingStrategy());
        }
        if (this.isDebugModeSet()) {
            builder.setDebugMode(this.getDebugMode());
        }

        if (this.isEveryActivityIsScreenSet()) {
            builder.setEveryActivityIsScreen(this.getEveryActivityIsScreen());
        }

        if (this.isEnvironmentSet()) {
            builder.setEnvironment(this.getEnvironment());
        }

        if (this.isAlternateInterfaceIdFlagSet()) {
            builder.setAlternateInterfaceIdFlag(this.getAlternateInterfaceIdFlag());
        }

        if (this.isPushSmallIconSet()) {
            builder.setPushSmallIcon(this.getPushSmallIcon());
        }

        if (this.isPushLargeIconSet()) {
            builder.setPushLargeIcon(this.getPushLargeIcon());
        }
        if (this.isAccentColorSet()) {
            builder.setPushAccentColor(this.getAccentColor());
        }
        if (this.isFilterCustomEventsSet()) {
            builder.setFilterCustomEvents(this.getFilterCustomEvents());
        }
        if (this.isDefaultPushChannelConfigurationSet()) {
            builder.setDefaultPushChannelConfiguration(this.getDefaultPushChannelConfiguration());
        }
        builder.setEnableCrashTracking(this.isEnableCrashTracking());
        builder.setSessionDestroyTime(this.sessionDestroyTime);

        return builder;
    }


    public boolean isValid(Context context) {
        if (context != null) {
            Context applicationContext = context.getApplicationContext();
            if (WebEngageUtils.isEmpty(this.getWebEngageKey())) {
                Logger.e(WebEngageConstant.TAG, "WebEngage key not found");
                return false;
            }

            if (this.getAutoGCMRegistrationFlag() && WebEngageUtils.isEmpty(this.getGcmProjectNumber())) {
                Logger.e(WebEngageConstant.TAG, "GCM project number not found");
                return false;
            }

            if (!WebEngageConstant.GCE.equals(this.getEnvironment())
                    && !WebEngageConstant.AWS.equals(this.getEnvironment())
                    && !WebEngageConstant.IN.equals(this.getEnvironment())
                    && !WebEngageConstant.IR0.equals(this.getEnvironment())
                    && !WebEngageConstant.UNL.equals(this.getEnvironment())) {
                Logger.e(WebEngageConstant.TAG, "Invalid value for Environment provided");
                return false;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !this.getDefaultPushChannelConfiguration().isValid(applicationContext)) {
                Logger.e(WebEngageConstant.TAG, "Invalid Push channel configuration found");
                return false;
            }

            return true;
        }

        return false;
    }


    public static class Builder {
        private AtomicBoolean locationTracking = new AtomicBoolean(false);
        private LocationTrackingStrategy locationTrackingStrategy = LocationTrackingStrategy.ACCURACY_CITY;
        private boolean autoGCMRegistration = false;
        private String webEngageKey = null;
        private String gcmProjectNumber = null;
        private String webEngageVersion = BuildConfig.MAJOR + "." + BuildConfig.MINOR + "." + BuildConfig.PATCH;
        private ReportingStrategy reportingStrategy = ReportingStrategy.BUFFER;
        private boolean debugMode = false;
        private boolean everyActivityIsScreen = false;
        private String environment = WebEngageConstant.AWS;
        private boolean alternateInterfaceIdFlag = false;
        private int pushSmallIcon = -1;
        private int pushLargeIcon = -1;
        private int accentColor = -1;
        private boolean filterCustomEvents = false;
        private PushChannelConfiguration defaultPushChannelConfiguration = new PushChannelConfiguration.Builder().build();

        private boolean isLocationTrackingSet = false;
        private boolean isLocationTrackingStrategySet = false;
        private boolean isAutoGCMRegistrationSet = false;
        private boolean isWebEngageKeySet = false;
        private boolean isGCMProjectNumberSet = false;
        private boolean isWebEngageVersionSet = false;
        private boolean isReportingStrategySet = false;
        private boolean isDebugModeSet = false;
        private boolean isEveryActivityIsScreenSet = false;
        private boolean isEnvironmentSet = false;
        private boolean isAlternateInterfaceIdFlagSet = false;
        private boolean isPushSmallIconSet = false;
        private boolean isPushLargeIconSet = false;
        private boolean isAccentColorSet = false;
        private boolean isFilterCustomEventsSet = false;
        private boolean isDefaultPushChannelConfigurationSet = false;
        private boolean isEnableCrashTracking = true;
        private long sessionDestroyTime = -1;

        public Builder() {

        }

        protected Builder(ConfigPreferenceManager configPreferenceManager) {
            this.locationTracking.set(configPreferenceManager.getLocationTrackingFlag());
            this.isLocationTrackingSet = configPreferenceManager.getPreferenceFile(BasePreferenceManager.DEFAULT_PREFS).contains(ConfigPreferenceManager.LOCATION_TRACKING_FLAG);


            this.locationTrackingStrategy = configPreferenceManager.getLocationTrackingStrategy();
            this.isLocationTrackingStrategySet = configPreferenceManager.getPreferenceFile(BasePreferenceManager.DEFAULT_PREFS).contains(ConfigPreferenceManager.LOCATION_TRACKING_STRATEGY);

            this.reportingStrategy = configPreferenceManager.getEventReportingStrategy();
            this.isReportingStrategySet = configPreferenceManager.getPreferenceFile(BasePreferenceManager.DEFAULT_PREFS).contains(ConfigPreferenceManager.EVENT_REPORTING_STRATEGY);
        }

        @Deprecated
        public Builder setLocationTracking(boolean state) {
            this.locationTracking.set(state);
            this.isLocationTrackingSet = true;
            return this;
        }

        public Builder setLocationTrackingStrategy(LocationTrackingStrategy strategy) {
            this.locationTrackingStrategy = strategy;
            this.isLocationTrackingStrategySet = true;
            return this;
        }

        public Builder setAutoGCMRegistrationFlag(boolean state) {
            this.autoGCMRegistration = state;
            this.isAutoGCMRegistrationSet = true;
            return this;
        }

        protected Builder setEnableCrashTracking(boolean enableCrashTracking) {
            isEnableCrashTracking = enableCrashTracking;
            return this;
        }

        public Builder setWebEngageKey(String key) {
            this.webEngageKey = key;
            this.isWebEngageKeySet = true;
            return this;
        }

        public Builder setGCMProjectNumber(String projectNumber) {
            this.gcmProjectNumber = projectNumber;
            this.isGCMProjectNumberSet = true;
            return this;
        }

        protected Builder setWebEngageVersion(String version) {
            this.webEngageVersion = version;
            this.isWebEngageVersionSet = true;
            return this;
        }

        public Builder setEventReportingStrategy(ReportingStrategy reportingStrategy) {
            this.reportingStrategy = reportingStrategy;
            this.isReportingStrategySet = true;
            return this;
        }

        public Builder setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
            this.isDebugModeSet = true;
            return this;
        }

        public Builder setEveryActivityIsScreen(boolean everyActivityIsScreen) {
            this.everyActivityIsScreen = everyActivityIsScreen;
            this.isEveryActivityIsScreenSet = true;
            return this;
        }

        protected Builder setEnvironment(String environment) {
            if (WebEngageConstant.IN.equalsIgnoreCase(environment)) {
                this.environment = WebEngageConstant.IN;
            } else if (WebEngageConstant.GCE.equalsIgnoreCase(environment)) {
                this.environment = WebEngageConstant.GCE;
            } else if (WebEngageConstant.IR0.equalsIgnoreCase(environment)) {
                this.environment = WebEngageConstant.IR0;
            } else if (WebEngageConstant.UNL.equalsIgnoreCase(environment)) {
                this.environment = WebEngageConstant.UNL;
            } else {
                this.environment = WebEngageConstant.AWS;
            }
            this.isEnvironmentSet = true;
            return this;
        }

        /**
         * @param alternateInterfaceIdFlag
         * @return
         * @deprecated WebEngage by default supports alternate Application support, this will be removed in 3.18.9
         */
        @Deprecated
        protected Builder setAlternateInterfaceIdFlag(boolean alternateInterfaceIdFlag) {
            this.alternateInterfaceIdFlag = alternateInterfaceIdFlag;
            this.isAlternateInterfaceIdFlagSet = true;
            return this;
        }

        public Builder setPushSmallIcon(int pushSmallIcon) {
            this.pushSmallIcon = pushSmallIcon;
            this.isPushSmallIconSet = true;
            return this;
        }

        public Builder setPushLargeIcon(int pushLargeIcon) {
            this.pushLargeIcon = pushLargeIcon;
            this.isPushLargeIconSet = true;
            return this;
        }

        public Builder setPushAccentColor(int accentColor) {
            this.accentColor = accentColor;
            this.isAccentColorSet = true;
            return this;
        }

        protected Builder setFilterCustomEvents(boolean filterCustomEvents) {
            this.filterCustomEvents = filterCustomEvents;
            this.isFilterCustomEventsSet = true;
            return this;
        }

        public Builder setDefaultPushChannelConfiguration(PushChannelConfiguration pushChannelConfiguration) {
            this.defaultPushChannelConfiguration = pushChannelConfiguration;
            this.isDefaultPushChannelConfigurationSet = true;
            return this;
        }

        /**
         * This method will allow to set the session destroy time which
         * is responsible to reset the session once
         * the application goes in background.
         *
         * @param sessionDestroyTimeInSecs time in seconds
         **/
        public Builder setSessionDestroyTime(long sessionDestroyTimeInSecs) {
            this.sessionDestroyTime = sessionDestroyTimeInSecs;
            return this;
        }

        public WebEngageConfig build() {
            WebEngageConfig webEngageConfig = new WebEngageConfig(this);
            return webEngageConfig;
        }
    }

    @Override
    public String toString() {
        return "LocationTracking: " + this.getLocationTrackingFlag()
                + "\nLocationTrackingStrategy: " + this.getLocationTrackingStrategy()
                + "\nAutoGCMRegistration: " + this.getAutoGCMRegistrationFlag()
                + "\nWebEngageKey: " + this.getWebEngageKey()
                + "\nGCMProjectNumber: " + this.getGcmProjectNumber()
                + "\nWebEngageVersion: " + this.getWebEngageVersion()
                + "\nReportingStrategy: " + this.getEventReportingStrategy()
                + "\nDebugMode: " + this.getDebugMode()
                + "\nEveryActivityIsScreen: " + this.getEveryActivityIsScreen()
                + "\nEnvironment: " + this.getEnvironment()
                + "\nAlternateInterfaceId: " + this.getAlternateInterfaceIdFlag()
                + "\nPushSmallIcon: " + this.getPushSmallIcon()
                + "\nPushLargeIcon: " + this.getPushLargeIcon()
                + "\nAccentColor: " + this.getAccentColor()
                + "\nFilterCustomEvent: " + this.getFilterCustomEvents()
                + "\nSessionDestroyTime: " + this.getSessionDestroyTime()
                + "\nDefaultPushChannelConfiguration: " + this.getDefaultPushChannelConfiguration();
    }
}
