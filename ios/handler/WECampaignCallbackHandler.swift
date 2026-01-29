import Foundation
import WEPersonalization

class WECampaignCallbackHandler: WECampaignCallback {
    static let shared = WECampaignCallbackHandler()
    
    private var latestScreenName = ""
    private let screenNameQueue = DispatchQueue(label: "com.webengage.personalization.screenname", attributes: .concurrent)
    
    private init() {}
    
    func getLatestScreenName() -> String {
        return screenNameQueue.sync {
            return self.latestScreenName
        }
    }
    
    func onCampaignPrepared(_ data: WECampaignData) -> WECampaignData {
        WELogger.d(WEConstants.TAG+" onCampaignPrepared: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = [
            "targetViewId": data.targetViewTag,
            "campaignId": data.campaignId ?? "",
            "payloadData": data.toJSONString() ?? ""
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCampaignPrepared", body: campaignData)
        return data
    }
    
    func onCampaignShown(data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onCampaignShown: property=\(data.targetViewTag), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = [
            "targetViewId": data.targetViewTag,
            "campaignId": data.campaignId ?? "",
            "payloadData": data.toJSONString() ?? ""
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCampaignShown", body: campaignData)
    }
    
    func onCampaignException(_ campaignId: String?, _ targetViewId: String, _ exception: any Error) {
        WELogger.d(WEConstants.TAG+" onCampaignException: property=\(targetViewId), campaign=\(campaignId ?? "nil"), error=\(exception.localizedDescription)")
        let campaignData: [String: Any] = [
            "targetViewId": targetViewId,
            "campaignId": campaignId ?? "",
            "exception": exception.localizedDescription
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCampaignException", body: campaignData)
    }
    
    func onCampaignClicked(actionId: String, deepLink: String, data: WECampaignData) -> Bool {
        WELogger.d(WEConstants.TAG+" onCampaignClicked: property=\(data.targetViewTag), actionId=\(actionId)")
        let campaignData: [String: Any] = [
            "actionId": actionId,
            "deepLink": deepLink,
            "payloadData": data.toJSONString() ?? ""
        ]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: "onCampaignClicked", body: campaignData)
        return true
    }
}



extension WECampaignCallbackHandler: PropertyRegistryCallback {
    func onPropertyCacheCleared(for screenDetails: [AnyHashable: Any]) {
        guard let screenName = screenDetails["screen_name"] as? String else {
            WELogger.d(WEConstants.TAG+" onPropertyCacheCleared: invalid screen_name")
            return
        }
        
        screenNameQueue.async(flags: .barrier) {
            self.latestScreenName = screenName
        }
        
        WELogger.d(WEConstants.TAG+" onPropertyCacheCleared: screen changed to \(screenName)")
        NotificationCenter.default.post(
            name: Notification.Name(WEConstants.SCREEN_NAVIGATED),
            object: nil,
            userInfo: ["screenName": screenName]
        )
    }
}
