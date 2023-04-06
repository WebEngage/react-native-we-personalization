package com.webengagepersonalization.utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WEUtils {
  public static  void sendEventToHybrid(ReactApplicationContext reactContext,
                                String eventName, @Nullable WritableMap params) {
    Logger.d(WEConstants.TAG, "sendEventToHybrid triggered for "+eventName+" for "+params.getString("targetViewId"));
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }


  public static WritableMap generateParams(WECampaignData weCampaignData) {
    WritableMap params = Arguments.createMap();
    params.putString(WEConstants.TARGETVIEW_ID, weCampaignData.getTargetViewId());
    params.putString(WEConstants.CAMPAIGN_ID, weCampaignData.getCampaignId());
    params.putString(WEConstants.PAYLOAD_DATA, weCampaignData.toJSONString());
    params.putString(WEConstants.TRACK_IMPRESSION, "WEPersonalizationBridge.trackImpression");
    params.putString(WEConstants.TRACK_CLICK, "WEPersonalizationBridge.trackClick");
    return params;
  }

  public static WritableMap generateParams(String campaignId, String targetViewId, Exception e) {
    WritableMap params = Arguments.createMap();
    params.putString(WEConstants.TARGETVIEW_ID, targetViewId);
    params.putString(WEConstants.CAMPAIGN_ID, campaignId);
    params.putString(WEConstants.EXCEPTION, e.toString());
    return params;
  }

  public static WritableMap generateParams(String actionId, String deepLink, WECampaignData weCampaignData) {
    WritableMap params = Arguments.createMap();
    params.putString(WEConstants.TARGETVIEW_ID, weCampaignData.getTargetViewId());
    params.putString(WEConstants.CAMPAIGN_ID, weCampaignData.getCampaignId());
    params.putString(WEConstants.PAYLOAD_DATA, weCampaignData.toJSONString());
    params.putString(WEConstants.ACTION_ID, actionId);
    params.putString(WEConstants.DEEPLINK, deepLink);
    return params;
  }


  //  Checks if view is visible in the current viewport for the user
  public static boolean isInlineWidgetVisible(final View view) {
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

  public static Map<String, Object> convertHybridMapToNativeMap(ReadableMap readableMap) {
    Map<String, Object> map = new HashMap<>();
    if(readableMap != null) {
      ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
      while (iterator.hasNextKey()) {
        String key = iterator.nextKey();
        switch (readableMap.getType(key)) {
          case Null:
            map.put(key, null);
            break;
          case Boolean:
            map.put(key, readableMap.getBoolean(key));
            break;
          case Number:
            map.put(key, readableMap.getDouble(key));
            break;
          case String:
            map.put(key, readableMap.getString(key));
            break;
          case Map:
            map.put(key, convertHybridMapToNativeMap(readableMap.getMap(key)));
            break;
          case Array:
            map.put(key, convertReadableArrayToList(readableMap.getArray(key)));
            break;
        }
      }
      return map;
    } else {
      return  null;
    }
  }

  private static List<Object> convertReadableArrayToList(ReadableArray readableArray) {
    List<Object> list = new ArrayList<>();
    for (int i = 0; i < readableArray.size(); i++) {
      switch (readableArray.getType(i)) {
        case Null:
          list.add(null);
          break;
        case Boolean:
          list.add(readableArray.getBoolean(i));
          break;
        case Number:
          list.add(readableArray.getDouble(i));
          break;
        case String:
          list.add(readableArray.getString(i));
          break;
        case Map:
          list.add(convertHybridMapToNativeMap(readableArray.getMap(i)));
          break;
        case Array:
          list.add(convertReadableArrayToList(readableArray.getArray(i)));
          break;
      }
    }
    return list;
  }
}


