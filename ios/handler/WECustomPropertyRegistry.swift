import Foundation
import WEPersonalization

public class WECustomPropertyRegistry: WEPlaceholderCallback {
    private init() {}
    
    static let instance = WECustomPropertyRegistry()
    
    private var registryMap = [Int: WEProperty]()
    private let registryQueue = DispatchQueue(label: "com.webengage.personalization.registry", attributes: .concurrent)
    internal var impressionTrackedForTargetviews: [String] = []
    public func registerData(map: [String: Any]) {
        guard let id = map[WEConstants.PAYLOAD_IOS_PROPERTY_ID] as? Int,
              let screenName = map[WEConstants.PAYLOAD_SCREEN_NAME] as? String else {
            WELogger.d(WEConstants.TAG+" registerData: invalid parameters")
            return
        }
        
        let weghinline = WEProperty(
            id: id,
            screenName: screenName,
            propertyID: id
        )
        
        registryQueue.async(flags: .barrier) {
            self.registryMap[id] = weghinline
        }
    }
    
    public func updateRegisterData(map: [String: Any]) {
        guard let id = map[WEConstants.PAYLOAD_IOS_PROPERTY_ID] as? Int else {
            WELogger.d(WEConstants.TAG+" updateRegisterData: invalid property id")
            return
        }
        
        registryQueue.async(flags: .barrier) {
            let existingData = self.registryMap[id]
            let weghinline = WEProperty(
                id: existingData?.id ?? -1,
                screenName: existingData?.screenName ?? "",
                propertyID: existingData?.propertyID ?? 0,
                campaignData: map[WEConstants.PAYLOAD_WEGDATA] as? WECampaignData
            )
            
            self.registryMap[id] = weghinline
            WELogger.d(WEConstants.TAG+" updateRegisterData: property=\(id)")
        }
    }
    
    public func removeRegisterData(id: Int) {
        registryQueue.async(flags: .barrier) {
            self.registryMap.removeValue(forKey: id)
        }
    }
    
    
    public func getWEGHinline(targetViewTag: Int) -> WEProperty? {
        return registryQueue.sync {
            return self.registryMap[targetViewTag]
        }
    }
    
    
}

public class CustomCallbackHandler: WEPlaceholderCallback {
    static let shared = CustomCallbackHandler()
    
    private init() {}
    
    public func onRendered(_ data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onRendered: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = [
            "targetViewId": data.targetViewTag,
            "campaignId": data.campaignId ?? "",
            "payloadData": data.toJSONString() ?? ""
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onRendered", body: campaignData)
    }
    
    public func onDataReceived(_ data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onCustomDataReceived: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        
        let campaignData: [String: Any] = [
            "targetViewId": data.targetViewTag,
            "campaignId": data.campaignId ?? "",
            "payloadData": data.toJSONString() ?? "",
            "trackImpression": "WEPersonalizationBridgeImpl.trackImpression",
            "trackClick": "WEPersonalizationBridgeImpl.trackClick"
        ]
        let customData: [String: Any] = [
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: data.targetViewTag,
            WEConstants.PAYLOAD_WEGDATA: data
        ]
        WECustomPropertyRegistry.instance.updateRegisterData(map: customData)
        
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCustomDataReceived", body: campaignData)
    }
    
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        WELogger.d(WEConstants.TAG+" onCustomPlaceholderException: property=\(targetViewId), campaign=\(campaignId ?? "nil"), error=\(exception.localizedDescription)")
        let campaignData: [String: Any] = [
            "targetViewId": targetViewId,
            "campaignId": campaignId ?? "",
            "exception": exception.localizedDescription
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCustomPlaceholderException", body: campaignData)
    }
}

extension CustomCallbackHandler: WECampaignControlInternalCallback {
    public func onCampaignPrepared(_ data: WECampaignData) -> WECampaignData {
        return data
    }
    
    public func onCampaignShown(data: WECampaignData) {
        // Handle if needed
    }
    
    public func onCampaignClicked(actionId: String, deepLink: String, data: WECampaignData) -> Bool {
        return true
    }
}

