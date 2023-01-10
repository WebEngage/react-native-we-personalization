package com.webengage.sdk.android;

import android.content.Context;

import com.webengage.sdk.android.actions.database.ReportingStrategy;

public class ConfigPreferenceManager extends BasePreferenceManager {
    private static final String LICENSE_CODE = "license_code";
    public static final String LOCATION_TRACKING_FLAG = "location_tracking_flag";
    public static final String LOCATION_TRACKING_STRATEGY = "location_tracking_strategy";
    public static final String EVENT_REPORTING_STRATEGY = "event_reporting_strategy";

    protected ConfigPreferenceManager(Context context) {
        super(context);
    }

    void saveLicenseCode(String licenseCode) {
        saveToPreferences(LICENSE_CODE, licenseCode);
    }

    String getLicenseCode() {
        return getPreferenceFile(DEFAULT_PREFS).getString(LICENSE_CODE, null);
    }

    public void saveEventReportingStrategy(ReportingStrategy strategy) {
        saveToPreferences(EVENT_REPORTING_STRATEGY, strategy.name());
    }

    public ReportingStrategy getEventReportingStrategy() {
        return ReportingStrategy.valueOf(getPreferenceFile(DEFAULT_PREFS).getString(EVENT_REPORTING_STRATEGY, ReportingStrategy.BUFFER.name()));
    }

    public void saveLocationTrackingFlag(boolean flag) {
        saveToPreferences(LOCATION_TRACKING_FLAG, flag);
    }

    public boolean getLocationTrackingFlag() {
        return getPreferenceFile(DEFAULT_PREFS).getBoolean(LOCATION_TRACKING_FLAG, true);
    }

    public void saveLocationTrackingStrategy(LocationTrackingStrategy strategy) {
        saveToPreferences(LOCATION_TRACKING_STRATEGY, strategy.name());
    }

    public LocationTrackingStrategy getLocationTrackingStrategy() {
        return LocationTrackingStrategy.valueOf(getPreferenceFile(DEFAULT_PREFS).getString(LOCATION_TRACKING_STRATEGY, LocationTrackingStrategy.ACCURACY_CITY.name()));
    }

}
