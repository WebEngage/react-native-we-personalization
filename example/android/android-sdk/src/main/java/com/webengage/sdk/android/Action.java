package com.webengage.sdk.android;


import android.content.Context;
import android.content.Intent;

import com.webengage.sdk.android.utils.AsyncRunner;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.Map;

public abstract class Action extends BasePreferenceManager {

    protected Action(Context context) {
        super(context);
    }

    protected abstract Object preExecute(Map<String, Object> actionAttributes);

    protected abstract Object execute(Object data);

    protected abstract void postExecute(Object data);

    public void performActionSync(Map<String, Object> actionAttributes) {
        postExecute(execute(preExecute(actionAttributes)));
    }

    public void performActionAsync(Map<String, Object> actionAttributes) {
        Runnable runnable = new AsyncActionRunner(actionAttributes);
        AsyncRunner.execute(runnable);
    }

    protected void dispatchEventTopic(Object data) {
        Intent intent = IntentFactory.newIntent(Topic.EVENT, data, applicationContext);
        WebEngage.startService(intent, applicationContext);

    }

    protected void dispatchExceptionTopic(Object data) {
        Intent intent = IntentFactory.newIntent(Topic.EXCEPTION, data, applicationContext);
        WebEngage.startService(intent, applicationContext);

    }

    protected void dispatchRenderTopic(Object data) {
        Intent intent = IntentFactory.newIntent(Topic.RENDER, new SubscriberData(getLUID(), data), applicationContext);
        WebEngage.startService(intent, applicationContext);

    }


    public CallbackDispatcher getCallbackDispatcher(Context context) {
        return CallbackDispatcher.init(context.getApplicationContext());
    }


    private class AsyncActionRunner implements Runnable {
        Map<String, Object> actionAttributes;

        AsyncActionRunner(Map<String, Object> actionAttributes) {
            this.actionAttributes = actionAttributes;
        }

        @Override
        public void run() {
            try {
                postExecute(execute(preExecute(actionAttributes)));
            } catch (Exception e) {
                Logger.e(WebEngageConstant.TAG, e.getMessage());
                //not logging exception to backend as it may cause infinite looping if exception controller calls exception action
                //in an async way and some exception occurs in exception action.
            }
        }
    }


}
