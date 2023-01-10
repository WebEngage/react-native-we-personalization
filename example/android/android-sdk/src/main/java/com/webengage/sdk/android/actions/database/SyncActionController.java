package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SyncActionController implements Subscriber {

    protected static final String ACTION_DATA = "action_data";
    protected static final String SERVER_URL = "server_url";
    private Context applicationContext = null;
    private static SyncActionController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new SyncActionController(context);
            }
            return instance;
        }
    };

    private SyncActionController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    @Override
    public void createAction(Topic topic, Object data) {
        Action syncAction = new SyncAction(applicationContext);
        Map<String, Object> actionAttribues = getActionAttributes(topic, data);
        syncAction.performActionSync(actionAttribues);

    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(SERVER_URL, WebEngageConstant.Urls.UPLOAD_EVENTS_URL.toString());
        actionAttributes.put(ACTION_DATA, data);
        return actionAttributes;

    }

}
