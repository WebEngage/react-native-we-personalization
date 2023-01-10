package com.webengage.sdk.android.actions.rules;

import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;


public class ConfigurationController implements Subscriber {
    protected static final String CONGIF_URL = "config_url";
    protected static final String TOPIC = "topic";
    private Context applicationContext = null;
    private static ConfigurationController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new ConfigurationController(context);
            }
            return instance;
        }
    };

    private ConfigurationController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        Action action = new ConfigurationAction(this.applicationContext);
        action.performActionSync(getActionAttributes(topic, data));
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(CONGIF_URL, WebEngageConstant.Urls.getConfigEndPoint(WebEngage.get().getWebEngageConfig().getWebEngageKey()));
        actionAttributes.put(TOPIC, topic);
        return actionAttributes;
    }
}
