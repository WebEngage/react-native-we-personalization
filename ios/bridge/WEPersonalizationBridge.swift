import UIKit
import React
import WEPersonalization
import WebEngage

@objc(WEPersonalizationBridge)
class WEPersonalizationBridge: RCTEventEmitter {

    public static var emitter: RCTEventEmitter!
    var propertyId = 0;
    var weCampaignData: WECampaignData? = nil

    static let shared = WEPersonalizationBridge()
    override init() {
        super.init()
        WELogger.initLogger()
        WEPersonalizationBridge.emitter = self
        print(WEConstants.TAG+" WEP: WEPersonalizationBridge Initialization")
        WEPersonalization.shared.initialise()
        UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
        WEPersonalization.shared.registerPropertyRegistryCallbacks(WECampaignCallbackHandler.shared)

    }

    @objc func registerWECampaignCallback() {
        print(WEConstants.TAG+" WEP: registerWECampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(WECampaignCallbackHandler.shared)
    }

    @objc func deregisterWECampaignCallback() -> Void {
        print(WEConstants.TAG+" WEP: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(WECampaignCallbackHandler.shared)
    }


    @objc func registerProperty(_ propertyId: Int, screenName: String) {
        print(WEConstants.TAG+" WEP: customPh: registerProperty called - \(propertyId)")
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared.self)
        self.propertyId = propertyId
        let data: [String: Any] = [
            WEConstants.PAYLOAD_ID: propertyId,
            WEConstants.PAYLOAD_SCREEN_NAME: screenName,
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: propertyId
        ]
        WECustomPropertyRegistry.instance.registerData(map: data)
    }

    @objc func deregisterProperty(_ propertyId: Int) {
        WELogger.d(WEConstants.TAG+" WEP: customPH: deregisterProperty called - \(propertyId)")
        WEPersonalization.shared.unregisterWEPlaceholderCallback(propertyId)
    }

    @objc func trackClick(_ propertyId: Int, attributes: [String: Any]) -> Void {
        let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: propertyId)
        weginline?.campaignData?.trackClick(attributes: attributes)
    }

    @objc func trackImpression(_ propertyId: Int, attributes: [String: Any]) -> Void {
        let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: propertyId)
        weginline?.campaignData?.trackImpression(attributes: attributes)
    }



    open override func supportedEvents() -> [String] {
        ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
    }
}
