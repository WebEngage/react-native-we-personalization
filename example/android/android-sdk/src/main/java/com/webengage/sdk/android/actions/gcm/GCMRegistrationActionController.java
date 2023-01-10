package com.webengage.sdk.android.actions.gcm;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.BootupActionController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class GCMRegistrationActionController implements Subscriber {
    protected static final String ACTION_DATA = "action_data";
    private Context applicationContext = null;
    static AtomicBoolean shouldDoRegistration = new AtomicBoolean(false);
    private static GCMRegistrationActionController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new GCMRegistrationActionController(context);
            }
            return instance;
        }
    };

    private GCMRegistrationActionController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    @Override
    public void createAction(Topic topic, Object data) {
        if (!WebEngage.get().getWebEngageConfig().getAutoGCMRegistrationFlag()) {
            return;
        }

        EventPayload eventPayload = (EventPayload) data;
        Action gcmRegistrationAction = new GCMRegistrationAction(applicationContext);

        if (topic != null) {
            if (Topic.BOOT_UP.equals(topic) || (Topic.EVENT.equals(topic) && (eventPayload != null && (EventName.APP_UPGRADED.equals(eventPayload.getEventName()) || EventName.VISITOR_NEW_SESSION.equals(eventPayload.getEventName()))))) {
                if (shouldDoRegistration.compareAndSet(false, true)) {
                    gcmRegistrationAction.performActionAsync(getActionAttributes(topic, data));
                }
            }
        }

    }

    @Override
    public boolean validateData(Object data) {
        return true;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttribues = new HashMap<String, Object>();
        actionAttribues.put(ACTION_DATA, data);
        return actionAttribues;
    }
}
