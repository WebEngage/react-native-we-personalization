package com.webengage.sdk.android.actions.render;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.CallbackDispatcher;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.callbacks.CustomPushRender;
import com.webengage.sdk.android.callbacks.CustomPushRerender;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class PushNotificationAction extends Action {
    private final Context applicationContext;
    private PushNotificationData pushNotificationData = null;
    private String id = "";
    private boolean isFirstRun = true;
    private boolean we_render = false;

    private Map<String, Object> systemData = null;
    private Map<String, Object> eventData = null;
    private Map<String, Object> extraData = null;

    protected PushNotificationAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public Object preExecute(Map<String, Object> actionAttributes) {
        String properties = null;

        isFirstRun = (boolean) actionAttributes.get(RenderingController.FIRST_TIME);
        if (!isFirstRun) {
            EventPayload eventPayload = (EventPayload) actionAttributes.get(RenderingController.ACTION_DATA);
            systemData = eventPayload.getSystemData();
            eventData = eventPayload.getEventData();
            extraData = eventPayload.getExtraData();
            if (systemData != null) {
                id = (String) systemData.get(WebEngageConstant.NOTIFICATION_ID);
            }
        } else {
            id = (String) actionAttributes.get(RenderingController.ACTION_DATA);
        }

        properties = getVolatileData(id);

        try {
            pushNotificationData = new PushNotificationData(new JSONObject(properties), applicationContext);
        } catch (Exception e) {
            dispatchExceptionTopic(e);
            return null;
        }

        if (eventData == null) {
            eventData = new HashMap<String, Object>();
        }
        eventData.put(WebEngageConstant.AMPLIFIED, pushNotificationData.isAmplified());

        if (isFirstRun) {
            systemData = new HashMap<String, Object>();
            systemData.put(WebEngageConstant.EXPERIMENT_ID, pushNotificationData.getExperimentId());
            systemData.put(WebEngageConstant.NOTIFICATION_ID, pushNotificationData.getVariationId());
            dispatchEventTopic(EventFactory.newSystemEvent(EventName.PUSH_NOTIFICATION_RECEIVED, systemData, eventData, null, applicationContext));
            Boolean result = DataHolder.get().getOptInValueForEntity(WebEngageConstant.Entity.PUSH);
            if (!result) {
                Logger.e(WebEngageConstant.TAG, "Push-opt-in is false, hence not rendering.");
                return null;
            }
            if (pushNotificationData != null) {
                PushNotificationData modifiedData = getCallbackDispatcher(this.applicationContext).onPushNotificationReceived(this.applicationContext, pushNotificationData);
                if (modifiedData != null) {
                    pushNotificationData = modifiedData;
                }
                if (pushNotificationData.shouldRender()) {
                    return pushNotificationData;
                }
            }
        } else {
            return pushNotificationData;
        }

        return null;
    }

    @Override
    public Object execute(Object data) {
        if (data != null) {
            boolean isCustomPushRender = pushNotificationData.isCustomRender();

            if (isFirstRun) {
                CustomPushRender customPushRender = null;
                CallbackDispatcher callbackDispatcher = getCallbackDispatcher(this.applicationContext);
                if (isCustomPushRender && callbackDispatcher.isCustomRenderRegistered()) {
                    customPushRender = callbackDispatcher;
                } else {
                    customPushRender = PushRendererFactory.getRender(this.pushNotificationData.getStyle());
                }
                if (customPushRender != null) {
                    return customPushRender.onRender(this.applicationContext, pushNotificationData);
                } else {
                    Logger.e(WebEngageConstant.TAG, "CustomPushRender is null");
                }
            } else {
                Bundle extras = WebEngageUtils.mapToBundle(extraData);
                we_render = extras != null && extras.getBoolean(WebEngageConstant.WE_RENDER, false);
                CustomPushRerender customPushRerender = null;
                if (we_render) {
                    customPushRerender = PushRendererFactory.getRerender(this.pushNotificationData.getStyle());
                } else {
                    customPushRerender = getCallbackDispatcher(this.applicationContext);
                }

                if (customPushRerender != null) {
                    return customPushRerender.onRerender(this.applicationContext, pushNotificationData, extras);
                } else {
                    Logger.e(WebEngageConstant.TAG, "CustomPushRerender is null");
                }
            }
        }
        return null;
    }

    @Override
    public void postExecute(Object data) {
        if (data != null) {
            Boolean shown = (Boolean) data;
            if (shown) {
                if (isFirstRun) {
                    dispatchEventTopic(EventFactory.newSystemEvent(EventName.PUSH_NOTIFICATION_VIEW, systemData, eventData, null, applicationContext));
                    getCallbackDispatcher(this.applicationContext).onPushNotificationShown(this.applicationContext, pushNotificationData);
                } else {
                    if (pushNotificationData.getStyle() == WebEngageConstant.STYLE.CAROUSEL_V1 && pushNotificationData.getCarouselV1Data() != null) {
                        List<CarouselV1CallToAction> carouselV1CallToActions = pushNotificationData.getCarouselV1Data().getCallToActions();

                        int currIndex = pushNotificationData.getCurrentIndex();

                        int prevIndex = 0;
                        if (extraData != null && extraData.containsKey(WebEngageConstant.CURRENT) && extraData.get(WebEngageConstant.CURRENT) != null) {
                            prevIndex = (Integer) extraData.get(WebEngageConstant.CURRENT);
                        }

                        String navigation = WebEngageConstant.RIGHT;
                        if (extraData != null && extraData.containsKey(WebEngageConstant.NAVIGATION) && extraData.get(WebEngageConstant.NAVIGATION) != null) {
                            navigation = (String) extraData.get(WebEngageConstant.NAVIGATION);
                        }

                        systemData.put(WebEngageConstant.CTA_ID, carouselV1CallToActions.get(currIndex).getId());
                        systemData.put(WebEngageConstant.NAVIGATED_FROM, carouselV1CallToActions.get(prevIndex).getId());
                        systemData.put(WebEngageConstant.NAVIGATION, navigation);

                        if (extraData != null) {
                            eventData.putAll(extraData);
                        }

                        Intent notificationBrowsedIntent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.PUSH_NOTIFICATION_ITEM_VIEW, systemData, eventData, null, applicationContext), applicationContext);
                        WebEngage.startService(notificationBrowsedIntent, applicationContext, null);
                    } else {
                        if (pushNotificationData.getStyle() != WebEngageConstant.STYLE.RATING_V1 && !we_render) {
                            if (extraData != null) {
                                eventData.putAll(extraData);
                            }
                            Intent pushRerenderIntent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.PUSH_NOTIFICATION_RERENDER, systemData, eventData, extraData, applicationContext), applicationContext);
                            WebEngage.startService(pushRerenderIntent, applicationContext, null);
                        }
                    }
                }
            } else {
                Logger.e(WebEngageConstant.TAG, "Push notification is not rendered.");
            }
        }
    }
}
