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
import com.webengage.personalization.WEPersonalization;
import com.webengage.personalization.callbacks.WEPlaceholderCallback;
import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.WebEngage;
import com.webengagepersonalization.Views.Registry;
import android.widget.FrameLayout;
import android.widget.Toast;

import javax.annotation.Nullable;


public class WebengagePersonalizationViewManager extends SimpleViewManager<View> implements WEPlaceholderCallback {
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
    WEPersonalization.Companion.get().init(); // Initializing Personalization SDK
//    The below code will trigger the callback called from Native View Method
    WritableMap event = Arguments.createMap();
    event.putString("customData", "event data");
    final Context context = view.getContext();
    if (context instanceof ReactContext) {
      ((ReactContext) context).getJSModule(RCTEventEmitter.class)
        .receiveEvent(view.getId(),
          "personalizationCallback", event);
    }
    WebEngage.get().analytics().screenNavigated("akshay");
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("ak_test_2", this);
    WEPersonalization.Companion.get().registerWEPlaceholderCallback("ak_test_1", this);
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("S1P1", this);
//    WEPersonalization.Companion.get().registerWEPlaceholderCallback("property_1", this);
    view.setBackgroundColor(Color.parseColor(color));
  }

  @ReactProp(name = "screenName")
  public void setScreenName(View view, String screenName) {
    Log.d("WebEngage", "Screen Name to register -> "+screenName);
    Registry.getInstance().setScreenName(screenName);
    WebEngage.get().analytics().screenNavigated(screenName);
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
