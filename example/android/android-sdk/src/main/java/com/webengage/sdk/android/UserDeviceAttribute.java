package com.webengage.sdk.android;


public class UserDeviceAttribute {

    public static final String TIME_SPENT = "time_spent";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String CITY = "city";
    public static final String COUNTRY = "country";
    public static final String REGION = "region";
    public static final String POSTAL_CODE = "postal_code";
    public static final String LOCALITY = "locality";
    public static final String OPT_IN_PUSH = "opt_in_push"; //System allow push
    public static final String OPT_IN_INAPP = "opt_in_inapp";
    public static final String LAST_LOGGED_IN = "last_logged_in";
    public static final String FIRST_LOGGED_IN = "first_logged_in";
    public static final String LAST_SEEN = "last_seen";
    public static final String FIRST_SESSION_START_TIME = "first_session_start_time";
    public static final String SESSION_COUNT = "session_count";
    public static final String CAMPAIGN_ID = "campaign_id";
    public static final String CAMPAIGN_SOURCE = "campaign_source";
    public static final String CAMPAIGN_MEDIUM = "campaign_medium";
    public static final String CAMPAIGN_TERM = "campaign_term";
    public static final String CAMPAIGN_CONTENT = "campaign_content";
    public static final String CAMPAIGN_GCLID = "campaign_gclid";
    public static final String REFERRER = "referrer";

    public static final String[] deviceAttributes = {
            TIME_SPENT,
            LATITUDE,
            LONGITUDE,
            CITY,
            COUNTRY,
            REGION,
            POSTAL_CODE,
            LOCALITY,
            OPT_IN_PUSH,
            OPT_IN_INAPP,
            LAST_LOGGED_IN,
            FIRST_LOGGED_IN,
            LAST_SEEN,
            FIRST_SESSION_START_TIME,
            SESSION_COUNT,
            CAMPAIGN_ID,
            CAMPAIGN_SOURCE,
            CAMPAIGN_MEDIUM,
            CAMPAIGN_TERM,
            CAMPAIGN_CONTENT,
            CAMPAIGN_GCLID,
            REFERRER
    };

    public static boolean isDeviceAttribute(String key) {
        for (int i = 0; i < deviceAttributes.length; i++) {
            if (deviceAttributes[i].equals(key)) {
                return true;
            }
        }
        return false;
    }


}
