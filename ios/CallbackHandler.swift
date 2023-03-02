////
////  CallbackHandler.swift
////  CocoaAsyncSocket
////
////  Created by Akshaykumar Chilad on 10/02/23.
////
//
//import Foundation
//import WEPersonalization
//
//class CallbackHandler : WECampaignCallback{
//    static let shared = CallbackHandler()
//    func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
//        print("WEP CC: onCampaignPrepared for \(data.targetViewTag)")
//        return data
//    }
//    
//    func onCampaignShown(data: WEGCampaignData) {
//        print("WEP CC: onCampaignShown for \(data.targetViewTag)")
//    }
//    
//    func onCampaignException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
//        print("WEP CC: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
//    }
//    
//    func onCampaignClicked(actionId: String, deepLink: String, data: WEGCampaignData) -> Bool {
//        print("WEP CC: onCampaignClicked for \(data.targetViewTag)")
//        return false
//    }
//}
//
//
//extension CallbackHandler:PropertyRegistryCallback{
//    func onPropertyCacheCleared(for screenDetails: [AnyHashable : Any]) {
//        NotificationCenter.default.post(name: Notification.Name("screenNavigated"), object: nil)
//        
//    }
//}
//
