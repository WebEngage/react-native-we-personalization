package com.webengage.sdk.android.actions.gcm;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.actions.exception.GCMRegistrationException;
import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.HashMap;
import java.util.Map;

class GCMRegistrationAction extends Action {
    private Context applicationContext = null;

    protected GCMRegistrationAction(Context context) {
        super(context);
        this.applicationContext = context.getApplicationContext();
    }

    @Override
    public Object preExecute(Map<String, Object> actionAttributes) {
        String projectNumber = WebEngage.get().getWebEngageConfig().getGcmProjectNumber();
        if (projectNumber == null) {
            Logger.e(WebEngageConstant.TAG, "AndroidManifest : GCM Project Number is not set,unable to register");
            return null;
        }
        return projectNumber;
    }

    @Override
    public Object execute(Object data) {
        if (data == null) {
            return null;
        }
        if (!ReflectionUtils.isGoogleCloudMessagingDependencyAdded()) {
            Logger.e(WebEngageConstant.TAG, "GoogleCloudMessaging class not found");
            return null;
        }
        int result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.applicationContext);
        if (result == ConnectionResult.SUCCESS || result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            if (result == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                Logger.w(WebEngageConstant.TAG, "Please update your google play service");
                dispatchExceptionTopic(new GCMRegistrationException("Google play service update required"));
            }
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this.applicationContext);
            try {
                String regID = gcm.register((String) data);
                if (regID != null && !regID.isEmpty() && !getRegistrationID().equals(regID)) {
                    saveRegistrationID(regID);
                    return regID;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                return ex;
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Google play services ");
            switch (result) {
                case ConnectionResult.SERVICE_MISSING:
                    sb.append("is missing ");
                    break;

                case ConnectionResult.SERVICE_DISABLED:
                    sb.append("is disabled ");
                    break;

                case ConnectionResult.SERVICE_UPDATING:
                    sb.append("is currently updating ");
                    break;

                case ConnectionResult.SERVICE_INVALID:
                    sb.append("version is invalid ");
                    break;
            }
            sb.append("on this device");
            Logger.e(WebEngageConstant.TAG, sb.toString());
            dispatchExceptionTopic(new GCMRegistrationException(sb.toString()));
            return null;
        }
    }


    @Override
    public void postExecute(Object data) {
        if (data != null) {
            if (data instanceof String) {
                String regId = (String) data;
                Map<String, Object> eventData = new HashMap<String, Object>();
                eventData.put("gcm_regId", regId);
                eventData.put("gcm_project_number", WebEngage.get().getWebEngageConfig().getGcmProjectNumber());
                dispatchEventTopic(EventFactory.newSystemEvent(EventName.GCM_REGISTERED, null, eventData, null, applicationContext));
                getCallbackDispatcher(this.applicationContext).onGCMRegistered(this.applicationContext, regId);
            } else if (data instanceof Exception) {
                Logger.e(WebEngageConstant.TAG, "GCM Register Error : " + ((Exception) data).getMessage());
                dispatchExceptionTopic(new GCMRegistrationException(((Exception) data).getMessage()));

            }
        }
        GCMRegistrationActionController.shouldDoRegistration.set(false);
    }


}
