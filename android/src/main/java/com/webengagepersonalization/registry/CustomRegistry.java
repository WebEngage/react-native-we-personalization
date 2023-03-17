package com.webengagepersonalization.registry;

import com.webengage.personalization.data.WECampaignData;

import java.util.HashMap;

public class CustomRegistry {
  private static CustomRegistry instance = null;
  private static final Object lock  = new Object();
  HashMap<String, WECampaignData> customMap = new HashMap<>();

  public static CustomRegistry get() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new CustomRegistry();
        }
      }
    }
    return instance;
  }

  public void registerData(String propertyId) {
    customMap.put(propertyId, null);
  }

  public void registerData(String propertyId, WECampaignData weCampaignData) {
    customMap.put(propertyId, weCampaignData);
  }

  public WECampaignData getMapData(String propertyId) {
    if (customMap != null && customMap.containsKey(propertyId)) {
      return customMap.get(propertyId);
    }
    return null;
  }

}
