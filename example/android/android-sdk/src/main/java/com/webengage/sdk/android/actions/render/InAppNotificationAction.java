package com.webengage.sdk.android.actions.render;


import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.AnalyticsFactory;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.NetworkUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.http.CachePolicy;
import com.webengage.sdk.android.utils.http.RequestMethod;
import com.webengage.sdk.android.utils.http.RequestObject;
import com.webengage.sdk.android.utils.http.Response;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InAppNotificationAction extends Action {
    private Context applicationContext = null;
    private boolean notificationIsReady = false;
    private boolean isControlGroup = false;
    private String experimentId = null;
    private String variationId = null;
    private String layoutId = null;
    private InAppNotificationData inAppNotificationData = null;

    public InAppNotificationAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        experimentId = (String) actionAttributes.get(RenderingController.ACTION_DATA);
        Map<String, String> layoutIdMap = getVariationMap().get(experimentId);
        if (layoutIdMap != null) {
            variationId = layoutIdMap.keySet().iterator().next();
            layoutId = layoutIdMap.get(variationId);
            notificationIsReady = true;
        } else {
            //sampled value falls under control group
            try {
                ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
                Map<String, Object> entityObj = configurationManager.getEntityObj(experimentId, WebEngageConstant.Entity.NOTIFICATION);
                if (entityObj.get("controlGroup") != null && Long.parseLong(entityObj.get("controlGroup").toString()) > 0) {
                    Logger.d(WebEngageConstant.TAG, "In-app (" + experimentId + ") has fallen in control group");
                    notificationIsReady = false;
                    isControlGroup = true;
                    Map<String, Object> systemData = new HashMap<String, Object>();
                    systemData.put(WebEngageConstant.EXPERIMENT_ID, experimentId);
                    Intent notificationControlGroupEvent = IntentFactory.newIntent(Topic.EVENT,
                            EventFactory.newSystemEvent(EventName.NOTIFICATION_CONTROL_GROUP,
                                    systemData, null, null, applicationContext),
                            applicationContext);
                    WebEngage.startService(notificationControlGroupEvent, applicationContext);
                }
            } catch (Exception e) {

            }

        }
        return notificationIsReady;
    }

    @Override
    protected Object execute(Object data) {
        if (notificationIsReady) {
            try {
                WeakReference<Activity> activityWeakReference = AnalyticsFactory.getAnalytics(this.applicationContext).getActivity();
                String inappData = getNotificationData(variationId);
                inAppNotificationData = new InAppNotificationData(experimentId, variationId, layoutId, inappData);
                JSONObject layoutAttributes = inAppNotificationData.getData().optJSONObject("layoutAttributes");
                boolean allowLandscape = layoutAttributes.optBoolean("allowLandscape", false);
                boolean allowPortrait = layoutAttributes.optBoolean("allowPortrait", false);
                int orientation = activityWeakReference.get().getResources().getConfiguration().orientation;
                if (layoutAttributes.isNull("allowPortrait")) {
                    // old implementation
                    if (!allowLandscape && orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        notificationIsReady = false;
                        return null;
                    }
                } else {
                    if (!(allowLandscape && allowPortrait)) {
                        if ((allowPortrait && orientation == Configuration.ORIENTATION_LANDSCAPE) || (allowLandscape && orientation == Configuration.ORIENTATION_PORTRAIT)) {
                            notificationIsReady = false;
                            return null;
                        }
                    }
                }
                if (activityWeakReference != null && activityWeakReference.get() != null
                        && !activityWeakReference.get().isFinishing() && Build.VERSION.SDK_INT >= 11) {
                    InAppNotificationData modifiedData = getCallbackDispatcher(this.applicationContext).onInAppNotificationPrepared(this.applicationContext, inAppNotificationData);
                    if (modifiedData != null) {
                        inAppNotificationData = modifiedData;
                    }
                    if (inAppNotificationData != null
                            && inAppNotificationData.shouldRender()
                            && activityWeakReference.get() != null
                            && !activityWeakReference.get().isFinishing()) {
                        FragmentManager fragmentManager = activityWeakReference.get().getFragmentManager();
                        RenderDialogFragment renderDialogFragment = new RenderDialogFragment();
                        Bundle bundle = new Bundle();
                        boolean isFullscreen = (activityWeakReference.get().getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
                        bundle.putBoolean("fullscreen", isFullscreen);
                        bundle.putParcelable("notificationData", inAppNotificationData);
                        bundle.putString("baseUrl", DataHolder.get().getBaseUrl());
                        renderDialogFragment.setArguments(bundle);
                        renderDialogFragment.setRetainInstance(true);
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(renderDialogFragment, WebEngageConstant.TAG).commitAllowingStateLoss();
//                        renderDialogFragment.show(fragmentManager, WebEngageConstant.TAG);
                    } else {
                        notificationIsReady = false;
                    }
                } else {
                    notificationIsReady = false;
                }
            } catch (Exception e) {
                dispatchExceptionTopic(e);
                notificationIsReady = false;
                return null;
            }

        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {
        if (!notificationIsReady && !isControlGroup) {
            //not un-setting flag if experiment falls under control group.Flag is unset after execution of notification_control_group event.
            //This is done to avoid firing of multiple control group event which was happening if flag is unset here only.
            DataHolder.get().setEntityRunningState(false);
        }
    }

    private String getNotificationData(String variationId) {
        Map<String, List<Object>> tokens = DataHolder.get().getTokens();
        if (tokens != null) {
            List<Object> variationTokens = tokens.get(variationId);
            if (variationTokens != null) {
                for (Object o : variationTokens) {
                    List<Object> variable = (List<Object>) o;
                    if (variable != null && variable.size() > 0) {
                        Object result = RuleExecutorFactory.getRuleExecutor().getFunction("$we_getResolvedData").onEvaluation(variable);
                        if (result != null) {
                            List<Object> list = new ArrayList<Object>();
                            list.add(variationId);
                            list.addAll(variable);
                            DataHolder.get().setData(list, result);
                        }
                    }
                }
            }
        }

        Map<String, Object> contextData = (Map<String, Object>) DataHolder.get().getData(variationId);
        if (contextData == null) {
            contextData = new HashMap<String, Object>();
        }
        String params = null;
        try {
            params = (String) DataType.convert(contextData, DataType.STRING, true);
        } catch (Exception e) {

        }
        DataHolder.get().setData(variationId, null);
        Map<String, String> postHeaders = new HashMap<String, String>();
        postHeaders.put("Content-Type", "application/json");
        postHeaders.put("Content-Encoding", "gzip");
        RequestObject requestObject = new RequestObject.Builder(WebEngageConstant.Urls.getEntityDataEndPoint(WebEngage.get().getWebEngageConfig().getWebEngageKey(), getLUID(), variationId, getCUID(),"NOTIFICATION-"), RequestMethod.POST, this.applicationContext)
                .setCachePolicy(CachePolicy.GET_DATA_FROM_NETWORK_ONLY_NO_CACHING)
                .setParams(params)
                .setHeaders(postHeaders)
                .build();
        Response response = requestObject.execute();
        String result = null;
        if (response.isReadable()) {
            result = NetworkUtils.readEntireStream(response.getInputStream());
        } else {
            response.closeErrorStream();
        }
        return result;
    }


}
