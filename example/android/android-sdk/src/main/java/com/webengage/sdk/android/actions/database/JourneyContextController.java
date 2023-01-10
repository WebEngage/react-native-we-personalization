package com.webengage.sdk.android.actions.database;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

public class JourneyContextController implements Subscriber {
    private Context applicationContext = null;
    private static JourneyContextController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new JourneyContextController(context);
            }
            return instance;
        }
    };

    private JourneyContextController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        Action action = new JourneyContextAction(this.applicationContext);
        action.performActionSync(getActionAttributes(topic, data));
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(WebEngageConstant.TOPIC, topic);
        map.put(WebEngageConstant.DATA, data);
        return map;
    }
}
