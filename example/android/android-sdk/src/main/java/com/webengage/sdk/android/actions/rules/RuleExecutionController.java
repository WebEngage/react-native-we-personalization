package com.webengage.sdk.android.actions.rules;


import android.content.Context;

import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Subscriber;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleExecutionController implements Subscriber {

    protected static final String RULE_EXECUTION_CHAIN = "execution_chain";
    protected static final String EVENT_STATE_DATA = "event_state_data";
    private Context applicationContext = null;
    private static RuleExecutionController instance = null;

    public static final Factory FACTORY = new Factory() {
        @Override
        public Subscriber initialize(Context context) {
            if (instance == null) {
                instance = new RuleExecutionController(context);
            }
            return instance;
        }
    };

    private RuleExecutionController(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public void createAction(Topic topic, Object data) {
        Map<String, Object> actionAttributes = getActionAttributes(topic, data);
        if (actionAttributes.get(RULE_EXECUTION_CHAIN) != null) {
            RuleExecutionAction action = new RuleExecutionAction(applicationContext);
            action.performActionSync(actionAttributes);
        }
    }

    @Override
    public boolean validateData(Object data) {
        return false;
    }

    @Override
    public Map<String, Object> getActionAttributes(Topic topic, Object data) {
        Map<String, Object> actionAttributes = new HashMap<String, Object>();
        if (data instanceof EventPayload) {
            List<WebEngageConstant.RuleCategory> executionChain = new ArrayList<WebEngageConstant.RuleCategory>();
            EventPayload eventPayload = (EventPayload) data;
            String event = eventPayload.getEventName();
            if (event != null && WebEngageConstant.SYSTEM.equals(eventPayload.getCategory())) {
                switch (event) {
                    case EventName.VISITOR_NEW_SESSION:
                    case EventName.USER_UPDATE:
                    case EventName.USER_UPDATE_GEO_INFO:
                    case EventName.USER_DELETE_ATTRIBUTES:
                        executionChain.add(WebEngageConstant.RuleCategory.SESSION_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.WE_WK_SCREEN_NAVIGATED:
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.USER_LOGGED_IN:
                        executionChain.add(WebEngageConstant.RuleCategory.SESSION_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.EVENT_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.NOTIFICATION_CLOSE:
                    case EventName.NOTIFICATION_CLICK:
                    case EventName.NOTIFICATION_VIEW:
                    case EventName.NOTIFICATION_CONTROL_GROUP:
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.EVENT_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.PUSH_NOTIFICATION_CLICK:
                    case EventName.PUSH_NOTIFICATION_CLOSE:
                    case EventName.PUSH_NOTIFICATION_VIEW:
                    case EventName.PUSH_NOTIFICATION_RECEIVED:
                    case EventName.WE_WK_PUSH_NOTIFICATION_RERENDER:
                    case EventName.PUSH_NOTIFICATION_ITEM_VIEW:
                    case EventName.APP_INSTALLED:
                    case EventName.APP_UPGRADED:
                    case EventName.USER_LOGGED_OUT:
                    case EventName.WE_WK_PAGE_DELAY:
                    case EventName.WE_WK_SESSION_DELAY:
                    case EventName.APP_CRASHED:
                        executionChain.add(WebEngageConstant.RuleCategory.EVENT_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.INLINE_PERSONALIZATION_VIEW:
                    case EventName.INLINE_CONTROL_GROUP:
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.EVENT_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                    case EventName.INLINE_PERSONALIZATION_CLICK:
                        executionChain.add(WebEngageConstant.RuleCategory.SESSION_RULE);
                        executionChain.add(WebEngageConstant.RuleCategory.PAGE_RULE);
                        actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
                        break;
                }
            } else {
                executionChain.add(WebEngageConstant.RuleCategory.EVENT_RULE);
                actionAttributes.put(RULE_EXECUTION_CHAIN, executionChain);
            }
            actionAttributes.put(EVENT_STATE_DATA, eventPayload);
        } else {
            actionAttributes.put(RULE_EXECUTION_CHAIN, data);
        }

        return actionAttributes;
    }

}
