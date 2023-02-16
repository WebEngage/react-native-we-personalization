//
//  Utils.swift
//  react-native-webengage-personalization
//
//  Created by Akshaykumar Chilad on 16/02/23.
//

import Foundation
import WEPersonalization

func generateParams(data: WEGCampaignData) -> [String: Any] {
    let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
    return campaignData;
}

//Exception
func generateParams(campaignId: String?, _ targetViewId: String, _ exception: Error) -> [String: Any] {
    let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId, "exception": exception]
    return campaignData;
}

// clicked
func generateParams(actionId: String, deepLink: String, data: WEGCampaignData) -> [String: Any] {
    let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink, "payloadData": data.toJSONString()]
    return campaignData;
}
