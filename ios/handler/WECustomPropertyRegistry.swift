//
//  CustomCallbackHandler.swift
//  react-native-webengage-personalization
//
//

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
                                    propertyID: (existingData?.propertyID ?? 0) as! Int,
                                    campaignData: map[WEConstants.PAYLOAD_WEGDATA] as! WECampaignData)

        registryMap[id] = weghinline
        print("CustomPH: updateRegisterData \(registryMap)")
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

public class CustomCallbackHandler:WEPlaceholderCallback{
    static let shared = CustomCallbackHandler()

    public func onRendered(data: WECampaignData) {
        print("customPH: onRendered \(data.targetViewTag)")
        let _: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? ""]

    }
    public func onDataReceived(_ data: WECampaignData) {
        print("WEP:customPH: onCustomDataReceived \(data.targetViewTag)")


        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId ?? "", "payloadData": data.toJSONString() ?? "", "trackImpression": "WEPersonalizationBridge.trackImpression","trackClick": "WEPersonalizationBridge.trackClick" ]
        let customData: [String: Any] = [
            WEConstants.PAYLOAD_IOS_PROPERTY_ID: data.targetViewTag,
            WEConstants.PAYLOAD_WEGDATA: data,
        ]
        WECustomPropertyRegistry.instance.updateRegisterData(map: customData)

        WEPersonalizationBridge.emitter.sendEvent(withName: "onCustomDataReceived", body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("customPH: onCustomPlaceholderException \(targetViewId)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception.localizedDescription]
        WEPersonalizationBridge.emitter.sendEvent(withName: "onCustomPlaceholderException", body: campaignData)
    }
}

