package com.webengagepersonalization;

import android.content.res.Resources;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.webengage.personalization.data.WECampaignData;

public class Utils {
  public static  void sendEvent(ReactApplicationContext reactContext,
                                String eventName, @Nullable WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

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
