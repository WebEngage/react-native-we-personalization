import Foundation
import WEPersonalization

public class WECustomPropertyRegistry: WEPlaceholderCallback {
    private init(){}
    
    static let instance = WECustomPropertyRegistry()
    
    public var registryMap = [Int: WEProperty]()
    internal var impressionTrackedForTargetviews:[String] = []
    public func registerData(map:[String:Any]){
        let id = map[WEConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int
        let weghinline = WEProperty(id: id,
                                    screenName: map[WEConstants.PAYLOAD_SCREEN_NAME] as! String,
                                    propertyID: map[WEConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int)
        
        registryMap[id] = weghinline
    }
    
    public func updateRegisterData(map:[String:Any]){
        let id = map[WEConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int
        let existingData = registryMap[id]
        let weghinline = WEProperty(id: existingData?.id ?? -1,
                                    screenName: (existingData?.screenName ?? "") as String,
                                    propertyID: (existingData?.propertyID ?? 0) ,
                                    campaignData: map[WEConstants.PAYLOAD_WEGDATA] as? WECampaignData)
        
        registryMap[id] = weghinline
        WELogger.d(WEConstants.TAG+" updateRegisterData: property=\(id)")
    }
    
    public func removeRegisterData(id:Int) {
        registryMap.removeValue(forKey: id)
    }
    
    
    public func getWEGHinline(targetViewTag:Int)->WEProperty?{
        for(_,weginline) in registryMap{
            if(weginline.propertyID == targetViewTag){
                return weginline
            }
        }
        return nil
    }
    
    
}

public class CustomCallbackHandler: WEPlaceholderCallback {
    static let shared = CustomCallbackHandler()
    
    public func onRendered(_ data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onRendered: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? ""]
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: "onRendered", body: campaignData)
    }
    
    public func onDataReceived(_ data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onCustomDataReceived: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? "", "trackImpression": "WEPersonalizationBridgeImpl.trackImpression","trackClick": "WEPersonalizationBridgeImpl.trackClick" ]
        let customData: [String: Any] = [
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: data.targetViewTag,
            WEConstants.PAYLOAD_WEGDATA: data,
        ]
        WECustomPropertyRegistry.instance.updateRegisterData(map: customData)
        
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: "onCustomDataReceived", body: campaignData)
    }
    
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        WELogger.d(WEConstants.TAG+" onCustomPlaceholderException: property=\(targetViewId), campaign=\(campaignId ?? "nil"), error=\(exception.localizedDescription)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception.localizedDescription]
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: "onCustomPlaceholderException", body: campaignData)
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

