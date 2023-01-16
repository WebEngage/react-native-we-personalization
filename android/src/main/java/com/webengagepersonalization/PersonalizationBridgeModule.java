package com.webengagepersonalization;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.WebEngage;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.util.Log;

@ReactModule(name = PersonalizationBridgeModule.NAME)
public class PersonalizationBridgeModule extends ReactContextBaseJavaModule implements WEPlaceholderCallback {
  public static final String NAME = "PersonalizationBridge";
  private ReactApplicationContext applicationContext = null;


  public PersonalizationBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.applicationContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
  }

  @ReactMethod
  public void add(double a, double b, Promise promise) {
    promise.resolve(a + b);
  }

  @ReactMethod
   public void immediateCallback(String name, String location, Callback callBack, Callback errorCallback) {
    WEPersonalization.Companion.get().init(); // Initializing Personalization SDK
    WebEngage.get().analytics().screenNavigated("ET_home");
    // WEPersonalization.Companion.get().registerWEPlaceholderCallback("flutter_banner", this);  // Text View

  }

   @ReactMethod
   public void listenerCallback() {
        WritableMap params = Arguments.createMap();
        params.putString("eventProperty", "someValue");
//      sendEvent(this.applicationContext, "EventReminder", params);
   }

   @ReactMethod
public void promiseCallback(String name, Promise promise) {
    try {
        String eventId = "25";
        // Below Handler will delay execution for 5 seconds
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // delayCallback.afterDelay();
                promise.resolve(eventId);

            }
        }, 5 * 1000);
    } catch(Exception e) {
        promise.reject("Create Event Error", e);
    }
  }

//  Call this method to send event to registered event listeners
// ex-  sendEvent(this.applicationContext, "EventReminder", params);
  private void sendEvent(ReactApplicationContext reactContext,
  String eventName, @Nullable WritableMap params) {
    reactContext
     .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
     .emit(eventName, params);
  }



  @Override
  public void onDataReceived(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "OnDataReceived from personalization view manager - "+weCampaignData);
  }

  @Override
  public void onPlaceholderException(String s, String s1, Exception e) {
    Log.d("WebEngage1", "onPlaceholderException from personalization view manager-> \ns- "+s+"\ns1- "+s1 + "\nerror-"+e);

  }

  @Override
  public void onRendered(WECampaignData weCampaignData) {
    Log.d("WebEngage1", "onRendered from personalization view manager");

  }
}
