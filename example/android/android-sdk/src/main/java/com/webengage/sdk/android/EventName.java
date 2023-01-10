package com.webengage.sdk.android;


import java.util.ArrayList;
import java.util.List;

public class EventName {

    public static final String USER_UPDATE = "user_update";

    public static final String USER_LOGGED_IN = "user_logged_in";

    public static final String USER_LOGGED_OUT = "user_logged_out";

    public static final String USER_UPDATE_GEO_INFO = "user_update_geo_info";

    public static final String USER_DELETE_ATTRIBUTES = "user_delete_attributes";

    public static final String USER_INCREMENT = "user_increment";

    public static final String APP_INSTALLED = "app_installed";

    public static final String APP_UPGRADED = "app_upgraded";

    public static final String APP_CRASHED = "app_crashed";

    public static final String GCM_REGISTERED = "gcm_registered";

    public static final String PUSH_NOTIFICATION_CLOSE = "push_notification_close";

    public static final String PUSH_NOTIFICATION_VIEW = "push_notification_view";

    public static final String PUSH_NOTIFICATION_CLICK = "push_notification_click";

    public static final String PUSH_NOTIFICATION_RECEIVED = "push_notification_received";

    public static final String PUSH_NOTIFICATION_ITEM_VIEW = "push_notification_item_view";

    public static final String PUSH_NOTIFICATION_RATING_SUBMITTED = "push_notification_rating_submitted";

    public static final String PUSH_NOTIFICATION_RERENDER = "push_notification_rerender";

    public static final String NOTIFICATION_VIEW = "notification_view";

    public static final String NOTIFICATION_CLICK = "notification_click";

    public static final String NOTIFICATION_CLOSE = "notification_close";

    public static final String VISITOR_NEW_SESSION = "visitor_new_session";

    public static final String VISITOR_SESSION_CLOSE = "visitor_session_close";

    public static final String PUSH_PING = "push_ping";

    public static final String GEOFENCE_TRANSITION = "geofence_transition";

    public static final String NOTIFICATION_CONTROL_GROUP = "notification_control_group";

    public static final String INLINE_CONTROL_GROUP = "app_personalization_control_group";

    public static final String INLINE_PERSONALIZATION_CLICK = "app_personalization_click";

    public static final String INLINE_PERSONALIZATION_VIEW = "app_personalization_view";

    public static final String INLINE_PERSONALIZATION_FAILED = "app_personalization_failed";

    public static final List<String> SYSTEM_EVENTS = new ArrayList<>();

    // Dummy events
    public static final String WE_WK_ACTIVITY_START = "we_wk_activity_start";
    public static final String WE_WK_PAGE_DELAY = "we_wk_page_delay";
    public static final String WE_WK_SESSION_DELAY = "we_wk_session_delay";
    public static final String WE_WK_LEAVE_INTENT = "we_wk_leave_intent";
    public static final String WE_WK_ACTIVITY_STOP = "we_wk_activity_stop";
    public static final String WE_WK_SCREEN_NAVIGATED = "we_wk_screen_navigated";
    public static final String WE_WK_PUSH_NOTIFICATION_RERENDER = "we_wk_push_notification_rerender";

    static {
        SYSTEM_EVENTS.add(USER_UPDATE);
        SYSTEM_EVENTS.add(USER_LOGGED_IN);
        SYSTEM_EVENTS.add(USER_LOGGED_OUT);
        SYSTEM_EVENTS.add(USER_UPDATE_GEO_INFO);
        SYSTEM_EVENTS.add(USER_DELETE_ATTRIBUTES);
        SYSTEM_EVENTS.add(USER_INCREMENT);
        SYSTEM_EVENTS.add(APP_INSTALLED);
        SYSTEM_EVENTS.add(APP_UPGRADED);
        SYSTEM_EVENTS.add(APP_CRASHED);
        SYSTEM_EVENTS.add(GCM_REGISTERED);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_CLOSE);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_VIEW);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_CLICK);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_RECEIVED);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_ITEM_VIEW);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_RATING_SUBMITTED);
        SYSTEM_EVENTS.add(PUSH_NOTIFICATION_RERENDER);
        SYSTEM_EVENTS.add(NOTIFICATION_VIEW);
        SYSTEM_EVENTS.add(NOTIFICATION_CLICK);
        SYSTEM_EVENTS.add(NOTIFICATION_CLOSE);
        SYSTEM_EVENTS.add(VISITOR_NEW_SESSION);
        SYSTEM_EVENTS.add(VISITOR_SESSION_CLOSE);
        SYSTEM_EVENTS.add(PUSH_PING);
        SYSTEM_EVENTS.add(GEOFENCE_TRANSITION);
        SYSTEM_EVENTS.add(NOTIFICATION_CONTROL_GROUP);
        SYSTEM_EVENTS.add(INLINE_CONTROL_GROUP);
        SYSTEM_EVENTS.add(INLINE_PERSONALIZATION_CLICK);
        SYSTEM_EVENTS.add(INLINE_PERSONALIZATION_VIEW);
        SYSTEM_EVENTS.add(WE_WK_ACTIVITY_START);
        SYSTEM_EVENTS.add(WE_WK_PAGE_DELAY);
        SYSTEM_EVENTS.add(WE_WK_SESSION_DELAY);
        SYSTEM_EVENTS.add(WE_WK_LEAVE_INTENT);
        SYSTEM_EVENTS.add(WE_WK_ACTIVITY_STOP);
        SYSTEM_EVENTS.add(WE_WK_SCREEN_NAVIGATED);
        SYSTEM_EVENTS.add(WE_WK_PUSH_NOTIFICATION_RERENDER);
        SYSTEM_EVENTS.add(INLINE_PERSONALIZATION_FAILED);
    }
}