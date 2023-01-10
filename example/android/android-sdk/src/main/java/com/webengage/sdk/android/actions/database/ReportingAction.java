package com.webengage.sdk.android.actions.database;


import android.content.Context;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.WebEngage;

import java.util.Map;

class ReportingAction extends Action {
    Context applicationContext = null;

    ReportingAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        return actionAttributes;
    }

    @Override
    protected Object execute(Object data) {
        Map<String, Object> actionAttributes = (Map<String, Object>) data;
        if (actionAttributes == null) {
            return null;
        }
        Object obj = actionAttributes.get(ReportingController.ACTION_DATA);
        EventPayload eventPayload = null;
        if(obj instanceof EventPayload) {
            eventPayload = (EventPayload)obj;
            eventPayload.setLicenseCode(WebEngage.get().getWebEngageConfig().getWebEngageKey());
            eventPayload.setInterfaceId(getInterfaceID());
        }


        Strategy strategy = (Strategy) actionAttributes.get(ReportingController.STRATEGY);
        strategy.report(eventPayload);
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }
}
