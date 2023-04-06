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
        WEPersonalizationBridge.emitter = self
        print(WEGConstants.TAG+" WEP: WEPersonalizationBridge Initialization")
        WEPersonalization.shared.initialise()
        UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
        WEPersonalization.shared.registerPropertyRegistryCallbacks(CampaignCallbackHandler.shared)
        
    }
    @objc func registerCampaignCallback() {
        print(WEGConstants.TAG+" WEP: registerCampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(CampaignCallbackHandler.shared)
    }
    
    @objc func unRegisterCampaignCallback() -> Void {
        print(WEGConstants.TAG+" WEP: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(CampaignCallbackHandler.shared)
    }
    
    @objc func userWillHandleDeepLink(_ doesUserHandleCallback: Bool) {
        print(WEGConstants.TAG+" WEP: userWillHandleDeepLink called - \(doesUserHandleCallback)")
        CampaignCallbackHandler.shared.autoHandleClick = doesUserHandleCallback
    }
    
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
        print(WEGConstants.TAG+" WEP: trackClick called")
        let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
        weginline?.campaignData?.trackClick(attributes: attributes)
    }
    
    @objc func trackImpression(_ propertyId: Int, attributes: [String: Any]) -> Void {
        print(WEGConstants.TAG+" WEP: trackImpression called")
        let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
        weginline?.campaignData?.trackImpression(attributes: attributes)
    }
    
    
    
    open override func supportedEvents() -> [String] {
        ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
    }
}
