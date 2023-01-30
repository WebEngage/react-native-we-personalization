package com.webengagepersonalization;

import android.content.Context;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.sdk.android.AmplifyController;
import com.webengage.sdk.android.CallbackDispatcher;

import java.util.HashMap;

public class Callbacker implements WEPropertyRegistryCallback {
  static HashMap<String, ScreenNavigatorCallback> mapOfScreenNavigatedCallbacks = new HashMap<>();

  public static void setScreenNavigatorCallback(String screenName, ScreenNavigatorCallback screenNavigatedCallback) {
    if(!mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      mapOfScreenNavigatedCallbacks.put(screenName, screenNavigatedCallback);
    }
    Logger.d(WEGConstants.TAG, "setScreenNavigatorCallback called inside callbacker ");

  }

  @Override
  public void onPropertyCacheCleared(@NonNull String navigatedScreen) {
    Logger.d(WEGConstants.TAG, "onPropertyCacheCleared called inside callbacker "+navigatedScreen);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      mapOfScreenNavigatedCallbacks.forEach((key, value) -> {
        ScreenNavigatorCallback callback = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
        Log.d(WEGConstants.TAG, " Map values- "+key+navigatedScreen);
        if(navigatedScreen.equals(key)) {
          Logger.d(WEGConstants.TAG, key+ " found- triggering calback");
          callback.screenNavigated(navigatedScreen);
        }
      });
    }
  }
}
