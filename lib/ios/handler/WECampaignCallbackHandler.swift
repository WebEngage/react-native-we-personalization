import Foundation
import WEPersonalization


class WECampaignCallbackHandler:WECampaignCallback{
    static let shared = WECampaignCallbackHandler()
    var latestScreenName = ""
    func getLatestScreenName() -> String {
        return self.latestScreenName
    }
    
    func onCampaignPrepared(_ data: WECampaignData) -> WECampaignData {
        WELogger.d(WEConstants.TAG+" WEP: WECampaignCallbackHandler: onCampaignPrepared for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? ""]
        WEPersonalizationBridge.emitter.sendEvent(withName: "onCampaignPrepared", body: campaignData)
        return data
    }
    
    func onCampaignShown(data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" WEP: WECampaignCallbackHandler: onCampaignShown for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? ""]
        WEPersonalizationBridge.emitter.sendEvent(withName: "onCampaignShown", body: campaignData)
    }
    
    private func onCampaignException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        WELogger.d(WEConstants.TAG+" WEP: WECampaignCallbackHandler: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
        WEPersonalizationBridge.emitter.sendEvent(withName: "onCampaignException", body: campaignData)
    }
    
    func onCampaignClicked(actionId: String, deepLink: String, data: WECampaignData) -> Bool {
        WELogger.d(WEConstants.TAG+" WEP: WECampaignCallbackHandler: onCampaignClicked for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink , "payloadData": data.toJSONString() ?? ""]
        WEPersonalizationBridge.emitter.sendEvent(withName: "onCampaignClicked", body: campaignData)
        return true
    }
}



extension WECampaignCallbackHandler:PropertyRegistryCallback{
    func onPropertyCacheCleared(for screenDetails: [AnyHashable : Any]) {
        WELogger.d(WEConstants.TAG+"WEP: screen changed!!!")
        self.latestScreenName = screenDetails["screen_name"] as! String
        if let screenName = screenDetails["screen_name"] as? String{
            WELogger.d(WEConstants.TAG+" WEP: screen changed to \(screenName)")
            NotificationCenter.default.post(name: Notification.Name(WEConstants.SCREEN_NAVIGATED), object: nil, userInfo: ["screenName": screenName]
            )
        }
    }
}
