import UIKit
import React
import WEPersonalization
import WebEngage

#if RCT_NEW_ARCH_ENABLED
import WEPersonalizationSpec
#endif

@objc(WEPersonalizationBridge)
class WEPersonalizationBridge: RCTEventEmitter {
    
#if RCT_NEW_ARCH_ENABLED
    // MARK: - TurboModule conformance
    static func moduleName() -> String! {
        return "WEPersonalizationBridge"
    }
    
    static func requiresMainQueueSetup() -> Bool {
        return false
    }
#endif
    
    public static var emitter: RCTEventEmitter!
    var propertyId = 0;
    var weCampaignData: WECampaignData? = nil
    var customPropertiesList = [Int]()
    var WEGPEPluginVersion = "1.1.1"
    
    
    static let shared = WEPersonalizationBridge()
    override init() {
        super.init()
        WELogger.initLogger()
        initialiseWEGVersion()
        WEPersonalizationBridge.emitter = self
        WEPersonalization.shared.initialise()
        UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
        WEPersonalization.shared.registerPropertyRegistryCallbacks(WECampaignCallbackHandler.shared)
        
    }
    
    func initialiseWEGVersion() {
        let key: WegVersionKey = .RNPE
        WebEngage.sharedInstance().setVersionForChildSDK(WEGPEPluginVersion, for: key)
      }
    
    @objc func initWePersonalization() {
        
    }
    
    @objc func registerWECampaignCallback() {
        WELogger.d(WEConstants.TAG+"WEP: WEPersonalizationBridge: registerWECampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc func deregisterWECampaignCallback() -> Void {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc func registerProperty(_ propertyId: String, screenName: String) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: registerProperty called - \(propertyId)")
        if let intPropertyId = Int(propertyId) {
            self.propertyId = intPropertyId
            let data: [String: Any] = [
                WEConstants.PAYLOAD_ID: intPropertyId,
                WEConstants.PAYLOAD_SCREEN_NAME: screenName,
                WEConstants.PAYLOAD_IOS_PROPERTY_ID: intPropertyId
            ]
            WECustomPropertyRegistry.instance.registerData(map: data)
            WEPersonalization.shared.registerWEPlaceholderCallback(intPropertyId, CustomCallbackHandler.shared.self)
        }
    }
    
    
    @objc func deregisterProperty(_ propertyId: String) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: deregisterProperty called - \(propertyId)")
        if let intPropertyId = Int(propertyId) {
            WECustomPropertyRegistry.instance.removeRegisterData(id: intPropertyId)
            WEPersonalization.shared.unregisterWEPlaceholderCallback(intPropertyId)
        }
    }
    
    @objc func trackClick(_ propertyId: String, attributes: [String: Any]) -> Void {
        if let intPropertyId = Int(propertyId) {
            let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: intPropertyId)
            weginline?.campaignData?.trackClick(attributes: attributes)
        }
    }
    
    @objc func trackImpression(_ propertyId: String, attributes: [String: Any]) -> Void {
        if let intPropertyId = Int(propertyId) {
            let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: intPropertyId)
            weginline?.campaignData?.trackImpression(attributes: attributes)
        }
    }
    
    @objc override func addListener(_ eventType: String) {
        // Handled by React Native
    }
    
    @objc override func removeListeners(_ count: Double) {
        // Handled by React Native
    }
    
    
    
    open override func supportedEvents() -> [String] {
        ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
    }
}
