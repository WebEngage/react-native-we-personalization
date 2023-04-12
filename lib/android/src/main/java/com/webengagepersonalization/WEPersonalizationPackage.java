package com.webengagepersonalization;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.webengagepersonalization.bridge.WEPersonalizationBridge;
import com.webengagepersonalization.views.WEPersonalizationViewManager;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class WEPersonalizationPackage implements ReactPackage {
  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    List<NativeModule> modules = new ArrayList<>();
    modules.add(new WEPersonalizationBridge(reactContext)); //
    return modules;
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Arrays.<ViewManager>asList(new WEPersonalizationViewManager(reactContext));
  }
}
