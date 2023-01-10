package com.webengage.sdk.android.utils;


import android.util.Base64;

import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebEngageConstant {
    public static final String URI = "deeplink_uri";
    public static final String GCM_MESSAGE_ACTION_KEY = "message_action";
    public static final String GCM_MESSAGE_SOURCE = "source";
    public static final String TOPIC = "topic";
    public static final String DATA = "data";
    public static final String TAG = "WebEngage";
    public static final int THREAD_STATS_TAG = TAG.hashCode();
    public static final String TAG_WE_WEB_VIEW = "TAG_WE_WEB_VIEW";

    /*
    GCM Message Action
     */
    public static final String SHOW_SYSTEM_TRAY_NOTIFICATION = "show_system_tray_notification";
    public static final String PING = "ping";

    /*
    GCM Message Control message keys
     */
    public static final String SHUTDOWN = "shutdown";


    public static final String KEY_GCM_PROJECT_NUMBER = "com.webengage.sdk.android.project_number";
    public static final String KEY_WEBENGAGE_KEY = "com.webengage.sdk.android.key";
    public static final String KEY_DEBUG_MODE = "com.webengage.sdk.android.debug";
    public static final String DIR_PATH = ".WebEngageCahce";
    public static final int DEFAULT_READOUT_TIME = 20000;
    public static final int DEFAULT_CONNECT_TIMEOUT = 60000;


    public final static String KEY_ENABLE_LOCATION_TRACKING = "com.webengage.sdk.android.location_tracking";
    public final static String KEY_AUTO_GCM_REGISTRATION = "com.webengage.sdk.android.auto_gcm_registration";
    public final static String KEY_ENVIRONMENT = "com.webengage.sdk.android.environment";
    public final static String KEY_ALTERNATE_INTERFACE_ID = "com.webengage.sdk.android.alternate_interface_id";
    public final static String KEY_PUSH_SMALL_ICON = "com.webengage.sdk.android.small_icon";
    public final static String KEY_PUSH_LARGE_ICON = "com.webengage.sdk.android.large_icon";
    public final static String KEY_ACCENT_COLOR = "com.webengage.sdk.android.accent_color";

    public final static String KEY_NOTIFICATION_CHANNEL_NAME = "com.webengage.sdk.android.push.channel.name";
    public final static String KEY_NOTIFICATION_CHANNEL_DESCRIPTION = "com.webengage.sdk.android.push.channel.description";
    public final static String KEY_NOTIFICATION_CHANNEL_IMPORTANCE = "com.webengage.sdk.android.push.channel.importance";
    public final static String KEY_NOTIFICATION_CHANNEL_GROUP = "com.webengage.sdk.android.push.channel.group";
    public final static String KEY_NOTIFICATION_CHANNEL_LIGHT_COLOR = "com.webengage.sdk.android.push.channel.light_color";
    public final static String KEY_NOTIFICATION_CHANNEL_LOCK_SCREEN_VISIBILITY = "com.webengage.sdk.android.push.channel.lock_screen_visibility";
    public final static String KEY_NOTIFICATION_CHANNEL_SHOW_BADGE = "com.webengage.sdk.android.push.channel.show_badge";
    public final static String KEY_NOTIFICATION_CHANNEL_SOUND = "com.webengage.sdk.android.push.channel.sound";
    public final static String KEY_NOTIFICATION_CHANNEL_VIBRATION = "com.webengage.sdk.android.push.channel.vibration";

    public final static String AWS = "aws";
    public final static String GCE = "gce";
    public final static String IN = "in";
    public final static String IR0 = "ir0";
    public final static String UNL = "unl";

    public static final String NOTIFICATION_ID = "id";
    public static final String HASHED_NOTIFICATION_ID = "hashed_notification_id";
    public static final String NOTIFICATION_MAIN_INTENT = "notification_main_intent";
    public static final String CTA_ID = "call_to_action";
    public static final String EXPERIMENT_ID = "experiment_id";
    public static final String CUSTOM_DATA = "custom_data";
    public static final String WE_ADD_TO_SCREEN_DATA = "we_add_to_screen_data";
    public static final String WE_DISMISS_ON_CLICK = "we_dismiss";  // Key for boolean value added in carousel push custom data, 'true' will dismiss carousel push notification on click
    public static final String WE_PUSH_CUSTOM = "we_push_custom";
    public static final String WE_CUSTOM_RENDER = "we_custom_render";
    public static final String AMPLIFIED = "amplified";
    public static final String LAUNCH_APP_IF_INVALID = "launch_app_if_invalid";
    public static final String DEFAULT_PUSH_CHANNEL_ID = "we_wk_push_channel";
    public static final String DEFAULT_PUSH_CHANNEL_NAME = "Marketing";
    public static final int DEFAULT_PUSH_CHANNEL_IMPORTANCE = 3;
    public static final String EVENT_DATA = "event_data";
    public static final String EXTRA_DATA = "extra_data";
    public static final String ACTIVITY_COUNT = "activity_count";

    public static final String SYSTEM = "system";
    public static final String APPLICATION = "application";
    public static final String CUSTOM = "custom";
    public static final String CONFIG = "config";
    public static final String FETCH_PROFILE = "fetch_profile";
    public static final String JCX = "jcx";


    public static final String LANDSCAPE = "landscape";
    public static final String PORTRAIT = "portrait";
    public static final String CURRENT = "current";
    public static final String DISMISS_ON_CLICK = "dismiss_flag";
    public static final String NAVIGATION = "navigation";
    public static final String NAVIGATED_FROM = "navigated_from";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String WHEN = "when";
    public static final String EVENT = "event";
    public static final String RATE_VALUE = "we_wk_rating";
    public static final String WE_RENDER = "we_wk_render";
    public static final String KEY_ENABLE_CRASH_TRACKING = "com.webengage.sdk.android.crash_tracking";
    public static final String AUTOSCROLL_TIMER_KEY = "ast";
    public static final String SHOULD_AUTOSCROLL = "autcarousel_activated";
    public static final String IS_STICKY_KEY = "s";

    public static final String SYSTEM_TRAY = "system_tray";

    public static final String DATE_TRANSIT_ISO_FORMAT = "'~t'yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String DATE_ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final int I_P_CAMPAIGN_LIMIT = 4;
    public static final String REFRESH_CONFIG_RULE ="refreshSessionPageRule";

// staging constants
//    public static final float DISTANCE_THRESHOLD = 0f;
//    public static final long LOCATION_INTERVAL = 10 * 60 * 1000;
//    public static final long LOCATION_FASTEST_INTERVAL = 10 * 60 * 1000;
//    public static final long BACKGROUND_SESSION_CREATION_INTERVAL = 6 * 60 * 60 * 1000;
//    public static final long FOREGROUND_SYNC_INTERVAL = 60 * 1000;
//    public static final long BACKGROUND_SYNC_INTERVAL = 3 * 60 * 60 * 1000;
//    public static final long APP_SESSION_DESTROY_TIMEOUT = 20 * 1000;
//    public static final long CONFIG_REFRESH_INTERVAL = 2 * 60 * 1000;
//    public static final long USER_PROFILE_CALL_INTERVAL = 60 * 1000;
//    public static final long LEAVE_INTENT_TRIGGER_DELAY = 1000;
//    public static final int ROWS_THRESHOLD = 5;

    // prod constants
    public static final int ONE_SECOND = 1000;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final float ONE_KILOMETER = 1000f;
    public static final float DISTANCE_THRESHOLD_COUNTRY = 100 * ONE_KILOMETER;
    public static final float DISTANCE_THRESHOLD_CITY = 20 * ONE_KILOMETER;
    public static final float DISTANCE_THRESHOLD_BEST = ONE_KILOMETER;
    public static final long LOCATION_INTERVAL_COUNTRY = 12 * ONE_HOUR;
    public static final long LOCATION_INTERVAL_CITY = 3 * ONE_HOUR;
    public static final long LOCATION_INTERVAL_BEST = 15 * ONE_MINUTE;
    public static final long LOCATION_FASTEST_INTERVAL_COUNTRY = 12 * ONE_HOUR;
    public static final long LOCATION_FASTEST_INTERVAL_CITY = ONE_HOUR;
    public static final long LOCATION_FASTEST_INTERVAL_BEST = 5 * ONE_MINUTE;
    public static final long BACKGROUND_SESSION_CREATION_INTERVAL = 6 * ONE_HOUR;
    public static final long FOREGROUND_SYNC_JCX_INTERVAL = 15 * ONE_SECOND;
    public static final long FOREGROUND_SYNC_INTERVAL = ONE_MINUTE;
    public static final long BACKGROUND_SYNC_INTERVAL = 3 * ONE_HOUR;
    public static final long APP_SESSION_DESTROY_TIMEOUT = 15 * ONE_SECOND;
    public static final long CONFIG_REFRESH_INTERVAL = 2 * ONE_MINUTE;
    public static final long USER_PROFILE_CALL_INTERVAL = ONE_MINUTE;
    public static final long LEAVE_INTENT_TRIGGER_DELAY = ONE_SECOND;
    public static final long AMPLIFY_INITIAL_DELAY = 10 * ONE_SECOND;
    public static final long AMPLIFY_DEFAULT_INTERVAL_MINUTES = 180;
    public static final long AMPLIFY_DEFAULT_INTERVAL = AMPLIFY_DEFAULT_INTERVAL_MINUTES * ONE_MINUTE;
    public static final long AMPLIFY_JOB_FINISH_DELAY = 30 * ONE_SECOND;
    public static final long SHOWN_PUSH_STORAGE_LIFETIME = 7 * ONE_DAY;
    public static final int ROWS_THRESHOLD = 5;
    public static final int ROWS_MAX_LIMIT = 10;
    public static final int GOASYNC_LIMIT = 8 * ONE_SECOND;
    public static final String DEVICE_TYPE_MOBILE = "Mobile";
    public static final String DEVICE_TYPE_TABLET = "Tablet";
    public static final String DEVICE_TYPE_DEFAULT = DEVICE_TYPE_MOBILE;
    public static final int BOUND = 0;
    public static final int UNBOUND = 1;
    public static final int FINISH = 2;
    public enum Urls {
        // prod
        UPLOAD_EVENTS_URL("https://c.webengage.com/m2.jpg", "https://c.webengage.io/m2.jpg", "https://c.in.webengage.com/m2.jpg", "https://c.ir0.webengage.com/m2.jpg", "https://c.unl.webengage.com/m2.jpg"),
        EXCEPTION_END_POINT("https://c.webengage.com/e.jpg", "https://c.webengage.io/e.jpg", "https://c.in.webengage.com/e.jpg", "https://c.ir0.webengage.com/e.jpg", "https://c.unl.webengage.com/e.jpg"),
        USER_PROFILE_BASE("https://c.webengage.com/upf.js", "https://c.webengage.io/upf.js", "https://c.in.webengage.com/upf.js", "https://c.ir0.webengage.com/upf.js", "https://c.unl.webengage.com/upf.js"),
        JOURNEY_CONTEXT_BASE("https://c.webengage.com/jcx.js", "https://c.webengage.io/jcx.js", "https://c.in.webengage.com/jcx.js", "https://c.ir0.webengage.com/jcx.js", "https://c.unl.webengage.com/jcx.js"),
        RESOURCE_BASE("https://wsdk-files.webengage.com/", "https://s3.amazonaws.com/webengage-zfiles-gc/", "https://wsdk-files.in.webengage.com/", "https://wsdk-files.ir0.webengage.com/", "https://wsdk-files.webengage.com/"),
        PERSONALISATION_BASE("https://p.webengage.com", "https://p.webengage.io", "https://p.in.webengage.com", "https://p.ir0.webengage.com", "https://p.unl.webengage.com"),
        AMPLIFY_PUSH_BASE("https://c.webengage.com/push-amp", "https://c.webengage.io/push-amp", "https://c.in.webengage.com/push-amp", "https://c.ir0.webengage.com/push-amp", "https://c.unl.webengage.com/push-amp");


        // Old staging
//        UPLOAD_EVENTS_URL("https://c.webengage.biz/m2.jpg"),
//        EXCEPTION_END_POINT("https://c.webengage.biz/e.jpg"),
//        USER_PROFILE_BASE("https://c.webengage.biz/upf.js"),
//        JOURNEY_CONTEXT_BASE("https://c.webengage.biz/jcx.js"),
//        RESOURCE_BASE("http://s3-ap-southeast-1.amazonaws.com/wk-test-staticz-files/"),
//        PERSONALISATION_BASE("https://p.webengage.biz"),
//        AMPLIFY_PUSH_BASE("http://push-amplification.dev.env.webengage.org/push-amp");

        //QA
//        UPLOAD_EVENTS_URL("https://c.qa.webengage.biz/m2.jpg"),
//        EXCEPTION_END_POINT("https://c.qa.webengage.biz/e.jpg"),
//        USER_PROFILE_BASE("https://c.qa.webengage.biz/upf.js"),
//        JOURNEY_CONTEXT_BASE("https://c.qa.webengage.biz/jcx.js"),
//        RESOURCE_BASE("https://wk-static-files-qa.s3.amazonaws.com/"),
//        PERSONALISATION_BASE("https://p.qa.webengage.biz"),
//        AMPLIFY_PUSH_BASE("http://push-amplification.dev.env.webengage.org/push-amp");

        //New Staging
//        UPLOAD_EVENTS_URL("https://c.stg.webengage.biz/m2.jpg"),
//        EXCEPTION_END_POINT("https://c.stg.webengage.biz/e.jpg"),
//        USER_PROFILE_BASE("https://c.stg.webengage.biz/upf.js"),
//        JOURNEY_CONTEXT_BASE("https://c.stg.webengage.biz/jcx.js"),
//        RESOURCE_BASE("https://wk-static-files-stg.s3.amazonaws.com/"),
//        PERSONALISATION_BASE("https://p.stg.webengage.biz"),
//        AMPLIFY_PUSH_BASE("http://push-amplification.dev.env.webengage.org/push-amp");

        // local
//        UPLOAD_EVENTS_URL("https://8a14121c.ngrok.io/local_with_upload/local_with_upload", "https://c.webengage.io/m1.jpg"),
//        EXCEPTION_END_POINT("https://c.webengage.com/e.jpg", "https://c.webengage.io/e.jpg"),
//        USER_PROFILE_BASE("https://8a14121c.ngrok.io/upf.js", "https://c.webengage.io/upf.js"),
//        JOURNEY_CONTEXT_BASE("https://8a14121c.ngrok.io/jcx.js", "https://c.webengage.io/jcx.js"),
//        RESOURCE_BASE("https://8a14121c.ngrok.io/", "https://s3.amazonaws.com/webengage-zfiles-gc/"),
//        PERSONALISATION_BASE("https://8a14121c.ngrok.io", "https://p.webengage.io"),
//        AMPLIFY_PUSH_BASE("https://c183315f-75b2-4e51-bde0-d6994cbdbe11.mock.pstmn.io/webengage/amplify/push");


        private String[] valueString;

        Urls(String... str) {
            this.valueString = str;
        }

        public String toString() {
            String environment = WebEngage.get().getWebEngageConfig().getEnvironment();
            if (AWS.equals(environment)) {
                return this.valueString[0];
            } else if (GCE.equals(environment)) {
                return this.valueString[1];
            } else if (IN.equals(environment)) {
                return this.valueString[2];
            } else if (IR0.equals(environment)) {
                return this.valueString[3];
            } else if (UNL.equals(environment)) {
                return this.valueString[4];
            }
            return null;
        }

       public static String getEntityDataEndPoint(String licenseCode, String luid, String variationId, String cuid, String channel) {
            StringBuilder sb = new StringBuilder();
            sb.append(PERSONALISATION_BASE.toString());
            sb.append("/users/");
            sb.append(licenseCode);
            sb.append("/");
            sb.append(luid);
            sb.append("/templates/");
            sb.append(channel);
            sb.append(variationId);
            if (cuid != null && !cuid.isEmpty()) {
                try {
                    String encodedCUID = URLEncoder.encode(cuid, "UTF-8");
                    sb.append("?cuid=");
                    sb.append(encodedCUID);
                } catch (UnsupportedEncodingException e) {

                }
            }
            return sb.toString();
        }

        public static String getLayoutEndPoint(String baseUrl, String layoutId) {
            return baseUrl + "js/notification-layout-" + layoutId + ".js";
        }

        public static String getUserProfileEndPoint(String luid, String cuid, String licenseCode) {
            StringBuilder sb = new StringBuilder();
            sb.append(USER_PROFILE_BASE.toString());
            sb.append("?licenseCode=");
            sb.append(licenseCode);
            sb.append("&luid=");
            sb.append(luid);
            if (cuid != null && !cuid.isEmpty()) {
                try {
                    String encodedCUID = URLEncoder.encode(cuid, "UTF-8");
                    sb.append("&cuid=");
                    sb.append(encodedCUID);
                } catch (UnsupportedEncodingException e) {

                }
            }
            return sb.toString();
        }

        public static String getConfigEndPoint(String licenseCode) {
            return RESOURCE_BASE.toString() + "webengage/" + licenseCode + "/android/v4.js";
        }

        public static String getJounreyContextEndPoint(String luid, String cuid, String licenseCode, String upfc) {
            StringBuilder sb = new StringBuilder();
            sb.append(JOURNEY_CONTEXT_BASE.toString());
            sb.append("?licenseCode=");
            sb.append(licenseCode);
            sb.append("&luid=");
            sb.append(luid);
            if (cuid != null && !cuid.isEmpty()) {
                try {
                    String encodedCUID = URLEncoder.encode(cuid, "UTF-8");
                    sb.append("&cuid=");
                    sb.append(encodedCUID);
                } catch (UnsupportedEncodingException e) {

                }
            }
            if (upfc != null) {
                try {
                    String lz = Base64.encodeToString(upfc.getBytes(), Base64.DEFAULT);
                    sb.append("&upfc2=");
                    sb.append(URLEncoder.encode(lz, "UTF-8"));
                } catch (UnsupportedEncodingException e) {

                } catch (AssertionError e) {
                    Logger.e(WebEngageConstant.TAG, " Error while encoding upfc to base 64");
                }
            }
            return sb.toString();
        }

        public static String getAmplifyPushUrl(String licenseCode, String luid, String cuid) {
            StringBuilder sb = new StringBuilder(AMPLIFY_PUSH_BASE.toString());
            sb.append("?sdkId=2");
            sb.append("&licenseCode=").append(licenseCode);
            sb.append("&luid=").append(luid);
            if (!WebEngageUtils.isEmpty(cuid)) {
                try {
                    String encodedCUID = URLEncoder.encode(cuid, "UTF-8");
                    sb.append("&cuid=").append(encodedCUID);
                } catch (UnsupportedEncodingException e) {
                    Logger.e(WebEngageConstant.TAG, "Exception while url-encoding cuid: " + cuid, e);
                }
            }

            try {
                String localTimeZone = WebEngageUtils.getTimezone();
                String timezone = String.format("%s:%s", localTimeZone.substring(0, 3), localTimeZone.substring(3));
                String urlEncodedTimezone = URLEncoder.encode(timezone, "UTF-8");
                sb.append("&timezone=").append(urlEncodedTimezone);
            } catch (UnsupportedEncodingException e) {
                Logger.e(WebEngageConstant.TAG, "Exception while url-encoding timezone in push-amplify url.", e);
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, "Exception while adding timezone in push-amplify url.", e);
            }

            sb.append("&sdkVersion=").append(BuildConfig.FEATURE_VERSION);

            return sb.toString();
        }
    }

    public enum STYLE {
        BIG_PICTURE,
        BIG_TEXT,
        INBOX,
        CAROUSEL_V1,
        RATING_V1
    }


    public enum RuleCategory {
        PAGE_RULE("pageRuleCode"),
        SESSION_RULE("sessionRuleCode"),
        EVENT_RULE("eventRuleCode"),
        CUSTOM_RULE("customRuleCode");
        private String s;

        RuleCategory(String s) {
            this.s = s;
        }

        public String toString() {
            return this.s;
        }
    }

    public enum Entity {
        NOTIFICATION, SURVEY, PUSH, INLINE_PERSONALIZATION
    }

    public static final List<EntityTypeIdentifier> entityTypeIdentifierList = new ArrayList<>();

    static {
        entityTypeIdentifierList.add(new EntityTypeIdentifier("personalizationRuleList", "notificationEncId", Entity.INLINE_PERSONALIZATION));
        entityTypeIdentifierList.add(new EntityTypeIdentifier("notificationRuleList", "notificationEncId", Entity.NOTIFICATION));
        entityTypeIdentifierList.add(new EntityTypeIdentifier("surveyRuleList", "surveyEncId", Entity.SURVEY));
    }

    public static class EntityTypeIdentifier {
        public String entityListKey;
        public String entityExperimentIdKey;
        public Entity entityType;

        public EntityTypeIdentifier() {
            this.entityListKey = "";
            this.entityExperimentIdKey = "";
            this.entityType = null;
        }

        public EntityTypeIdentifier(String entityListKey, String entityExperimentIdKey, Entity entityType) {
            this.entityListKey = entityListKey;
            this.entityExperimentIdKey = entityExperimentIdKey;
            this.entityType = entityType;
        }
    }

    public static final String VIEW = "view";
    public static final String CLICK = "click";
    public static final String CLOSE = "close";
    public static final String HIDE = "hide";
    public static final String HIDE_SESSION = "hide_session";
    public static final String VIEW_SESSION = "view_session";
    public static final String CLOSE_SESSION = "close_session";
    public static final String SCOPE_SEPARATOR = "_";
    public static final String[] ScopeMetric = {VIEW, CLICK, CLOSE, VIEW_SESSION, CLOSE_SESSION};

    public static final String HIGH_REPORTING_PRIORITY = "high_reporting_priority";
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_LOW = 1;

    public static final Map<String, Integer> priorityMap;

    static {
        priorityMap = new HashMap<>();
        priorityMap.put(EventName.PUSH_NOTIFICATION_CLICK, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_CLOSE, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_ITEM_VIEW, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_RATING_SUBMITTED, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_RERENDER, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_RECEIVED, PRIORITY_HIGH);
        priorityMap.put(EventName.PUSH_NOTIFICATION_VIEW, PRIORITY_HIGH);
        priorityMap.put(EventName.GEOFENCE_TRANSITION, PRIORITY_HIGH);
        priorityMap.put(EventName.GCM_REGISTERED, PRIORITY_HIGH);
        priorityMap.put(EventName.APP_UPGRADED, PRIORITY_HIGH);
        priorityMap.put(EventName.APP_INSTALLED, PRIORITY_HIGH);
    }

}
