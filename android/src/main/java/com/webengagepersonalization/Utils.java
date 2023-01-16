package com.webengagepersonalization;

import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.webengage.personalization.data.WECampaignData;

public class Utils {
  public static  void sendEvent(ReactApplicationContext reactContext,
                                String eventName, @Nullable WritableMap params) {
    Log.d("Ak1", "inside writable param");
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }
}
