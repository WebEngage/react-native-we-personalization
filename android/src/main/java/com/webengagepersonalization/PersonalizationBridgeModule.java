package com.webengagepersonalization;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.bridge.Callback;

import android.os.Handler;

@ReactModule(name = PersonalizationBridgeModule.NAME)
public class PersonalizationBridgeModule extends ReactContextBaseJavaModule {
  public static final String NAME = "PersonalizationBridge";

  public PersonalizationBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
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
   public void createCalendarEvent(String name, String location, Callback callBack) {
       String eventId = "event123";
       callBack.invoke(null,eventId);
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
}
