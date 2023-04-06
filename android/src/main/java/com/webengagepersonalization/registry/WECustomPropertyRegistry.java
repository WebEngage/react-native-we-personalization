package com.webengagepersonalization.registry;

import com.webengage.personalization.data.WECampaignData;

import java.util.HashMap;
public class WECustomPropertyRegistry {
  private static WECustomPropertyRegistry instance = null;
  private static final Object lock  = new Object();
  HashMap<String, WECampaignData> customMap = new HashMap<>();

  public static WECustomPropertyRegistry get() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new WECustomPropertyRegistry();
        }
      }
    }
    return instance;
  }



  public void registerProperty(String propertyId) {
    customMap.put(propertyId, null);
  }

  public void registerProperty(String propertyId, WECampaignData weCampaignData) {
    customMap.put(propertyId, weCampaignData);
  }

  public WECampaignData getMapData(String propertyId) {
    if (customMap != null && customMap.containsKey(propertyId)) {
      return customMap.get(propertyId);
    }
    return null;
  }

}
