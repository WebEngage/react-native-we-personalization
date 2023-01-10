package com.webengagepersonalization.Views;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Registry {
  private String propertyId;
  private String screenName;
  public static Registry instance;



  public static Registry getInstance() {
    if (instance == null) {
        synchronized (Registry.class) {
          if (instance == null) {
            instance = new Registry();
          }
        }
    }
    return  instance;
  }

  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  public void setPropertyId(String propertyId) {
    this.propertyId = propertyId;
  }

  public Map getRegistryData() {
    Map<String, String> registryMap = new HashMap<>();
    registryMap.put("screenName", this.screenName);
    registryMap.put("propertyId", this.propertyId);
    return  registryMap;

  }

}
