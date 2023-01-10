package com.webengage.sdk.android.actions.deeplink;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.actions.database.BootupActionController;
import com.webengage.sdk.android.actions.render.CallToAction;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepLinkActionController implements Subscriber {
    private static volatile DeepLinkActionController instance = null;
    protected static final String ACTION_DATA = "action_data";

    private Context applicationContext = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new DeepLinkActionController(context);
            }
            return instance;
        }
    };

    private DeepLinkActionController(Context context) {
        this.applicationContext = context.getApplicationContext();

    }


    @Override
    public void createAction(Topic topic, Object data) {
        if(data == null) {
            return;
        }
        Action action = new DeepLinkAction(applicationContext);
        action.performActionSync(getActionAttributes(topic, data));
    }



    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic Topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(ACTION_DATA, data);
        return actionAttributes;
    }
}
