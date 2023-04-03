//
//  CustomCallbackHandler.swift
//  react-native-webengage-personalization
//
//  Created by Akshaykumar Chilad on 16/02/23.
//

import Foundation
import WEPersonalization

public class customRegistry: WEPlaceholderCallback {
    private init(){}

        static let instance = customRegistry()

        public var registryMap = [Int: WEGHInline]()
        internal var impressionTrackedForTargetviews:[String] = []
    public func registerData(map:[String:Any]){
            let id = map[WEGConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int
            let weghinline = WEGHInline(id: id,
                screenName: map[WEGConstants.PAYLOAD_SCREEN_NAME] as! String,
                propertyID: map[WEGConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int)

            registryMap[id] = weghinline
        }

    public func updateRegisterData(map:[String:Any]){
        let id = map[WEGConstants.PAYLOAD_IOS_PROPERTY_ID] as! Int
        let existingData = registryMap[id]
        print("Existing data \(existingData) ")
        let weghinline = WEGHInline(id: existingData?.id ?? -1,
                                    screenName: (existingData?.screenName ?? "") as String,
                                    propertyID: (existingData?.propertyID ?? 0) as! Int,
                                    campaignData: map[WEGConstants.PAYLOAD_WEGDATA] as! WECampaignData)

        registryMap[id] = weghinline
        print("CustomPH: updateRegisterData \(registryMap)")
    }

    public func getWEGHinline(targetViewTag:Int)->WEGHInline?{
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
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]

    }
    public func onDataReceived(_ data: WECampaignData) {
        print("WEP:customPH: onCustomDataReceived \(data.targetViewTag)")


        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString(), "trackImpression": "PersonalizationBridge.trackImpression","trackClick": "PersonalizationBridge.trackClick" ]
        let customData: [String: Any] = [
            WEGConstants.PAYLOAD_IOS_PROPERTY_ID: data.targetViewTag,
            WEGConstants.PAYLOAD_WEGDATA: data,
        ]
        customRegistry.instance.updateRegisterData(map: customData)

        PersonalizationBridge.emitter.sendEvent(withName: "onCustomDataReceived", body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WEP: customPH: onCustomPlaceholderException \(targetViewId)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception.localizedDescription]
        PersonalizationBridge.emitter.sendEvent(withName: "onCustomPlaceholderException", body: campaignData)
    }
}

extension PersonalizationBridge: WEPlaceholderCallback {
    func onDataReceived(_ data: WECampaignData) {
        print("onDataReceived ###")
    }
    func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("onPlaceholderException ###")
    }

}
