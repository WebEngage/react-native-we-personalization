package com.webengage.sdk.android.actions.database;


import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

public class ReportingController implements Subscriber {
    protected static final String ACTION_DATA = "action_data";
    protected static final String PRIORITY = "priority";
    protected static final String STRATEGY = "strategy";
    protected static StrategyFactory strategyFactory = null;
    private Context applicationContext = null;
    private static ReportingController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new ReportingController(context);
            }
            return instance;
        }
    };

    private ReportingController(Context context) {
        this.applicationContext = context.getApplicationContext();
        if (strategyFactory == null) {
            ReportingStatistics reportingStatistics = new ReportingStatistics();
            strategyFactory = new StrategyFactory(reportingStatistics, applicationContext);
        }
    }

    @Override
    public void createAction(Topic topic, Object data) {
        if (data == null || validateData(data)) {
            Action reportingAction = new ReportingAction(applicationContext);
            reportingAction.performActionSync(getActionAttributes(topic, data));
        }
    }

    @Override
    public boolean validateData(Object data) {
        if (data instanceof EventPayload) {
            EventPayload eventPayload = (EventPayload) data;
            return !(WebEngage.get().getWebEngageConfig().getFilterCustomEvents() && WebEngageConstant.APPLICATION.equals(eventPayload.getCategory()));
        } else {
            return false;
        }
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        if (data != null) {
            actionAttributes.put(ACTION_DATA, data);
            actionAttributes.put(STRATEGY, strategyFactory.getStrategy((EventPayload) data));
        } else {
            actionAttributes.put(ACTION_DATA, null);
            actionAttributes.put(STRATEGY, strategyFactory.getFlushStrategy());
        }
        return actionAttributes;
    }

}
