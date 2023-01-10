package com.webengage.sdk.android.actions.rules;


import android.content.Context;

import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.rules.ruleEngine.Expression;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationManager {
    private Map<String, Object> config;
    private Context applicationContext;
    private long sessionDestroyTime = -1;

    public ConfigurationManager(Map<String, Object> config) {
        this.config = config;
    }

    public ConfigurationManager(Context context) throws Exception {
        this.applicationContext = context.getApplicationContext();
        RequestObject requestObject = new RequestObject.Builder(WebEngageConstant.Urls.getConfigEndPoint(
                WebEngage.get().getWebEngageConfig().getWebEngageKey()),
                RequestMethod.GET,
                context.getApplicationContext())
                .setCachePolicy(CachePolicy.GET_DATA_FROM_CACHE_ONLY)
                .build();
        Response response = requestObject.execute();
        if (response.isReadable()) {
            config = NetworkUtils.getAsMap(response.getInputStream(), false);
        } else {
            response.closeErrorStream();
            throw new IOException("Some error during parsing config");
        }
    }


    public Set<String> initRuntime(RuleExecutor ruleExecutor, DataHolder dataHolder) {
        Map<String, Object> runtimeConfig = new HashMap<>();
        runtimeConfig.put("tzo", config.get("tzo"));
        runtimeConfig.put("events", config.get("events"));
        runtimeConfig.put("gbp", getBaseUrl());
        runtimeConfig.put("geoFences", config.get("geoFences"));
        runtimeConfig.put("upfc", config.get("upfc"));

        Object useLegacyRuleCompilerObj =  config.get("useLegacyRuleCompiler");
        boolean legacyAndroidCompiler = false;
        if(useLegacyRuleCompilerObj != null) {
            legacyAndroidCompiler = Boolean.parseBoolean(useLegacyRuleCompilerObj.toString());
        }
        DataHolder.get().setUseLegacyRuleCompiler(legacyAndroidCompiler);

        Map<String, Rule> ruleMap = new LinkedHashMap<String, Rule>();
        Map<String, List<Object>> tokens = new HashMap<String, List<Object>>();
        Set<String> experimentIds = new HashSet<String>();
        Map<String, Object> inLineCampaigns = new HashMap<>();
        Map<String, Object> inAppCampaigns = new HashMap<>();
        for (WebEngageConstant.EntityTypeIdentifier entityTypeIdentifier : WebEngageConstant.entityTypeIdentifierList) {
            String entityListKey = entityTypeIdentifier.entityListKey;
            String entityExperimentIdKey = entityTypeIdentifier.entityExperimentIdKey;
            List<Object> entityRuleArray = (List<Object>) config.get(entityListKey);
            if (entityRuleArray != null) {
                for (int i = 0; i < entityRuleArray.size(); i++) {
                    List<Object> orderArray = (List<Object>) entityRuleArray.get(i);
                    if (orderArray != null) {
                        for (int j = 0; j < orderArray.size(); j++) {
                            Map<String, Object> entityObject = (Map<String, Object>) orderArray.get(j);
                            if (entityObject != null) {
                                String experimentId = (String) entityObject.get(entityExperimentIdKey);
                                if (experimentId != null) {
                                    if ("personalizationRuleList".equals(entityListKey)) {
                                        inLineCampaigns.put(experimentId, entityObject);
                                    } else if ("notificationRuleList".equals(entityListKey)) {
                                        inAppCampaigns.put(experimentId, entityObject);
                                    }
                                    experimentIds.add(experimentId);
                                    ruleMap.put(experimentId, getRuleFromEntity(entityObject));
                                    List<Object> variations = (List<Object>) entityObject.get("variations");
                                    if (variations != null) {
                                        for (int k = 0; k < variations.size(); k++) {
                                            Map<String, Object> variationObj = (Map<String, Object>) variations.get(k);
                                            if (variationObj != null) {
                                                String variationId = (String) variationObj.get("id");
                                                List<Object> variables = (List<Object>) variationObj.get("tokens");
                                                if (variables != null && variationId != null) {
                                                    tokens.put(variationId, variables);
                                                }
                                            } // look at these fucking braces
                                        }
                                    }
                                }
                            } // OMG!!! its still going.
                        }
                    }
                } //Almost there!!!
            }
        } //Lets never do that again.
        ruleExecutor.setRuleMap(ruleMap);
        ruleExecutor.setEventCriteriaMap(getEventCriterias());
        runtimeConfig.put("tokens", tokens);
        dataHolder.silentSetData(WebEngageConstant.CONFIG, runtimeConfig);
        sessionDestroyTime = config.get("sdt") != null ? (long) config.get("sdt"): -1;
        Logger.d(WebEngageConstant.TAG, "initRuntime sessionDestroyTime: " + sessionDestroyTime);
        dataHolder.setData("inline_campaigns", inLineCampaigns);
        dataHolder.setData("in_app_campaigns", inAppCampaigns);
        dataHolder.setData("inline_properties", config.get("properties"));
        dataHolder.setData("p_campaign_limit", config.get("pCampaignLimit"));
        return experimentIds;
    }

    private Rule getRuleFromEntity(Map<String, Object> entityObject) {
        Map<String, Object> ruleObj = (Map<String, Object>) entityObject.get("rules");
        Rule rule = null;
        if (ruleObj == null) {
            rule = new Rule("true", "true", "true");
        } else {
            String sessionRule = (String) ruleObj.get(WebEngageConstant.RuleCategory.SESSION_RULE.toString());
            String pageRule = (String) ruleObj.get(WebEngageConstant.RuleCategory.PAGE_RULE.toString());
            String eventRule = (String) ruleObj.get(WebEngageConstant.RuleCategory.EVENT_RULE.toString());
            rule = new Rule(sessionRule == null ? "true" : sessionRule, pageRule == null ? "true" : pageRule, eventRule == null ? "true" : eventRule);
        }
        return rule;
    }

    public Set<String> getEntityVariationResources(String experimentId, Map<String, Object> variationObj) {
        Set<String> entityResources = new HashSet<String>();
        String layoutId = (String) variationObj.get("layout");
        entityResources.add(getVariationLayoutUrl(layoutId));
        entityResources.addAll(getVariationImageUrls(variationObj));
        return entityResources;
    }

    public Set<String> getVariationImageUrls(Map<String, Object> variationObj) {
        Set<String> urls = new HashSet<>();
        try {
            ArrayList otherResources = (ArrayList) variationObj.get("resources");
            if (otherResources != null) {
                urls.addAll(otherResources);
            }
        } catch (Exception e) {

        }
        return urls;
    }

    public String getVariationLayoutUrl(String layoutId) {
        return WebEngageConstant.Urls.getLayoutEndPoint(getBaseUrl(), layoutId);
    }

    public Set<String> getEntityAllResource(Map<String, Object> entityObj, WebEngageConstant.Entity type) {
        String experimentIdKey = null;
        switch (type) {
            case INLINE_PERSONALIZATION:
                experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(0).entityExperimentIdKey;
                break;

            case NOTIFICATION:
                experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(1).entityExperimentIdKey;
                break;

            case SURVEY:
                experimentIdKey = WebEngageConstant.entityTypeIdentifierList.get(2).entityExperimentIdKey;
                break;
        }
        String experimentId = (String) entityObj.get(experimentIdKey);
        List<Object> variations = (List<Object>) entityObj.get("variations");
        Set<String> allResources = new HashSet<String>();
        if (variations != null) {
            for (int i = 0; i < variations.size(); i++) {
                Map<String, Object> variationObj = (Map<String, Object>) variations.get(i);
                if (variationObj != null) {
                    allResources.addAll(getEntityVariationResources(experimentId, variationObj));
                }
            }
        }
        return allResources;
    }

    public Map<String, Object> getEntityVariationObj(String variationId, Map<String, Object> entityObj) {
        List<Object> variationArray = (List<Object>) entityObj.get("variations");
        if (variationArray != null) {
            for (int i = 0; i < variationArray.size(); i++) {
                Map<String, Object> variation = (Map<String, Object>) variationArray.get(i);
                if (variation != null) {
                    String id = (String) variation.get("id");
                    if (variationId.equals(id)) {
                        return variation;
                    }
                }
            }
        }
        return null;
    }

    public Map<String, Object> getEntityObj(String experimentId, WebEngageConstant.Entity entity) {
        WebEngageConstant.EntityTypeIdentifier entityTypeIdentifier = null;
        switch (entity) {
            case INLINE_PERSONALIZATION:
                entityTypeIdentifier = WebEngageConstant.entityTypeIdentifierList.get(0);
                break;
            case NOTIFICATION:
                entityTypeIdentifier = WebEngageConstant.entityTypeIdentifierList.get(1);
                break;
            case SURVEY:
                entityTypeIdentifier = WebEngageConstant.entityTypeIdentifierList.get(2);
                break;
        }
        List<Object> entityRuleList = (List<Object>) config.get(entityTypeIdentifier.entityListKey);
        if (entityRuleList != null && experimentId != null && !experimentId.isEmpty()) {
            for (int i = 0; i < entityRuleList.size(); i++) {
                List<Object> orderArray = (List<Object>) entityRuleList.get(i);
                if (orderArray != null) {
                    for (int j = 0; j < orderArray.size(); j++) {
                        Map<String, Object> entityObject = (Map<String, Object>) orderArray.get(j);
                        if (entityObject != null) {
                            String id = (String) entityObject.get(entityTypeIdentifier.entityExperimentIdKey);
                            if (id != null && id.equals(experimentId)) {
                                return entityObject;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private Set<String> getAllNotificationResources() {
        Set<String> notificationResources = new HashSet<String>();
        WebEngageConstant.EntityTypeIdentifier entityTypeIdentifier = WebEngageConstant.entityTypeIdentifierList.get(1);
        List<Object> notificationRuleList = (List<Object>) config.get(entityTypeIdentifier.entityListKey);
        if (notificationRuleList != null) {
            for (int i = 0; i < notificationRuleList.size(); i++) {
                List<Object> orderArray = (List<Object>) notificationRuleList.get(i);
                if (orderArray != null) {
                    for (int j = 0; j < orderArray.size(); j++) {
                        Map<String, Object> entityObject = (Map<String, Object>) orderArray.get(j);
                        if (entityObject != null) {
                            notificationResources.addAll(getEntityAllResource(entityObject, WebEngageConstant.Entity.NOTIFICATION));
                        }
                    }
                }
            }
        }
        return notificationResources;

    }

    private Set<String> getAllSurveyResources() {
        return new HashSet<String>();
    }

    public Set<String> getAllResources() {
        Set<String> global = getGlobalResources();
        global.addAll(getAllNotificationResources());
        global.addAll(getAllSurveyResources());
        global.add(WebEngageConstant.Urls.getConfigEndPoint(WebEngage.get().getWebEngageConfig().getWebEngageKey()));
        return global;
    }

    public String getBaseUrl() {
        return (String) config.get("gbp");
    }

    public Set<String> getGlobalResources() {
        Set<String> globalResources = new HashSet<String>();
        String baseUrl = getBaseUrl();
        if (baseUrl != null) {
            List<String> resources = (List<String>) config.get("grs");
            if (resources != null) {
                for (String s : resources) {
                    globalResources.add(baseUrl + s);
                }
            }
        }
        return globalResources;
    }

    public boolean shouldDoLeaveIntent() {
        Map<String, Object> events = (Map<String, Object>) config.get("events");
        if (events != null) {
            List<Object> leaveIntentArray = (List<Object>) events.get(EventName.WE_WK_LEAVE_INTENT.toString());
            if (leaveIntentArray != null) {
                return leaveIntentArray.get(0) != null && (boolean) leaveIntentArray.get(0);
            }
        }
        return false;
    }

    public List<Object> getEventCriteriaList() {
        return (List<Object>) config.get("ecl");
    }


    private Map<String, List<EventCriteria>> getEventCriterias() {
        Map<String, List<EventCriteria>> eventCriteriaMap = new HashMap<String, List<EventCriteria>>();
        List<Object> eventCriterias = getEventCriteriaList();
        if (eventCriterias != null) {
            for (int i = 0; i < eventCriterias.size(); i++) {
                Map<String, Object> eventCriteriaObj = (Map<String, Object>) eventCriterias.get(i);
                if (eventCriteriaObj != null && eventCriteriaObj.get("function") != null) {
                    EventCriteria eventCriteria = new EventCriteria.Builder()
                            .setId((String) eventCriteriaObj.get("criteria_id"))
                            .setFunction(eventCriteriaObj.get("function").toString())
                            .setAttribute((String) eventCriteriaObj.get("attribute"))
                            .setAttributeCategory((String) eventCriteriaObj.get("attributeCategory"))
                            .setExpression(new Expression((String) eventCriteriaObj.get("rule")))
                            .build();
                    String eventName = (String) eventCriteriaObj.get("eventName");
                    if (eventCriteriaMap.get(eventName) == null) {
                        List<EventCriteria> list = new ArrayList<EventCriteria>();
                        eventCriteriaMap.put(eventName, list);
                    }
                    eventCriteriaMap.get(eventName).add(eventCriteria);
                }
            }
        }
        return eventCriteriaMap;
    }


    public Set<String> getEventCriteriaIds() {
        Set<String> ids = new HashSet<String>();
        List<Object> eventCriterias = getEventCriteriaList();
        if (eventCriterias != null) {
            for (int i = 0; i < eventCriterias.size(); i++) {
                Map<String, Object> eventCriteriaObj = (Map<String, Object>) eventCriterias.get(i);
                if (eventCriteriaObj != null) {
                    ids.add((String) eventCriteriaObj.get("criteria_id"));
                }
            }
        }
        return ids;
    }

    public long getSessionDestroyTime() {
        return sessionDestroyTime;
    }
}
