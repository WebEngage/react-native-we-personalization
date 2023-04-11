import UIKit
import React
import WEPersonalization
import WebEngage

@objc(WEPersonalizationBridge)
class WEPersonalizationBridge: RCTEventEmitter {
    
    public static var emitter: RCTEventEmitter!
    var propertyId = 0;
    var weCampaignData: WECampaignData? = nil
    var customPropertiesList = [Int]()
    
    
    static let shared = WEPersonalizationBridge()
    override init() {
        super.init()
        WELogger.initLogger()
        WEPersonalizationBridge.emitter = self
        WEPersonalization.shared.initialise()
        UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
        WEPersonalization.shared.registerPropertyRegistryCallbacks(WECampaignCallbackHandler.shared)
        
    }
    
    @objc func registerWECampaignCallback() {
        WELogger.d(WEConstants.TAG+"WEP: WEPersonalizationBridge: registerWECampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc func deregisterWECampaignCallback() -> Void {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc func registerProperty(_ propertyId: Int, screenName: String) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: registerProperty called - \(propertyId)")
        self.propertyId = propertyId
        let data: [String: Any] = [
            WEConstants.PAYLOAD_ID: propertyId,
            WEConstants.PAYLOAD_SCREEN_NAME: screenName,
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: propertyId
        ]
        WECustomPropertyRegistry.instance.registerData(map: data)
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared.self)
    }
    
    
    @objc func deregisterProperty(_ propertyId: Int) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: deregisterProperty called - \(propertyId)")
        let id = propertyId as! Int
        WECustomPropertyRegistry.instance.removeRegisterData(id: id)
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
