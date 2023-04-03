import UIKit
import React
import WEPersonalization
import WebEngage

@objc(PersonalizationBridge)
class PersonalizationBridge: RCTEventEmitter {

  public static var emitter: RCTEventEmitter!
    var propertyId = 0;
    var weCampaignData: WECampaignData? = nil

    static let shared = PersonalizationBridge()
  override init() {
    super.init()
    PersonalizationBridge.emitter = self
      print(WEGConstants.TAG+" WEP: Inside PersonalizationBridge")
      WEPersonalization.shared.initialise()
      UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
      WEPersonalization.shared.registerPropertyRegistryCallbacks(CampaignCallbackHandler.shared)

  }
// campaigns
    @objc func registerCampaignCallback() {
        print(WEGConstants.TAG+" WEP: WET: registerCampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(CampaignCallbackHandler.shared)
    }

//    unRegisterForCampaigns
    @objc func unRegisterCampaignCallback() -> Void {
        print(WEGConstants.TAG+" WEP: WET: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(CampaignCallbackHandler.shared)
    }

    @objc func userWillHandleDeepLink(_ doesUserHandleCallback: Bool) {
        print(WEGConstants.TAG+" WEP: WET: userWillHandleDeepLink called - \(doesUserHandleCallback)")
        CampaignCallbackHandler.shared.autoHandleClick = doesUserHandleCallback
    }

    // Custom place holders
    @objc func registerCallback(_ propertyId: Int, screenName: String) {
        print(WEGConstants.TAG+" WEP: customPh: registerCallback called - \(propertyId)")
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared.self)
       self.propertyId = propertyId
       let data: [String: Any] = [
           WEGConstants.PAYLOAD_ID: propertyId,
           WEGConstants.PAYLOAD_SCREEN_NAME: screenName,
           WEGConstants.PAYLOAD_IOS_PROPERTY_ID: propertyId
       ]
      customRegistry.instance.registerData(map: data)
    }

    @objc func unRegisterCallback(_ propertyId: Int) {
        print(WEGConstants.TAG+" WEP: customPH: unRegisterCallback called - \(propertyId)")
        WEPersonalization.shared.unregisterWEPlaceholderCallback(propertyId)
    }

    @objc func trackClick(_ propertyId: Int, attributes: [String: Any]) -> Void {
        print(WEGConstants.TAG+" WEP: WET: trackClick called")
       let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
       weginline?.campaignData?.trackClick(attributes: attributes)
    }

    @objc func trackImpression(_ propertyId: Int, attributes: [String: Any]) -> Void {
        print(WEGConstants.TAG+" WEP: WET: trackImpression called")
       let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
       weginline?.campaignData?.trackImpression(attributes: attributes)
    }



  open override func supportedEvents() -> [String] {
    ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
  }
}
