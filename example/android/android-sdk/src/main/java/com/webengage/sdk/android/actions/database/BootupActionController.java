package com.webengage.sdk.android.actions.database;


import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;

import java.util.Map;

public class BootupActionController implements Subscriber {

    private static volatile BootupActionController instance = null;
    private Context applicationContext = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new BootupActionController(context);
            }
            return instance;
        }
    };

    private BootupActionController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }


    @Override
    public void createAction(Topic Topic, Object data) {
        Action bootupAction = new BootupAction(applicationContext);
        bootupAction.performActionSync(null);
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic Topic, Object data) {
        return null;
    }
}
