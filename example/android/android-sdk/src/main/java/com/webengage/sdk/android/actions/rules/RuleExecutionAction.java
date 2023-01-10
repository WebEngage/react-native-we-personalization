package com.webengage.sdk.android.actions.rules;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.actions.database.DataContainer;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.render.InLinePersonalizationAction;
import com.webengage.sdk.android.actions.rules.ruleEngine.Function;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.htmlspanner.TextUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.webengage.sdk.android.utils.WebEngageConstant.TAG;

public class RuleExecutionAction extends Action {
    private Context applicationContext = null;
    private List<WebEngageConstant.RuleCategory> executionChain = null;
    String userIdentifier = null;
    long start = -1;

    protected RuleExecutionAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        return actionAttributes;
    }

    /**
     * Good job,congratulation!!!
     * If you think that reaching till here was like a gold mining, boy you
     * have no idea about what is going to come ahead.
     * So, get ready for some more actions and fasten your seat-belts because the ride
     * is going to get jerky.
     *
     * @param data
     * @return
     */
    @Override
    protected Object execute(Object data) {
        start = System.currentTimeMillis();
        userIdentifier = getCUID().isEmpty() ? getLUID() : getCUID();
        List<String> evaluatedIds = null;
        Map<String, Object> actionAttributes = (Map<String, Object>) data;
        executionChain = (List<WebEngageConstant.RuleCategory>) actionAttributes.get(RuleExecutionController.RULE_EXECUTION_CHAIN);
        EventPayload eventPayload = (EventPayload) actionAttributes.get(RuleExecutionController.EVENT_STATE_DATA);
        if (eventPayload != null) {
            boolean flag = checkForEventCriterias(eventPayload);
            if (flag) {
                if (executionChain.size() > 0) {
                    if (executionChain.get(0) != WebEngageConstant.RuleCategory.SESSION_RULE) {
                        executionChain.add(0, WebEngageConstant.RuleCategory.SESSION_RULE);
                    }
                    if (executionChain.size() > 1) {
                        if (executionChain.get(1) != WebEngageConstant.RuleCategory.PAGE_RULE) {
                            executionChain.add(1, WebEngageConstant.RuleCategory.PAGE_RULE);
                        }
                    } else {
                        executionChain.add(1, WebEngageConstant.RuleCategory.PAGE_RULE);
                    }
                } else {
                    executionChain.add(0, WebEngageConstant.RuleCategory.SESSION_RULE);
                    executionChain.add(1, WebEngageConstant.RuleCategory.PAGE_RULE);
                }
            }
        }
        for (WebEngageConstant.RuleCategory ruleCategory : executionChain) {
            if (ruleCategory.equals(WebEngageConstant.RuleCategory.PAGE_RULE)) {
                RuleExecutorFactory.getRuleExecutor().setCompetingIds(getSessionEvaluatedIds());
                Map<String, Map<String, String>> variationMap = getVariationMap();
                List<String> ids = RuleExecutorFactory.getRuleExecutor().evaluateRulesByCategory(WebEngageConstant.RuleCategory.PAGE_RULE);
                long minOrder = Long.MAX_VALUE;
                try {
                    evaluatedIds = new ArrayList<String>();
                    if (ids.size() > 0) {
                        ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                        for (String id : ids) {
                            Map<String, Object> entityObj = null;
                            WebEngageConstant.Entity entityProcessing = null;
                            boolean isInline;
                            if (null != DataHolder.get().getInlineCampaignsData() && null != DataHolder.get().getInlineCampaignsData().get(id)) {
                                entityObj = configurationManager.getEntityObj(id, WebEngageConstant.Entity.INLINE_PERSONALIZATION);
                                entityProcessing = WebEngageConstant.Entity.INLINE_PERSONALIZATION;
                                isInline = true;
                            } else {
                                entityObj = configurationManager.getEntityObj(id, WebEngageConstant.Entity.NOTIFICATION);
                                entityProcessing = WebEngageConstant.Entity.NOTIFICATION;
                                isInline = false;
                            }
                            long order = entityObj.get("order") == null ? 0 : (long) entityObj.get("order");

                            if (isInline) {
                                Map<String, String> layoutIdMap = variationMap.get(id);
                                Map<String, Object> variationObj = null;
                                if (layoutIdMap != null) {
                                    variationObj = configurationManager.getEntityVariationObj(layoutIdMap.keySet().iterator().next(), entityObj);
                                }
                                //variationObj will be null if sampling fall under control group
                                boolean result = performBaseCheck(entityObj, variationObj, entityProcessing);
                                if (result) {
//                                    Logger.d(WebEngageConstant.TAG, "Rule eval adding to evaluation list: " + id);
                                    evaluatedIds.add(id);
                                } else {
//                                    Logger.d(WebEngageConstant.TAG, "Rule eval failed to add to evaluation list: " + id);
                                }
                            } else {
                                if (minOrder == Long.MAX_VALUE || order <= minOrder) {
                                    Map<String, String> layoutIdMap = variationMap.get(id);
                                    Map<String, Object> variationObj = null;
                                    if (layoutIdMap != null) {
                                        variationObj = configurationManager.getEntityVariationObj(layoutIdMap.keySet().iterator().next(), entityObj);
                                    }
                                    //variationObj will be null if sampling fall under control group
                                    boolean result = performBaseCheck(entityObj, variationObj, entityProcessing);

                                    if (result) {
                                        minOrder = Math.min(order, minOrder);
                                        //Logger.d(WebEngageConstant.TAG, "Rule eval adding to evaluation list: " + id);
                                        evaluatedIds.add(id);
                                    } else {
                                       // Logger.d(WebEngageConstant.TAG, "Rule eval failed to add to evaluation list: " + id);
                                    }
                                } else {
                                    //Logger.d(WebEngageConstant.TAG, "Rule eval order <= minOrder: order " + order + " min:" + minOrder + " exper: " + id);
                                }
                            }
                        }
                    }
                    RuleExecutorFactory.getRuleExecutor().setCompetingIds(evaluatedIds);
                } catch (Exception e) {

                }
            } else if (ruleCategory.equals(WebEngageConstant.RuleCategory.SESSION_RULE)) {
                try {
                    ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                    RuleExecutorFactory.getRuleExecutor().reset();
                    evaluatedIds = RuleExecutorFactory.getRuleExecutor().evaluateRulesByCategory(WebEngageConstant.RuleCategory.SESSION_RULE);
                    Iterator<String> iterator = evaluatedIds.iterator();
                    Map<String, Map<String, String>> variationMap = new HashMap<String, Map<String, String>>();
                    Set<String> layoutUrls = new HashSet<>();
                    while (iterator.hasNext()) {
                        String id = iterator.next();
                        Map<String, Object> entityObj = null;
                        if (null != DataHolder.get().getInlineCampaignsData() && null != DataHolder.get().getInlineCampaignsData().get(id)) {
                            entityObj = configurationManager.getEntityObj(id, WebEngageConstant.Entity.INLINE_PERSONALIZATION);
                        } else {
                            entityObj = configurationManager.getEntityObj(id, WebEngageConstant.Entity.NOTIFICATION);
                        }
                        String variationId = performSampling(id, entityObj);
                        if (variationId != null) {
                            Map<String, Object> variationObj = configurationManager.getEntityVariationObj(variationId, entityObj);
                            String layoutId = (String) variationObj.get("layout");
                            layoutUrls.add(configurationManager.getVariationLayoutUrl(layoutId));
                            Map<String, String> layoutIdMap = new HashMap<String, String>();
                            layoutIdMap.put(variationId, layoutId);
                            variationMap.put(id, layoutIdMap);
                        } else {
                            // sampled value falls in control group
                        }
                    }
                    if (!layoutUrls.isEmpty() && eventPayload != null && EventName.VISITOR_NEW_SESSION.equals(eventPayload.getEventName())) {
                        NetworkUtils.preFetchResourcesAsync(layoutUrls, this.applicationContext);
                    }
                    RuleExecutorFactory.getRuleExecutor().setCompetingIds(evaluatedIds);
                    saveSessionEvaluatedIds(evaluatedIds);
//                    Logger.d(WebEngageConstant.TAG, " Evaluation ids after session rule: " + evaluatedIds);
                    saveVariationMap(variationMap);
                } catch (Exception e) {

                }
            } else if (ruleCategory.equals(WebEngageConstant.RuleCategory.EVENT_RULE)) {
                evaluatedIds = RuleExecutorFactory.getRuleExecutor().evaluateRulesByCategory(WebEngageConstant.RuleCategory.EVENT_RULE);
            }
        }
//        Logger.d(WebEngageConstant.TAG, "execute evaluatedIds: " + evaluatedIds + " chain: " + executionChain);

        return evaluatedIds;
    }

    @Override
    protected void postExecute(Object data) {
        List<String> evaluatedIds = (List<String>) data;
        List<String> renderingIds = null;
        //Logger.d(WebEngageConstant.TAG, "postExecute evaluatedIds: " + evaluatedIds + " chain: " + executionChain);
        if (evaluatedIds != null) {
            renderingIds = RuleExecutorFactory.getRuleExecutor().filterRenderingIds(evaluatedIds,
                    executionChain.get(executionChain.size() - 1));
        }
//        Logger.d(WebEngageConstant.TAG, "affter filterRenderingIds evaluatedIds: " + renderingIds + " chain: " + executionChain);
        renderingIds = checkForInLineCampaigns(renderingIds);
        dispatchRenderTopic(renderingIds);
    }

    private List<String> checkForInLineCampaigns(List<String> renderingIds) {
        HashMap<String, Object> qualifiedInlineCampaigns = new HashMap<>();
        List<String> inAppIds = new ArrayList<>();
        if (renderingIds.size() > 0) {
            for (String id : renderingIds) {
                if (null != DataHolder.get().getInlineCampaignsData()
                        && null != DataHolder.get().getInlineCampaignsData().get(id)) {
                    HashMap<String, Object> entityObj = (HashMap<String, Object>) DataHolder.get().getInlineCampaignsData().get(id);
                    String targetView = (String) entityObj.get("targetView");
                    if (!TextUtils.isEmpty(targetView) && qualifiedInlineCampaigns.get(targetView) == null) {
                        qualifiedInlineCampaigns.put(targetView, entityObj);
                    } else {
//                        Logger.d(TAG, "Skipping " + targetView + " for campaign id: " + id + " as already campaign has been added or target is null");
//                        Logger.d(TAG, "Already added campaign - " + qualifiedInlineCampaigns.get(targetView));
                    }
                } else {
                    inAppIds.add(id);
//                if (DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.NOTIFICATION) && DataHolder.get().isAppForeground()) {
//                    if (DataHolder.get().compareAndSetEntityRunningState(false, true)) {
//                        Action renderingAction = new InAppNotificationAction(this.applicationContext);
//                        Map<String, Object> actionAttributes = new HashMap<String, Object>();
//                        actionAttributes.put("action_data", id);
//                        renderingAction.performActionSync(actionAttributes);
//                    }
//                }
                }
            }
            if (executionChain.size() == 1
                    && (executionChain.get(0) == WebEngageConstant.RuleCategory.PAGE_RULE)
                    || (executionChain.get(0) == WebEngageConstant.RuleCategory.EVENT_RULE)) {
                Action renderingAction = new InLinePersonalizationAction(this.applicationContext);
                Map<String, Object> actionAttributes = new HashMap<String, Object>();
                actionAttributes.put("action_data", qualifiedInlineCampaigns);
                renderingAction.performActionSync(actionAttributes);
            }
        }
        return inAppIds;
    }


    private String performSampling(String experimentId, Map<String, Object> entityObj) {
        if (entityObj != null) {
            double lowerBound = 0.0;

            String entityId = experimentId;
            if (entityObj.get("journeyId") != null) {
                entityId = (String) entityObj.get("journeyId");
            }

            double sampledValue = WebEngageUtils.getSampledValue(entityId, userIdentifier);
            List<Object> variations = (List<Object>) entityObj.get("variations");
            if (variations != null) {
                for (int i = 0; i < variations.size(); i++) {
                    Map<String, Object> variation = (Map<String, Object>) variations.get(i);
                    if (variation != null) {
                        double variationSampledValue = variation.get("sampling") == null ? 100.0 : Double.parseDouble(variation.get("sampling") + "");
                        if (sampledValue >= lowerBound && sampledValue < (lowerBound + variationSampledValue)) {
                            return (String) variation.get("id");
                        }
                        lowerBound += variationSampledValue;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Don't waste your time !!!! it returns true or false based
     * on some probability distribution.
     *
     * @param eventPayload
     * @return
     */
    public boolean checkForEventCriterias(EventPayload eventPayload) {
        boolean flag = false;
        Object result = null;
        String eventName = eventPayload.getEventName();
        if (eventName != null) {
            String category = eventPayload.getCategory();
            if (WebEngageConstant.SYSTEM.equals(category) && !eventName.startsWith("we_")) {
                eventName = "we_" + eventName;
            }
            List<EventCriteria> eventCriteriaList = RuleExecutorFactory.getRuleExecutor().getEventCriteriasForEvent(eventName);
            if (eventCriteriaList != null) {
                for (EventCriteria eventCriteria : eventCriteriaList) {
                    result = eventCriteria.getExpression().evaluate();
                    if (result != null && (Boolean) result) {
                        List<Object> path = new ArrayList<Object>();
                        path.add(DataContainer.EVENT.toString());
                        path.add(eventName);
                        if (WebEngageConstant.SYSTEM.equals(eventCriteria.getAttributeCategory())) {
                            path.add("we_wk_sys");
                        }
                        path.add(eventCriteria.getAttribute());
                        Object newValue = DataHolder.get().getData(path);

                        List<Object> arguments = new ArrayList<Object>();
                        arguments.add(newValue);
                        arguments.add(DataHolder.get().getEventCriteria(eventCriteria.getId()));
                        Function function = RuleExecutorFactory.getRuleExecutor().getFunction(eventCriteria.getFunction());
                        result = function != null ? function.onEvaluation(arguments) : null;
                        if (result != null) {
                            flag = true;
                            DataHolder.get().setOrUpdateEventCriteriaValue(userIdentifier, eventCriteria.getId(), (Map<String, Object>) result);
                        }
                    }
                }
            }
        }
        return flag;
    }

    public static boolean performBaseCheck(Map<String, Object> entityObj, Map<String, Object> variationObj,
                                           WebEngageConstant.Entity entity) {
        Long maxTimesPerUser = (Long) entityObj.get("maxTimesPerUser");
        Long totalViewCount = DataHolder.get().getEntityTotalViewCountPerScope(entityObj, entity);
        Long totalClickCount = DataHolder.get().getEntityTotalClickCountPerScope(entityObj, entity);
        Long totalCloseCountSession = DataHolder.get().getEntityTotalCloseCountInSessionPerScope(entityObj, entity);
        Long totalHideCountSession = DataHolder.get().getEntityTotalHideCountInSessionPerScope(entityObj, entity);
        boolean result = true;
        if (maxTimesPerUser != null) {
            result = result && (totalViewCount < maxTimesPerUser);
        }
        if (entityObj.get("targetView") == null) {
            //check only if Inapp
            result = result && (totalClickCount < 1) && (totalCloseCountSession < 1) && (totalHideCountSession < 1);
        }
        if (variationObj != null) {
            //variationObj can be null if sampling fall under control group
            List<String> targetActivities = (List<String>) variationObj.get("targetActivities");
            boolean skipTargetPage = entityObj.containsKey("skipTargetPage") ? Boolean.valueOf(entityObj.get("skipTargetPage").toString()) : false;
            if (targetActivities != null && !targetActivities.isEmpty() && skipTargetPage) {
                String screenPath = DataHolder.get().getScreenPath();
                result = result && (!targetActivities.contains(screenPath));
            }
        }
        Long startTimeStamp = entityObj.get("startTimestamp") == null ? Long.MIN_VALUE : (long) entityObj.get("startTimestamp");
        Long endTimeStamp = entityObj.get("endTimestamp") == null ? Long.MAX_VALUE : (long) entityObj.get("endTimestamp");
        Long currentTimeStamp = System.currentTimeMillis();
        return result && (currentTimeStamp >= startTimeStamp && currentTimeStamp <= endTimeStamp);
    }
}
