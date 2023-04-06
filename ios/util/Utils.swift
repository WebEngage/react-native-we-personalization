
import Foundation
import WEPersonalization

func generateParams(data: WECampaignData) -> [String: Any] {
    let campaignData: [String: Any] = [WEGConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEGConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId, WEGConstants.PAYLOAD: data.toJSONString()]
    return campaignData;
}

//Exception
func generateParams(campaignId: String?, _ targetViewId: String, _ exception: Error) -> [String: Any] {
    let campaignData: [String: Any] = [WEGConstants.PAYLOAD_TARGET_VIEW_ID: targetViewId, WEGConstants.PAYLOAD_CAMPAIGN_ID: campaignId, WEGConstants.EXCEPTION: exception]
    return campaignData;
}

// clicked
func generateParams(actionId: String, deepLink: String, data: WECampaignData) -> [String: Any] {
    let campaignData: [String: Any] = [WEGConstants.PAYLOAD_ACTION_ID: actionId, WEGConstants.PAYLOAD_DEEPLINK: deepLink, WEGConstants.PAYLOAD: data.toJSONString()]
    return campaignData;
}