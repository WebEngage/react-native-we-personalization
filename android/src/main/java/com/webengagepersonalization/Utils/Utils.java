package com.webengagepersonalization.Utils;

import android.content.res.Resources;
import android.graphics.Rect;
import android.text.PrecomputedText;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.webengage.personalization.data.WECampaignData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      params.putString("trackImpression", "PersonalizationBridge.trackImpression");
      params.putString("trackClick", "PersonalizationBridge.trackClick");

    return params;
  }

  public static WritableMap generateParams(String campaignId, String targetViewId, Exception e) {
    WritableMap params = Arguments.createMap();
    params.putString("targetViewId", targetViewId);
    params.putString("campaignId", campaignId);
    params.putString("exception", e.toString());
    return params;
  }

  public static WritableMap generateParams(String actionId, String deepLink, WECampaignData weCampaignData) {
    WritableMap params = Arguments.createMap();
    params.putString("targetViewId", weCampaignData.getTargetViewId());
    params.putString("campaignId", weCampaignData.getCampaignId());
    params.putString("payloadData", weCampaignData.toJSONString());
    params.putString("actionId", actionId);
    params.putString("deepLink", deepLink);
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

  public static Map<String, Object> convertReadableMapToMap(ReadableMap readableMap) {
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
            map.put(key, convertReadableMapToMap(readableMap.getMap(key)));
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

  public static List<Object> convertReadableArrayToList(ReadableArray readableArray) {
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
          list.add(convertReadableMapToMap(readableArray.getMap(i)));
          break;
        case Array:
          list.add(convertReadableArrayToList(readableArray.getArray(i)));
          break;
      }
    }
    return list;
  }
}


