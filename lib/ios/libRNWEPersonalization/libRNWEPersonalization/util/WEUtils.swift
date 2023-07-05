
import Foundation
import WEPersonalization

func generateParams(data: WECampaignData) -> [String: Any] {
    let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEConstants.PAYLOAD: data.toJSONString() ?? ""]
    return campaignData;
}

//Exception
func generateParams(campaignId: String?, _ targetViewId: String, _ exception: Error) -> [String: Any] {
    let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: targetViewId, WEConstants.PAYLOAD_CAMPAIGN_ID: campaignId ?? "", WEConstants.EXCEPTION: exception]
    return campaignData;
}

// clicked
func generateParams(actionId: String, deepLink: String, data: WECampaignData) -> [String: Any] {
    let campaignData: [String: Any] = [WEConstants.PAYLOAD_ACTION_ID: actionId, WEConstants.PAYLOAD_DEEPLINK: deepLink, WEConstants.PAYLOAD: data.toJSONString() ?? ""]
    return campaignData;
}
