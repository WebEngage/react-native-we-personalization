package com.webengagepersonalization;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.webengagepersonalization.Bridge.PersonalizationBridgeModule;
import com.webengagepersonalization.Views.WEGPersonalizationViewManager;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class WebengagePersonalizationPackage implements ReactPackage {
  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
 List<NativeModule> modules = new ArrayList<>();
    modules.add(new PersonalizationBridgeModule(reactContext));
    return modules;
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Arrays.<ViewManager>asList(new WEGPersonalizationViewManager(reactContext));
  }
}
