package com.webengage.sdk.android.actions.database;


import android.content.Context;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

public class DataController implements Subscriber {

    private static volatile DataController instance = null;
    protected static final String ACTION_DATA = "action_data";
    protected static final String ACTION_TYPE = "action_type";
    //protected static final String STATIC = "static";
    protected static final String GCM = "gcm";
    protected static final String EVENT = "event";
    protected static final String INTERNAL_EVENT = "internal_event";
    protected static final String CHANGE_DATA = "change_data";
    private Context applicationContext = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new DataController(context);
            }
            return instance;
        }
    };

    private DataController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    @Override
    public void createAction(Topic topic, Object data) {
        switch (topic) {
            case GCM_MESSAGE:
                Bundle extras = (Bundle) data;
                if (extras != null && extras.containsKey(WebEngageConstant.GCM_MESSAGE_SOURCE) && ("webengage").equalsIgnoreCase(extras.getString(WebEngageConstant.GCM_MESSAGE_SOURCE))) {
                    if (extras.containsKey("message_data") && extras.containsKey(WebEngageConstant.GCM_MESSAGE_ACTION_KEY)) {
                        Action action = new DataHolderAction(applicationContext);
                        action.performActionSync(getActionAttributes(topic, extras));
                    }
                }
                break;
            case EVENT:
            case INTERNAL_EVENT:
            case DATA:
                Action action = new DataHolderAction(applicationContext);
                action.performActionSync(getActionAttributes(topic, data));

                break;

        }
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(ACTION_DATA, data);
        switch (topic) {
            case GCM_MESSAGE:
                actionAttributes.put(ACTION_TYPE, GCM);
                break;
            case INTERNAL_EVENT:
                actionAttributes.put(ACTION_TYPE, INTERNAL_EVENT);
                break;
            case EVENT:
                actionAttributes.put(ACTION_TYPE, EVENT);
                break;
            case DATA:
                actionAttributes.put(ACTION_TYPE, CHANGE_DATA);
                break;

        }
        return actionAttributes;
    }
}
