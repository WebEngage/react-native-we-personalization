package com.webengage.sdk.android;

import android.content.Context;

import java.util.Map;

public class AmplifyController implements Subscriber {
    private static AmplifyController instance = null;
    private Context applicationContext = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new AmplifyController(context);
            }
            return instance;
        }
    };

    private AmplifyController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        if (topic.equals(Topic.AMPLIFY)) {
            if (validateData(data)) {
                Action pushAmplifyAction = new PushAmplifyAction(this.applicationContext);
                pushAmplifyAction.performActionSync(getActionAttributes(topic, data));
            }
        }
    }

    @Override
    public boolean validateData(Object data) {
        return true;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        return null;
    }
}
