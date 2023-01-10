package com.webengagepersonalization;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import java.util.Map;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
//import android.os.Handler.Callback;
import com.facebook.react.bridge.Callback;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.webengagepersonalization.Views.Registry;

import android.widget.FrameLayout;
import android.widget.Toast;

import javax.annotation.Nullable;


public class WebengagePersonalizationViewManager extends SimpleViewManager<View> {
  public static final String REACT_CLASS = "WebengagePersonalizationView";
  private ReactApplicationContext applicationContext = null;

  public WebengagePersonalizationViewManager(ReactApplicationContext reactContext) {
    super();
    this.applicationContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  @NonNull
  public View createViewInstance(ThemedReactContext reactContext) {
    return new View(reactContext);
  }


  @ReactProp(name = "color")
  public void setColor(View view, String color) {
    Log.d("WebEngage", "inside color data -> ");
//    The below code will trigger the callback called from Native View Method
    WritableMap event = Arguments.createMap();
    event.putString("customData", "event data");
    final Context context = view.getContext();
    if (context instanceof ReactContext) {
      ((ReactContext) context).getJSModule(RCTEventEmitter.class)
        .receiveEvent(view.getId(),
          "personalizationCallback", event);
    }

    view.setBackgroundColor(Color.parseColor(color));
  }

  @ReactProp(name = "screenName")
  public void setScreenName(View view, String screenName) {
    Log.d("WebEngage", "Screen Name to register -> "+screenName);
    Registry.getInstance().setScreenName(screenName);
    Map<String, String> data = Registry.getInstance().getRegistryData();
    Log.d("Akshay", data.toString());
  }

  @ReactProp(name = "propertyId")
  public void setPropertyId(View view, String propertyId) {
    Log.d("WebEngage", "PropertyId to register -> "+propertyId);
    Registry.getInstance().setPropertyId(propertyId);

  }



  public Map getExportedCustomBubblingEventTypeConstants() {
    return MapBuilder.builder().put(
      "topChange",
      MapBuilder.of(
        "phasedRegistrationNames",
        MapBuilder.of("bubbled", "onChange")
      )
    ).build();
  }

  @Override
  public @Nullable Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.of(
      "personalizationCallback",
      MapBuilder.of("registrationName", "personalizationCallback")
      );
  }

}
