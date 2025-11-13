import UIKit
import React
import WEPersonalization
import WebEngage

@objc(WEPersonalizationBridgeImpl)
@objcMembers
public class WEPersonalizationBridgeImpl: NSObject {
    
    @objc public static let shared = WEPersonalizationBridgeImpl()
    
    @objc public static var emitter: RCTEventEmitter!
    @objc public var propertyId = 0
    @objc public var weCampaignData: WECampaignData? = nil
    @objc public var customPropertiesList = [Int]()
    @objc public var WEGPEPluginVersion = "1.1.1"
    
    @objc public func setEmitter(_ emitter: RCTEventEmitter) {
        WEPersonalizationBridgeImpl.emitter = emitter
    }
    
    @objc public override init() {
        super.init()
    }
    
    @objc public func initialize() {
        WELogger.initLogger()
        initialiseWEGVersion()
        WEPersonalization.shared.initialise()
        UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
        WEPersonalization.shared.registerPropertyRegistryCallbacks(WECampaignCallbackHandler.shared)
    }
    
    @objc public func initialiseWEGVersion() {
        let key: WegVersionKey = .RNPE
        WebEngage.sharedInstance().setVersionForChildSDK(WEGPEPluginVersion, for: key)
    }
    
    @objc public func supportedEvents() -> [String] {
        return ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
    }
    
    @objc public func initWePersonalization() {
      print("Arch: swift - initWePersonalization called");
      
        // Implementation if needed
    }
    
    @objc public func registerWECampaignCallback() {
        WELogger.d(WEConstants.TAG+"WEP: WEPersonalizationBridge: registerWECampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc public func deregisterWECampaignCallback() {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc public func registerProperty(_ propertyId: String, screenName: String) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: registerProperty called - \(propertyId)")
        if let intPropertyId = Int(propertyId) {
            self.propertyId = intPropertyId
            let data: [String: Any] = [
                WEConstants.PAYLOAD_ID: intPropertyId,
                WEConstants.PAYLOAD_SCREEN_NAME: screenName,
                WEConstants.PAYLOAD_IOS_PROPERTY_ID: intPropertyId
            ]
            WECustomPropertyRegistry.instance.registerData(map: data)
            WEPersonalization.shared.registerWEPlaceholderCallback(intPropertyId, CustomCallbackHandler.shared)
        }
    }
    
    @objc public func deregisterProperty(_ propertyId: String) {
        WELogger.d(WEConstants.TAG+" WEP: WEPersonalizationBridge: deregisterProperty called - \(propertyId)")
        if let intPropertyId = Int(propertyId) {
            WECustomPropertyRegistry.instance.removeRegisterData(id: intPropertyId)
            WEPersonalization.shared.unregisterWEPlaceholderCallback(intPropertyId)
        }
    }
    
    @objc public func trackClick(_ propertyId: String, attributes: [String: Any]) {
        if let intPropertyId = Int(propertyId) {
            let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: intPropertyId)
            weginline?.campaignData?.trackClick(attributes: attributes)
        }
    }
    
    @objc public func trackImpression(_ propertyId: String, attributes: [String: Any]) {
        if let intPropertyId = Int(propertyId) {
            let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: intPropertyId)
            weginline?.campaignData?.trackImpression(attributes: attributes)
        }
    }
    
    @objc public func addListener(_ eventType: String) {
        // Handled by React Native
    }
    
    @objc public func removeListeners(_ count: Double) {
        // Handled by React Native
    }
}
