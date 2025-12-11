package com.webengage.we_personalization_rn.registry;

import com.webengage.personalization.data.WECampaignData;
import com.webengage.sdk.android.Logger;
import com.webengage.we_personalization_rn.utils.WEConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WECustomPropertyRegistry {
  private static volatile WECustomPropertyRegistry instance;
  private static final Object lock = new Object();
  private final Map<String, WECampaignData> customMap = new ConcurrentHashMap<>();

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
    if (propertyId == null) {
      Logger.d(WEConstants.TAG, "WECustomPropertyRegistry: registerProperty - propertyId is null");
      return;
    }
    customMap.put(propertyId, null);
  }

  public void registerProperty(String propertyId, WECampaignData weCampaignData) {
    if (propertyId == null) {
      Logger.d(WEConstants.TAG, "WECustomPropertyRegistry: registerProperty - propertyId is null");
      return;
    }
    customMap.put(propertyId, weCampaignData);
  }

  public WECampaignData getMapData(String propertyId) {
    if (propertyId == null) {
      Logger.d(WEConstants.TAG, "WECustomPropertyRegistry: getMapData - propertyId is null");
      return null;
    }
    return customMap.get(propertyId);
  }

}
