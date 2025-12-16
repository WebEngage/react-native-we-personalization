import UIKit
import React
import WEPersonalization
import WebEngage

@objc(WEPersonalizationBridgeImpl)
@objcMembers
public class WEPersonalizationBridgeImpl: NSObject {
    
    @objc public static let shared = WEPersonalizationBridgeImpl()
    
    @objc public static var emitter: RCTEventEmitter?
    @objc public var propertyId = 0
    @objc public var weCampaignData: WECampaignData? = nil
    @objc public var customPropertiesList = [Int]()
    @objc public var WEGPEPluginVersion = "1.1.2"
    
    private var listenerCount: Int = 0
    private let listenerQueue = DispatchQueue(label: "com.webengage.personalization.listener", attributes: .concurrent)
    
    @objc public func setEmitter(_ emitter: RCTEventEmitter?) {
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
      print("\(WEConstants.TAG) initWePersonalization called");
      
        // Implementation if needed
    }
    
    @objc public func registerWECampaignCallback() {
        WELogger.d(WEConstants.TAG+" registerWECampaignCallback called")
        WEPersonalization.shared.registerWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc public func deregisterWECampaignCallback() {
        WELogger.d(WEConstants.TAG+" deregisterWECampaignCallback called")
        WEPersonalization.shared.unregisterWECampaignCallback(WECampaignCallbackHandler.shared)
    }
    
    @objc public func registerProperty(_ propertyId: Int, screenName: String) {
        guard !screenName.isEmpty else {
            WELogger.d(WEConstants.TAG+" registerProperty: invalid screenName")
            return
        }
        
        WELogger.d(WEConstants.TAG+" registerProperty: property=\(propertyId), screen=\(screenName)")
        self.propertyId = propertyId
        let data: [String: Any] = [
            WEConstants.PAYLOAD_ID: propertyId,
            WEConstants.PAYLOAD_SCREEN_NAME: screenName,
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: propertyId
        ]
        WECustomPropertyRegistry.instance.registerData(map: data)
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared)
    }
    
    @objc public func deregisterProperty(_ propertyId: Int) {
        WELogger.d(WEConstants.TAG+" deregisterProperty: property=\(propertyId)")
        WECustomPropertyRegistry.instance.removeRegisterData(id: propertyId)
        WEPersonalization.shared.unregisterWEPlaceholderCallback(propertyId)
    }
    
    @objc public func trackClick(_ propertyId: Int, attributes: [String: Any]?) {
        guard let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: propertyId),
              let campaignData = weginline.campaignData else {
            WELogger.d(WEConstants.TAG+" trackClick: no campaign data for property=\(propertyId)")
            return
        }
        campaignData.trackClick(attributes: attributes)
    }
    
    @objc public func trackImpression(_ propertyId: Int, attributes: [String: Any]?) {
        guard let weginline = WECustomPropertyRegistry.instance.getWEGHinline(targetViewTag: propertyId),
              let campaignData = weginline.campaignData else {
            WELogger.d(WEConstants.TAG+" trackImpression: no campaign data for property=\(propertyId)")
            return
        }
        campaignData.trackImpression(attributes: attributes)
    }
    
    @objc public func addListener(_ eventType: String) {
        listenerQueue.async(flags: .barrier) {
            self.listenerCount += 1
        }
    }
    
    @objc public func removeListeners(_ count: Double) {
        listenerQueue.async(flags: .barrier) {
            self.listenerCount -= Int(count)
            if self.listenerCount < 0 {
                self.listenerCount = 0
            }
        }
    }
    
    @objc public func hasListeners() -> Bool {
        return listenerQueue.sync {
            return self.listenerCount > 0
        }
    }
}
