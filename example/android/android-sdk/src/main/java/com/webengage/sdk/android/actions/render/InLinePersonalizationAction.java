package com.webengage.sdk.android.actions.render;

import static com.webengage.sdk.android.utils.WebEngageConstant.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.Analytics;
import com.webengage.sdk.android.AnalyticsFactory;
import com.webengage.sdk.android.CallbackDispatcher;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.actions.rules.Rule;
import com.webengage.sdk.android.actions.rules.RuleExecutor;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InLinePersonalizationAction extends Action {
    private Context mContext;

    public InLinePersonalizationAction(Context context) {
        super(context);
        this.mContext = context;

    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        int mCampaignCount = DataHolder.get().getIPCampaignLimit();
        //TODO changed mCampaignCount for testing purpose delete the line below
        //mCampaignCount = 5;
        int _campaignCount = 0;
        List<HashMap<String, Object>> result = new ArrayList<>();
        HashMap<String, Object> qualifiedCampaignMap = (HashMap<String, Object>) actionAttributes.get(RenderingController.ACTION_DATA);
        List<HashMap<String, Object>> properties = DataHolder.get().getInlineProperties();
        if (null != properties) {
            List<String> alreadyAddedProperties = new ArrayList<>();
            for (HashMap<String, Object> _property : properties) {
                HashMap<String, Object> property = new HashMap<>();
                property.putAll(_property);
                String targetView = (String) property.get("targetView");
                if (targetView == null || TextUtils.isEmpty(targetView.trim())) {
                    continue;
                }
                targetView = targetView.trim();
                if (isPropertyRulePasses(property)) {
                    if (null != qualifiedCampaignMap && !qualifiedCampaignMap.containsKey(targetView)) {
                        if (!alreadyAddedProperties.contains(targetView)) {
                            alreadyAddedProperties.add(targetView);
                            result.add(property);
                        } else {
                            Logger.d(TAG, "Ignoring property: " + targetView + " with Id: " + property.get("p_id") + " because it is already been consumed");
                        }
                    } else {
                        HashMap<String, Object> entityObject = (HashMap<String, Object>) qualifiedCampaignMap.get(targetView);
                        if (entityObject == null)
                            continue;
                        String experimentId = (String) entityObject.get("notificationEncId");
                        Map<String, String> layoutIdMap = getVariationMap().get(experimentId);
                        if (null == layoutIdMap) {
                            Logger.d(TAG, "Ignoring campaign due to control group: layoutIdMap " + experimentId);
                            handleForControlGroup(experimentId);
                        } else {
                            String variationId = layoutIdMap.keySet().iterator().next();
                            property.put("variationId", variationId);
                            property.put("campaign", entityObject);
                            if (variationId != null) {
                                property.put("params", getParams(variationId));
                            }
                            if (_campaignCount < mCampaignCount) {
                                if (!alreadyAddedProperties.contains(targetView)) {
                                    alreadyAddedProperties.add(targetView);
                                    result.add(property);
                                    _campaignCount++;
                                } else {
                                    Logger.d(TAG, "Ignoring property: " + targetView + " with Id: " + property.get("p_id") + " because it is already been consumed");
                                }
                            } else {
                                Logger.d(TAG, "Ignoring property: _campaignCount > mCampaignCount variationId "+variationId);
                            }
                        }
                    }
                } else {
                    Logger.d(TAG, "Ignoring property: " + targetView + " with Id: " + property.get("p_id") + " because screen rule failed");
                }
            }
        }
       // Logger.d(TAG, "InLinePersonalizationAction show campaign  pre-execute: " + new JSONArray(result));
        return result;
    }

    @Override
    protected Object execute(Object data) {
        HashMap<String, Object> personalizationResult = new HashMap<>();
        List<HashMap<String, Object>> result = (List<HashMap<String, Object>>) data;
        HashMap<String, Object> systemData = new HashMap<>();
        if (result.size() > 0) {
            systemData.put("luid", getLUID());
            systemData.put("cuid", getCUID());
            systemData.put("base_url", WebEngageConstant.Urls.PERSONALISATION_BASE.toString());
            personalizationResult.put("systemData", systemData);
            personalizationResult.put("properties", data);
            Analytics analytics = AnalyticsFactory.getAnalytics(mContext);
            WeakReference<Activity> activityWeakReference = analytics.getActivity();
            if (null != activityWeakReference.get()
                    && !activityWeakReference.get().isFinishing()) {
                CallbackDispatcher.init(mContext).propertiesReceived(activityWeakReference, personalizationResult);
            }
        }

        return null;
    }

    private String getParams(String variationId) {
        Map<String, List<Object>> tokens = DataHolder.get().getTokens();
        if (tokens != null) {
            List<Object> variationTokens = (List<Object>) tokens.get(variationId);
            if (variationTokens != null) {
                for (Object obj : variationTokens) {
                    List<Object> variable = (ArrayList<Object>) obj;
                    if (variable != null && variable.size() > 0) {
                        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(variable);
                        if (result != null) {
                            List<Object> list = new ArrayList<>();
                            list.add(variationId);
                            list.addAll(variable);
                            DataHolder.get().setData(list, result);

                        }
                    }
                }
            }
        }
        HashMap<String, Object> data = (HashMap<String, Object>) DataHolder.get().getData(variationId);
        if (data == null)
            data = new HashMap<String, Object>();
        String params = "";
        try {
            params = (String) DataType.convert(data, DataType.STRING, true);
        } catch (Exception e) {
        }
        return params;
    }

    private void handleForControlGroup(String experimentId) {
        try {
            ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
            Map<String, Object> entityObj = configurationManager.getEntityObj(experimentId,
                    WebEngageConstant.Entity.INLINE_PERSONALIZATION);
            if (entityObj.get("controlGroup") != null
                    && Long.parseLong(entityObj.get("controlGroup").toString()) > 0) {
                Logger.d(TAG, "InLinePersonalizationAction (" + experimentId + ") has fallen in control group");
                Map<String, Object> systemData = new HashMap<String, Object>();
                systemData.put(WebEngageConstant.EXPERIMENT_ID, experimentId);
                Intent notificationControlGroupEvent = IntentFactory.newIntent(Topic.EVENT,
                        EventFactory.newSystemEvent(EventName.INLINE_CONTROL_GROUP,
                                systemData, null, null, applicationContext),
                        applicationContext);
                WebEngage.startService(notificationControlGroupEvent, applicationContext);
            }
        } catch (Exception e) {
        }
    }

    private boolean isPropertyRulePasses(Map<String, Object> property) {
        RuleExecutor ruleExecutor = RuleExecutorFactory.getRuleExecutor();
        String customRule = String.valueOf(property.get("pageRuleCode"));
        Rule rule = new Rule(customRule);
        return ruleExecutor.evaluateRule(rule, WebEngageConstant.RuleCategory.CUSTOM_RULE);
    }

    @Override
    protected void postExecute(Object data) {

    }
}
