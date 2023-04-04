//
//  CampaignCampaignCallbackHandler.swift
//  react-native-webengage-personalization
//
//  Created by Akshaykumar Chilad on 17/02/23.
//

import Foundation
import WEPersonalization
class CampaignCallbackHandler:WECampaignCallback{
    static let shared = CampaignCallbackHandler()
    var autoHandleClick = true
    var latestScreenName = ""
    
    func getLatestScreenName() -> String {
        return self.latestScreenName
    }
    func onCampaignPrepared(_ data: WECampaignData) -> WECampaignData {
        print(WEGConstants.TAG+" WEP: onCampaignPrepared for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignPrepared", body: campaignData)
        return data
    }
    
    func onCampaignShown(data: WECampaignData) {
        print(WEGConstants.TAG+" WEP: onCampaignShown for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignShown", body: campaignData)
    }
    
    func onCampaignException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        print(WEGConstants.TAG+" WEP: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignException", body: campaignData)
    }
    
    func onCampaignClicked(actionId: String, deepLink: String, data: WECampaignData) -> Bool {
        print(WEGConstants.TAG+" WEP: onCampaignClicked for \(data.targetViewTag) autoHandle \(self.autoHandleClick)")
        let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink ?? "", "payloadData": data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignClicked", body: campaignData)
        return self.autoHandleClick
    }
}



extension CampaignCallbackHandler:PropertyRegistryCallback{
    func onPropertyCacheCleared(for screenDetails: [AnyHashable : Any]) {
        print(WEGConstants.TAG+"WEP: screen changed!!!")
        let message = "From CampaignCallbackHandler!"
        self.latestScreenName = screenDetails["screen_name"] as! String
        
        if let screenName = screenDetails["screen_name"] as? String{
            print(WEGConstants.TAG+" WEP: screen changed to \(screenName)")
            NotificationCenter.default.post(name: Notification.Name(WEGConstants.SCREEN_NAVIGATED), object: nil, userInfo: ["screenName": screenName]
            )
        }
        
    }
}
