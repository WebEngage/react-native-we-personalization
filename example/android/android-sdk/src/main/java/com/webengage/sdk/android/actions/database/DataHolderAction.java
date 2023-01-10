package com.webengage.sdk.android.actions.database;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.LocationManagerFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.UserDeviceAttribute;
import com.webengage.sdk.android.UserSystemAttribute;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.render.CarouselV1CallToAction;
import com.webengage.sdk.android.actions.render.PushNotificationData;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;
import com.webengage.sdk.android.utils.http.HttpDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DataHolderAction extends Action {

    private Context applicationContext = null;
    private Object dynamicData = null;

    protected DataHolderAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public Object preExecute(Map<String, Object> actionAttributes) {
        return actionAttributes;
    }

    @Override
    public Object execute(Object data) {
        Map<String, Object> actionAttributes = (Map<String, Object>) data;
        String actionType = (String) actionAttributes.get(DataController.ACTION_TYPE);
        if (DataController.GCM.equals(actionType)) {
            Bundle bundle = (Bundle) actionAttributes.get(DataController.ACTION_DATA);
            String messageAction = bundle.getString(WebEngageConstant.GCM_MESSAGE_ACTION_KEY);
            if (WebEngageConstant.SHOW_SYSTEM_TRAY_NOTIFICATION.equalsIgnoreCase(messageAction)) {
                String notificationProperties = bundle.getString("message_data");
                JSONObject json = null;
                try {
                    json = new JSONObject(notificationProperties);
                } catch (JSONException e) {
                    dispatchExceptionTopic(e);
                }
                if (json != null) {
                    String id = json.optString("identifier");
                    saveVolatileData(id, json.toString());
                }
            } else if (WebEngageConstant.PING.equalsIgnoreCase(messageAction)) {
                String pingData = bundle.getString("message_data");
                try {
                    Map<String, Object> systemData = null;
                    if (pingData != null) {
                        systemData = (Map<String, Object>) DataType.convert(pingData, DataType.MAP, false);
                    }
                    dispatchEventTopic(EventFactory.newSystemEvent(EventName.PUSH_PING, systemData, null, null, applicationContext));

                } catch (Exception e) {

                }
            } else if (WebEngageConstant.CONFIG.equals(messageAction)) {
                Intent intent = IntentFactory.newIntent(Topic.CONFIG_REFRESH, null, applicationContext);
                WebEngage.startService(intent, applicationContext);
            } else if (WebEngageConstant.FETCH_PROFILE.equals(messageAction)) {
                Intent intent = IntentFactory.newIntent(Topic.FETCH_PROFILE, null, applicationContext);
                WebEngage.startService(intent, applicationContext);
            } else if (WebEngageConstant.JCX.equals(messageAction)) {
                Intent intent = IntentFactory.newIntent(Topic.JOURNEY_CONTEXT, null, applicationContext);
                WebEngage.startService(intent, applicationContext);
            }


        } else if (DataController.EVENT.equalsIgnoreCase(actionType) || DataController.INTERNAL_EVENT.equalsIgnoreCase(actionType)) {
            String id = null;
            String userIdentifier = null;
            EventPayload eventPayload = (EventPayload) actionAttributes.get(DataController.ACTION_DATA);
            eventPayload.setCUID(getCUID());
            eventPayload.setSUID(getSUID());
            eventPayload.setLUID(getLUID());
            eventPayload.setEventTime(new Date());
            String event = eventPayload.getEventName();
            SystemDataFactory systemDataFactory = new SystemDataFactory(applicationContext);
            if (event != null && WebEngageConstant.SYSTEM.equals(eventPayload.getCategory())) {
                userIdentifier = getCUID().isEmpty() ? getLUID() : getCUID();
                Map<String, Object> systemData = null;
                Map<String, Object> eventData = null;
                if (EventName.USER_UPDATE_GEO_INFO.equals(event) || EventName.USER_UPDATE.equals(event) || EventName.USER_DELETE_ATTRIBUTES.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, systemData);

                    if (EventName.USER_UPDATE_GEO_INFO.equals(event)) {
                        Double latitude = (Double) systemData.get(UserDeviceAttribute.LATITUDE.toString());
                        Double longitude = (Double) systemData.get(UserDeviceAttribute.LONGITUDE.toString());
                        if (latitude != null && longitude != null) {
                            Map<String, Object> locationComponents = systemDataFactory.getLocationAddress(latitude, longitude);
                            DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, locationComponents);
                            if (locationComponents != null) {
                                systemData.putAll(locationComponents);
                                eventPayload.setSystemData(systemData);
                            }
                        }
                    }

                    eventData = eventPayload.getEventData();
                    DataHolder.get().setOrUpdateUsersCustomAttributes(userIdentifier, eventData);
                    if (EventName.USER_UPDATE.equals(event)) {
                        Logger.d(WebEngageConstant.TAG, "User attributes successfully saved");
                    } else if (EventName.USER_DELETE_ATTRIBUTES.equals(event)) {
                        Logger.d(WebEngageConstant.TAG, "User attributes successfully removed");
                    }
                } else if (EventName.USER_INCREMENT.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    DataHolder.get().incrementUsersSystemAttributes(userIdentifier, systemData);

                    eventData = eventPayload.getEventData();
                    DataHolder.get().incrementUsersCustomAttributes(userIdentifier, eventData);
                } else if (EventName.USER_LOGGED_IN.equals(event)) {
                    String cuid = getCUID();
                    UserProfileDataManager.getInstance(applicationContext).linkLUIDToCUID(cuid, getLUID());
                    if (DataHolder.get().getUserLastLoggedIn() == null) {
                        Map<String, Object> firstLoggedIn = new HashMap<String, Object>();
                        firstLoggedIn.put(UserDeviceAttribute.FIRST_LOGGED_IN.toString(), new Date());
                        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, firstLoggedIn);
                    }
                    Map<String, Object> lastLoggedIn = new HashMap<String, Object>();
                    lastLoggedIn.put(UserDeviceAttribute.LAST_LOGGED_IN.toString(), new Date());
                    DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, lastLoggedIn);
                    Map<String, Object> userData = UserProfileDataManager.getInstance(this.applicationContext).getAllUserData(userIdentifier);
                    DataHolder.get().silentSetData(userData);
                    Logger.d(WebEngageConstant.TAG, "User successfully Logged in");
                    DataHolder.get().setOrUpdateUserProfile(userIdentifier, "cuid", cuid, DataContainer.USER, Operation.FORCE_UPDATE);
                    appendCustomScreenData(eventPayload);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.USER_LOGGED_OUT.equals(event)) {
                    Logger.d(WebEngageConstant.TAG, "User successfully Logged out");
                    appendCustomScreenData(eventPayload);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.NOTIFICATION_CLICK.equals(event)
                        || EventName.INLINE_PERSONALIZATION_CLICK.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    String notificationId = (String) systemData.get(WebEngageConstant.EXPERIMENT_ID);
                    try {
                        ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                        WebEngageConstant.Entity entity = (EventName.INLINE_PERSONALIZATION_CLICK.equals(event))
                                ? (WebEngageConstant.Entity.INLINE_PERSONALIZATION)
                                : (WebEngageConstant.Entity.NOTIFICATION);
                        Map<String, Object> entityObj = configurationManager.getEntityObj(notificationId, entity);
                        String scopeString = DataHolder.get().getScopeStringForExperiment(entityObj, entity);
                        DataHolder.get().incrementUserProfile(userIdentifier,
                                scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLICK,
                                1l, DataContainer.SCOPES);
                        appendCustomScreenData(eventPayload);
                        systemData.put("total_view_count", DataHolder.get().getEntityTotalViewCountAcrossScopes(notificationId));
                        systemData.put("total_view_count_session", DataHolder.get().getEntityTotalViewCountInSessionAcrossScopes(notificationId));
                        int index = scopeString.indexOf('[');
                        if (index != -1) {
                            systemData.put("scope", scopeString.substring(index + 1, scopeString.indexOf(']', index)));
                        }
                        if (entityObj.get("journeyId") != null) {
                            systemData.put("journey_id", entityObj.get("journeyId"));
                        }
                        eventPayload.setSystemData(systemData);
                        appendSystemData(eventPayload, systemDataFactory, false);
                        reportEventToDataHolder(eventPayload);
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }
                } else if (EventName.NOTIFICATION_VIEW.equals(event)
                        || EventName.INLINE_PERSONALIZATION_VIEW.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    String notificationId = (String) systemData.get(WebEngageConstant.EXPERIMENT_ID);
                    try {
                        ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                        WebEngageConstant.Entity entity = (EventName.INLINE_PERSONALIZATION_VIEW.equals(event))
                                ? (WebEngageConstant.Entity.INLINE_PERSONALIZATION)
                                : (WebEngageConstant.Entity.NOTIFICATION);
                        Map<String, Object> entityObj = configurationManager.getEntityObj(notificationId, entity);
                        String scopeString = DataHolder.get().getScopeStringForExperiment(entityObj, entity);
                        DataHolder.get().incrementUserProfile(userIdentifier,
                                scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW,
                                1L, DataContainer.SCOPES);
                        DataHolder.get().incrementUserProfile(userIdentifier,
                                notificationId + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW_SESSION,
                                1L, DataContainer.SCOPES);
                        String topLevelViewCount = notificationId + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.VIEW;
                        if (!scopeString.equals(notificationId)) {
                            DataHolder.get().incrementUserProfile(userIdentifier, topLevelViewCount, 1L, DataContainer.SCOPES);
                        }

                        appendCustomScreenData(eventPayload);

                        systemData.put("total_view_count", DataHolder.get().getEntityTotalViewCountAcrossScopes(notificationId));
                        systemData.put("total_view_count_session", DataHolder.get().getEntityTotalViewCountInSessionAcrossScopes(notificationId));
                        int index = scopeString.indexOf('[');
                        if (index != -1) {
                            systemData.put("scope", scopeString.substring(index + 1, scopeString.indexOf(']', index)));
                        }
                        if (entityObj.get("journeyId") != null) {
                            systemData.put("journey_id", entityObj.get("journeyId"));
                        }

                        eventPayload.setSystemData(systemData);
                        appendSystemData(eventPayload, systemDataFactory, false);
                        reportEventToDataHolder(eventPayload);
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }
                } else if (EventName.NOTIFICATION_CLOSE.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    String notificationId = (String) systemData.get(WebEngageConstant.EXPERIMENT_ID);
                    try {
                        ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                        Map<String, Object> entityObj = configurationManager.getEntityObj(notificationId, WebEngageConstant.Entity.NOTIFICATION);
                        String scopeString = DataHolder.get().getScopeStringForExperiment(entityObj, WebEngageConstant.Entity.NOTIFICATION);
                        DataHolder.get().incrementUserProfile(userIdentifier, scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLOSE_SESSION, 1l, DataContainer.SCOPES);
                        DataHolder.get().incrementUserProfile(userIdentifier, scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.CLOSE, 1l, DataContainer.SCOPES);
                        appendCustomScreenData(eventPayload);
                        systemData.put("total_view_count", DataHolder.get().getEntityTotalViewCountAcrossScopes(notificationId));
                        systemData.put("total_view_count_session", DataHolder.get().getEntityTotalViewCountInSessionAcrossScopes(notificationId));
                        int index = scopeString.indexOf('[');
                        if (index != -1) {
                            systemData.put("scope", scopeString.substring(index + 1, scopeString.indexOf(']', index)));
                        }
                        if (entityObj.get("journeyId") != null) {
                            systemData.put("journey_id", entityObj.get("journeyId"));
                        }
                        eventPayload.setSystemData(systemData);
                        appendSystemData(eventPayload, systemDataFactory, false);
                        reportEventToDataHolder(eventPayload);
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }

                } else if (EventName.NOTIFICATION_CONTROL_GROUP.equals(event) || EventName.INLINE_CONTROL_GROUP.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    String notificationId = (String) systemData.get(WebEngageConstant.EXPERIMENT_ID);
                    try {
                        ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                        Map<String, Object> entityObj = configurationManager.getEntityObj(notificationId, (EventName.INLINE_CONTROL_GROUP.equals(event)) ? (WebEngageConstant.Entity.INLINE_PERSONALIZATION) : (WebEngageConstant.Entity.NOTIFICATION));
                        String scopeString = DataHolder.get().getScopeStringForExperiment(entityObj, (EventName.INLINE_CONTROL_GROUP.equals(event)) ? (WebEngageConstant.Entity.INLINE_PERSONALIZATION) : (WebEngageConstant.Entity.NOTIFICATION));
                        DataHolder.get().incrementUserProfile(userIdentifier, scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.HIDE_SESSION, 1l, DataContainer.SCOPES);
                        DataHolder.get().incrementUserProfile(userIdentifier, scopeString + WebEngageConstant.SCOPE_SEPARATOR + WebEngageConstant.HIDE, 1l, DataContainer.SCOPES);
                        appendCustomScreenData(eventPayload);
                        int index = scopeString.indexOf('[');
                        if (index != -1) {
                            systemData.put("scope", scopeString.substring(index + 1, scopeString.indexOf(']', index)));
                        }

                        String entityId = notificationId;

                        if (entityObj.get("journeyId") != null) {
                            entityId = (String) entityObj.get("journeyId");
                            systemData.put("journey_id", entityId);
                        }
                        eventPayload.setSystemData(systemData);
                        appendSystemData(eventPayload, systemDataFactory, false);
                        eventData = eventPayload.getEventData();
                        if (eventData == null) {
                            eventData = new HashMap<String, Object>();
                        }
                        eventData.put("control_group", entityObj.get("controlGroup"));
                        eventData.put("bucket_value", WebEngageUtils.getSampledValue(entityId, userIdentifier));
                        eventPayload.setEventData(eventData);

                        reportEventToDataHolder(eventPayload);
                    } catch (Exception e) {
                        dispatchExceptionTopic(e);
                    }

                } else if (EventName.PUSH_NOTIFICATION_RECEIVED.equals(event)) {
                    appendCustomKeyValue(eventPayload, WebEngageConstant.Entity.PUSH);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.PUSH_NOTIFICATION_CLOSE.equals(event)) {
                    dynamicData = eventPayload.getSystemData();
                    id = (String) ((Map<String, Object>) dynamicData).get(WebEngageConstant.NOTIFICATION_ID);
                    appendCustomKeyValue(eventPayload, WebEngageConstant.Entity.PUSH);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                    clearPushImageCache(id);
                    removeVolatileData(id);
                } else if (EventName.PUSH_NOTIFICATION_VIEW.equals(event)) {
                    appendCustomKeyValue(eventPayload, WebEngageConstant.Entity.PUSH);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.PUSH_NOTIFICATION_CLICK.equals(event) || EventName.PUSH_NOTIFICATION_RATING_SUBMITTED.equals(event)) {
                    dynamicData = eventPayload.getSystemData();
                    id = (String) ((Map<String, Object>) dynamicData).get(WebEngageConstant.NOTIFICATION_ID);
                    boolean shouldDismiss = true;


                    Map<String, Object> extraData = eventPayload.getExtraData();
                    if (extraData != null) {
                        shouldDismiss = (boolean) extraData.get(WebEngageConstant.DISMISS_ON_CLICK);

                    }

                    appendCustomKeyValue(eventPayload, WebEngageConstant.Entity.PUSH);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                    if (shouldDismiss) {
                        clearPushImageCache(id);
                        removeVolatileData(id);
                    }
                } else if (EventName.PUSH_NOTIFICATION_ITEM_VIEW.equals(event)) {
                    appendCustomKeyValue(eventPayload, WebEngageConstant.Entity.PUSH);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.WE_WK_ACTIVITY_START.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    Map<String, Object> systemScreenData = DataHolder.get().getSystemScreenData();
                    if (systemScreenData == null) {
                        systemScreenData = new HashMap<String, Object>();
                    }
                    if (systemData != null) {
                        systemScreenData.putAll(systemData);
                    }
                    DataHolder.get().setSystemScreenData(systemScreenData);
                } else if (EventName.WE_WK_SCREEN_NAVIGATED.equals(event)) {
                    DataHolder.get().clearScreenEvents();
                    systemData = eventPayload.getSystemData();
                    Map<String, Object> systemScreenData = DataHolder.get().getSystemScreenData();
                    if (systemScreenData == null) {
                        systemScreenData = new HashMap<String, Object>();
                    }
                    if (systemData != null) {
                        systemScreenData.putAll(systemData);
                    }
                    DataHolder.get().setSystemScreenData(systemScreenData);
                    DataHolder.get().incrementUserProfile(userIdentifier, "page_view_count_session", 1l, DataContainer.ANDROID);
                    DataHolder.get().incrementUserProfile(userIdentifier, "total_page_view_count", 1l, DataContainer.ANDROID);
                    eventData = eventPayload.getEventData();
                    DataHolder.get().setCustomScreenData(eventData);

                } else if (EventName.VISITOR_NEW_SESSION.equals(event)) {
                    String cuid = getCUID();
                    Map<String, Object> allUserData = UserProfileDataManager.getInstance(this.applicationContext).getAllUserData(cuid.isEmpty() ? getLUID() : cuid);
                    if (allUserData != null) {
                        if (allUserData.size() > 0) {
                            DataHolder.get().silentSetData(allUserData);
                        }
                    }
                    systemData = eventPayload.getSystemData();
                    DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, systemData);
                    if ("online".equals(DataHolder.get().getLatestSessionType())) {
                        Map<String, Object> sessonCount = new HashMap<String, Object>();
                        sessonCount.put(UserDeviceAttribute.SESSION_COUNT.toString(), 1l);
                        DataHolder.get().incrementUsersSystemAttributes(userIdentifier, sessonCount);
                        Long sessionCount = DataHolder.get().getForegroundSessionCount();
                        if (sessionCount != null && sessionCount == 1l) {
                            DataHolder.get().setOrUpdateUserProfile(userIdentifier,
                                    UserDeviceAttribute.FIRST_SESSION_START_TIME.toString(), new Date(), DataContainer.ANDROID);
                            DataHolder.get().setOrUpdateUserProfile(userIdentifier,
                                    UserSystemAttribute.CREATED_AT.toString(), new Date(), DataContainer.USER);
                            DataHolder.get().setOrUpdateUserProfile(userIdentifier,
                                    UserSystemAttribute.REFERRER_TYPE.toString(), "direct", DataContainer.USER);
                        }
                    } else {
                        DataHolder.get().incrementUserProfile(userIdentifier, "b_session_count", 1l, DataContainer.ANDROID);
                    }

                    if (WebEngage.get().getWebEngageConfig().isLocationTrackingEnabled()) {
                        Location location = LocationManagerFactory.getLocationManager(applicationContext).getLastKnownLocation();
                        if (location != null) {
                            Map<String, Object> locationData = new HashMap<String, Object>();
                            locationData.put(UserDeviceAttribute.LATITUDE.toString(), location.getLatitude());
                            locationData.put(UserDeviceAttribute.LONGITUDE.toString(), location.getLongitude());
                            Map<String, Object> locationComponents = systemDataFactory.getLocationAddress(location.getLatitude(), location.getLongitude());
                            if (locationComponents != null) {
                                locationData.putAll(locationComponents);
                            }
                            DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, locationData);
                        }
                    }

                    Map<String, Object> sessionData = systemDataFactory.generateNewSessionData();
                    DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, sessionData);
                    appendSystemData(eventPayload, systemDataFactory, true);
                } else if (EventName.VISITOR_SESSION_CLOSE.equals(event)) {
                    if ("online".equals(DataHolder.get().getLatestSessionType())) {
                        Map<String, Object> lastSeen = new HashMap<String, Object>();
                        lastSeen.put(UserDeviceAttribute.LAST_SEEN.toString(), new Date());
                        DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, lastSeen);
                    }
                    appendSystemData(eventPayload, systemDataFactory, false);
                } else if (EventName.APP_INSTALLED.equals(event)) {
                    systemData = eventPayload.getSystemData();
                    DataHolder.get().setOrUpdateUsersSystemAttributes(userIdentifier, systemData);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.APP_UPGRADED.equals(event) || EventName.APP_CRASHED.equals(event)) {
                    Map<String, Object> sessionData = systemDataFactory.generateNewSessionData();
                    DataHolder.get().setOrUpdateUsersDeviceAttributes(userIdentifier, sessionData);
                    appendCustomScreenData(eventPayload);
                    appendSystemData(eventPayload, systemDataFactory, false);
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.GCM_REGISTERED.equals(event)) {
                    appendSystemData(eventPayload, systemDataFactory, false);
                } else if (EventName.PUSH_PING.equals(event)) {
                    appendSystemData(eventPayload, systemDataFactory, false);
                } else if (EventName.WE_WK_SESSION_DELAY.equals(event)) {
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.WE_WK_PAGE_DELAY.equals(event)) {
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.WE_WK_LEAVE_INTENT.equals(event)) {
                    reportEventToDataHolder(eventPayload);
                } else if (EventName.GEOFENCE_TRANSITION.equals(event)) {   //whether it will be targetable or not

                }

            } else {
                //custom event
                appendSystemData(eventPayload, systemDataFactory, false);
                reportEventToDataHolder(eventPayload);
            }

        } else if (DataController.CHANGE_DATA.equals(actionType)) {
            HashMap<String, Object> map = (HashMap<String, Object>) actionAttributes.get(DataController.ACTION_DATA);
            if (map != null) {
                ArrayList<Object> containerPath = (ArrayList<Object>) map.get("path");
                if (containerPath != null) {
                    DataHolder.get().setData(containerPath, map.get("data"));
                }
            }
        }
        return null;
    }

    @Override
    public void postExecute(Object data) {

    }


    private void appendCustomScreenData(EventPayload eventPayload) {
        Map<String, Object> screenData = DataHolder.get().getCustomScreenData();
        Map<String, Object> eventData = eventPayload.getEventData();
        Map<String, Object> totalData = new HashMap<String, Object>();
        if (screenData != null) {
            totalData.putAll(screenData);
        }
        if (eventData != null) {
            totalData.putAll(eventData);
        }
        eventPayload.setEventData(totalData);
    }

    private void reportEventToDataHolder(EventPayload eventPayload) {
        Map<String, Object> eventData = eventPayload.getEventData();
        Map<String, Object> systemData = eventPayload.getSystemData();
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (systemData != null) {
            systemData.put("event_time", eventPayload.getEventTime());
            attributes.put("we_wk_sys", systemData);
        }
        if (eventData != null) {
            attributes.putAll(eventData);
        }
        String category = eventPayload.getCategory();
        String eventName = eventPayload.getEventName();
        if (WebEngageConstant.SYSTEM.equals(category) && !eventName.startsWith("we_")) {
            eventName = "we_" + eventName;
        }
        DataHolder.get().setOrUpdateEventAttributes(eventName, attributes);

        Map<String, List<Object>> tokens = DataHolder.get().getTokens();
        if (tokens != null) {
            for (Map.Entry<String, List<Object>> entry : tokens.entrySet()) {
                List<Object> variationTokens = entry.getValue();
                if (variationTokens != null) {
                    for (Object o : variationTokens) {
                        List<Object> variable = (List<Object>) o;
                        if (variable != null && variable.size() != 0 && shouldBeCached(eventPayload, variable)) {
                            DataHolder.get().setOrUpdateLatestEventCache(eventName, attributes);
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean shouldBeCached(EventPayload eventPayload, List<Object> variable) {
        if (eventPayload != null && variable != null && variable.size() > 2 && "event".equals(variable.get(0).toString())) {
            if (eventPayload.getEventName().equals(variable.get(2))) {
                String eventCategory = (String) variable.get(1);
                if (eventCategory != null) {
                    if (WebEngageConstant.CUSTOM.equals(eventCategory)) {
                        return WebEngageConstant.APPLICATION.equals(eventPayload.getCategory());
                    } else if (WebEngageConstant.SYSTEM.equals(eventCategory)) {
                        return WebEngageConstant.SYSTEM.equals(eventPayload.getCategory());
                    }
                }
            }
        }
        return false;
    }

    private void appendSystemData(EventPayload eventPayload, SystemDataFactory systemDataFactory, boolean isNewSession) {
        Map<String, Object> systemData = eventPayload.getSystemData();
        if (systemData == null) {
            systemData = new HashMap<String, Object>();
        }
        eventPayload.setSystemData(systemDataFactory.generateSystemData(systemData, isNewSession));
    }


    private void clearPushImageCache(String variationId) {
        PushNotificationData pushNotificationData = null;
        try {
            pushNotificationData = new PushNotificationData(new JSONObject(getVolatileData(variationId)), this.applicationContext);
        } catch (JSONException e) {

        }
        if (pushNotificationData != null && pushNotificationData.isBigNotification() && pushNotificationData.getStyle() != null) {
            switch (pushNotificationData.getStyle()) {
                case BIG_PICTURE:
                    // no need to remove image cache.

                    break;

                case CAROUSEL_V1:
                    Set<String> urlToRemove = new HashSet<String>();
                    List<CarouselV1CallToAction> callToActions = pushNotificationData.getCarouselV1Data().getCallToActions();
                    if (callToActions != null) {
                        for (CarouselV1CallToAction callToAction : callToActions) {
                            urlToRemove.add(callToAction.getImageURL());
                        }
                    }
                    if (urlToRemove.size() > 0) {
                        HttpDataManager.getInstance(this.applicationContext).removeResourcesByURL(urlToRemove);
                    }

                    break;

                case RATING_V1:
                    urlToRemove = new HashSet<String>();
                    if (pushNotificationData.getRatingV1().getImageUrl() != null) {
                        urlToRemove.add(pushNotificationData.getRatingV1().getImageUrl());
                    }

                    if (pushNotificationData.getRatingV1().getIconUrl() != null) {
                        urlToRemove.add(pushNotificationData.getRatingV1().getIconUrl());
                    }

                    if (urlToRemove.size() > 0) {
                        HttpDataManager.getInstance(this.applicationContext).removeResourcesByURL(urlToRemove);
                    }

                    break;
            }
        }
    }


    private void appendCustomKeyValue(EventPayload eventPayload, WebEngageConstant.Entity entity) {
        if (entity != null) {
            switch (entity) {
                case PUSH:
                    Map<String, Object> systemData = eventPayload.getSystemData();
                    if (systemData != null) {
                        String variationId = (String) systemData.get(WebEngageConstant.NOTIFICATION_ID);
                        if (variationId != null) {
                            try {
                                PushNotificationData pushNotificationData = new PushNotificationData(new JSONObject(getVolatileData(variationId)), this.applicationContext);
                                if (pushNotificationData.getCustomData() != null) {
                                    Map<String, Object> totalData = new HashMap<String, Object>();
                                    Map<String, Object> eventData = eventPayload.getEventData();
                                    if (eventData != null) {
                                        totalData.putAll(eventData);
                                    }
                                    totalData.putAll(WebEngageUtils.bundleToMap(pushNotificationData.getCustomData()));
                                    eventPayload.setEventData(totalData);
                                }
                            } catch (Exception e) {

                            }
                        }
                    }

                    break;


                case NOTIFICATION:


                    break;

                default:


                    break;
            }
        }
    }

}
