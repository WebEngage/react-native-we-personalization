package com.webengagepersonalization;

import android.content.Context;
import android.os.Build;
import android.telecom.Call;
import android.util.Log;

import androidx.annotation.NonNull;

import com.webengage.personalization.callbacks.WEPropertyRegistryCallback;
import com.webengage.sdk.android.AmplifyController;
import com.webengage.sdk.android.CallbackDispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Callbacker implements WEPropertyRegistryCallback {
  static HashMap<String, ArrayList<ScreenNavigatorCallback>> mapOfScreenNavigatedCallbacks = new HashMap<>();

  public static void setScreenNavigatorCallback(String screenName, ScreenNavigatorCallback screenNavigatedCallback) {
    if (mapOfScreenNavigatedCallbacks.containsKey(screenName)) {
      ArrayList<ScreenNavigatorCallback> callbacks = mapOfScreenNavigatedCallbacks.get(screenName);
      callbacks.add(screenNavigatedCallback);
    } else {
      ArrayList<ScreenNavigatorCallback> callbacks = new ArrayList<>();
      callbacks.add(screenNavigatedCallback);
      mapOfScreenNavigatedCallbacks.put(screenName, callbacks);
    }
    Logger.d(WEGConstants.TAG, "Callbacker: setScreenNavigatorCallback called for - "+screenName);
  }

  @Override
  public void onPropertyCacheCleared(@NonNull String navigatedScreen) {
    Logger.d(WEGConstants.TAG, "\n\n");
    Logger.d(WEGConstants.TAG, "\n\n################################# \n\n");
    Logger.d(WEGConstants.TAG, "Screen changed! onPropertyCacheCleared called inside callbacker - "+navigatedScreen);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      mapOfScreenNavigatedCallbacks.forEach((key, value) -> {
//        ScreenNavigatorCallback callback = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
        Log.d(WEGConstants.TAG, " Map values- key- "+key+ " value- "+value.size());
        ArrayList<ScreenNavigatorCallback> callbacksList = mapOfScreenNavigatedCallbacks.get(navigatedScreen);
       try {
         for (ScreenNavigatorCallback callback : callbacksList) {
           if (navigatedScreen.equals(key)) {
             Logger.d(WEGConstants.TAG, key + " found inside onPropertyCacheCleared list - triggering calback");
             callback.screenNavigated(navigatedScreen);
           }
         }
       } catch (Exception e) {
         e.printStackTrace();
       }
      });
    }
  }
}
