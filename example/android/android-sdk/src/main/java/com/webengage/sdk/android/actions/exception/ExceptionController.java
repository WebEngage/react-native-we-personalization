package com.webengage.sdk.android.actions.exception;


import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;

import java.util.HashMap;
import java.util.Map;

public class ExceptionController implements Subscriber {
    protected static final String ACTION_DATA = "action_data";
    private Context applicationContext = null;
    private static ExceptionController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new ExceptionController(context);
            }
            return instance;
        }
    };

    private ExceptionController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        Action exceptionAction = new ExceptionAction(applicationContext);
        exceptionAction.performActionSync(getActionAttributes(topic, data));
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        actionAttributes.put(ACTION_DATA, data);
        return actionAttributes;
    }
}
