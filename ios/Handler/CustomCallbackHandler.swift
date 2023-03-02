//
//  CustomCallbackHandler.swift
//  react-native-webengage-personalization
//
//  Created by Akshaykumar Chilad on 16/02/23.
//

import Foundation
import WEPersonalization

public class CustomCallbackHandler:WEPlaceholderCallback{
    static let shared = CustomCallbackHandler()
    
    public func onRendered(data: WEGCampaignData) {
        print("customPH: onRendered \(data.targetViewTag)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
        print("customPH: Calling onRendered for -> \(data.targetViewTag)")
    }
    public func onDataReceived(_ data: WEGCampaignData) {
//        let campaignData = data;
        print("customPH: onCustomDataReceived \(data.targetViewTag)")

        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]

        PersonalizationBridge.emitter.sendEvent(withName: "onCustomDataReceived", body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        print("customPH: onCustomPlaceholderException \(targetViewId)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
        PersonalizationBridge.emitter.sendEvent(withName: "onCustomPlaceholderException", body: campaignData)
    }
}
