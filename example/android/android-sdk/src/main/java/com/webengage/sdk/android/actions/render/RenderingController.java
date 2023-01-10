package com.webengage.sdk.android.actions.render;


import android.content.Context;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenderingController implements Subscriber {
    protected static final String ACTION_DATA = "action_data";
    protected static final String FIRST_TIME = "first_time";
    private Context applicationContext = null;
    private static RenderingController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new RenderingController(context);
            }
            return instance;
        }
    };

    private RenderingController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        switch (topic) {
            case GCM_MESSAGE:
                Bundle extras = (Bundle) data;
                if (extras != null && extras.containsKey(WebEngageConstant.GCM_MESSAGE_SOURCE) && ("webengage").equalsIgnoreCase(extras.getString(WebEngageConstant.GCM_MESSAGE_SOURCE))) {
                    String action = extras.getString(WebEngageConstant.GCM_MESSAGE_ACTION_KEY);
                    if (action != null && action.equalsIgnoreCase(WebEngageConstant.SHOW_SYSTEM_TRAY_NOTIFICATION) && extras.containsKey("message_data")) {
                        try {
                            String message = extras.getString("message_data");
                            Logger.d(WebEngageConstant.TAG, " Push Payload: " + message);
                            JSONObject gcmMessage = new JSONObject(message);
                            Action systemTrayNotificationAction = new PushNotificationAction(applicationContext);
                            systemTrayNotificationAction.performActionSync(getActionAttributes(topic, gcmMessage.getString("identifier")));
                        } catch (JSONException e) {
                            WebEngage.startService(IntentFactory.newIntent(Topic.EXCEPTION, e, this.applicationContext), applicationContext);
                        }
                    }
                }
                break;

            case RENDER:
                Action renderingAction = null;
                List<String> renderingIds = (List<String>) data;
                if (renderingIds != null && renderingIds.size() > 0) {
                    for (String id : renderingIds) {
                        if (DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.NOTIFICATION) && DataHolder.get().isAppForeground()) {
                            if (DataHolder.get().compareAndSetEntityRunningState(false, true)) {
                                renderingAction = new InAppNotificationAction(this.applicationContext);
                                renderingAction.performActionSync(getActionAttributes(topic, id));
                            }
                        }
                    }
                }
                break;

            case EVENT:
            case INTERNAL_EVENT:
                if (data instanceof EventPayload) {
                    EventPayload eventPayload = (EventPayload) data;
                    if (EventName.WE_WK_PUSH_NOTIFICATION_RERENDER.equals(eventPayload.getEventName())) {
                        Action pushNotificationAction = new PushNotificationAction(this.applicationContext);
                        pushNotificationAction.performActionSync(getActionAttributes(topic, data));
                    }
                }
                break;
        }
    }

    private void checkForInLineProperties() {

    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttribues = new HashMap<String, Object>();
        switch (topic) {
            case GCM_MESSAGE:
                actionAttribues.put(FIRST_TIME, true);
                actionAttribues.put(WebEngageConstant.CURRENT, 0);
                actionAttribues.put(WebEngageConstant.NAVIGATION, WebEngageConstant.RIGHT);
                actionAttribues.put(ACTION_DATA, data);
                break;
            case EVENT:
            case INTERNAL_EVENT:
                EventPayload eventPayload = (EventPayload) data;
                actionAttribues.put(FIRST_TIME, false);
                actionAttribues.put(ACTION_DATA, eventPayload);
                break;
            case RENDER:
                actionAttribues.put(ACTION_DATA, data);
                break;
        }

        return actionAttribues;
    }
}
