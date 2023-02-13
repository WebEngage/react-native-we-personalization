package com.webengagepersonalization.Bridge;

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
import com.webengagepersonalization.Utils.WEGConstants;

import androidx.annotation.Nullable;

import android.os.Handler;
import android.util.Log;

// TODO - This might be required for custom part
@ReactModule(name = WEGConstants.PERSONALIZATION_BRIDGE)
public class PersonalizationBridgeModule extends ReactContextBaseJavaModule implements WEPlaceholderCallback {
  private ReactApplicationContext applicationContext = null;

  public PersonalizationBridgeModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.applicationContext = reactContext;
  }

  @Override
  @NonNull
  public String getName() {
    return WEGConstants.PERSONALIZATION_BRIDGE;
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
