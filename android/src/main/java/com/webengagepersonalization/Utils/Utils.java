package com.webengagepersonalization.Utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.webengage.personalization.data.WECampaignData;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
  public static  void sendEvent(ReactApplicationContext reactContext,
                                String eventName, @Nullable WritableMap params) {
    Log.d("WebEngage", "SendEvent triggered for "+eventName+" for "+params.getString("targetViewId"));
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }


  public static WritableMap generateParams(WECampaignData weCampaignData) {
    WritableMap params = Arguments.createMap();
    params.putString("targetViewId", weCampaignData.getTargetViewId());
    params.putString("campaignId", weCampaignData.getCampaignId());
    params.putString("payloadData", weCampaignData.toJSONString());
    return params;
  }

  public static WritableMap generateParams(String campaignId, String targetViewId, Exception e) {
    WritableMap params = Arguments.createMap();
    params.putString("targetViewId", targetViewId);
    params.putString("campaignId", campaignId);
    params.putString("Error", e.toString());
    return params;
  }


//  Checks if view is visible in the current viewport for the user
  public static boolean isVisible(final View view) {
    if (view == null) {
      return false;
    }
    if (!view.isShown()) {
      return false;
    }
    final Rect actualPosition = new Rect();
    view.getGlobalVisibleRect(actualPosition);
    final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
    return actualPosition.intersect(screen);
  }
}
