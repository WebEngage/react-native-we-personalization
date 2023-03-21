import UIKit
import React
import WEPersonalization

@objc(PersonalizationBridge)
class PersonalizationBridge: RCTEventEmitter {

  public static var emitter: RCTEventEmitter!
    var propertyId = 0;
    var weCampaignData: WEGCampaignData? = nil

    static let shared = PersonalizationBridge()
  override init() {
    super.init()
    PersonalizationBridge.emitter = self
      print(WEGConstants.TAG+" WEP: Inside PersonalizationBridge")
      WEPersonalization.shared.initialise()
      UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
      WEPersonalization.shared.registerPropertyRegistryCallbacks(CampaignCallbackHandler.shared)

  }
// campaigns
    @objc func registerCampaignCallback() {
        print(WEGConstants.TAG+" WEP: WET: registerCampaignCallback called ")
        WEPersonalization.shared.registerWECampaignCallback(CampaignCallbackHandler.shared)
    }

//    unRegisterForCampaigns
    @objc func unRegisterCampaignCallback() -> Void {
        print(WEGConstants.TAG+" WEP: WET: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(CampaignCallbackHandler.shared)
    }

    @objc func userWillHandleDeepLink(_ doesUserHandleCallback: Bool) {
        print(WEGConstants.TAG+" WEP: WET: userWillHandleDeepLink called - \(doesUserHandleCallback)")
        CampaignCallbackHandler.shared.autoHandleClick = doesUserHandleCallback
    }

    // Custom place holders
    @objc func registerCallback(_ propertyId: Int, screenName: String) {
        print(WEGConstants.TAG+" WEP: customPh: registerCallback called - \(propertyId)")
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared.self)
       self.propertyId = propertyId
       let data: [String: Any] = [
           WEGConstants.PAYLOAD_ID: propertyId,
           WEGConstants.PAYLOAD_SCREEN_NAME: screenName,
           WEGConstants.PAYLOAD_IOS_PROPERTY_ID: propertyId
       ]
      customRegistry.instance.registerData(map: data)
    }

    @objc func unRegisterCallback(_ propertyId: Int) {
        print(WEGConstants.TAG+" WEP: customPH: unRegisterCallback called - \(propertyId)")
        WEPersonalization.shared.unregisterWEPlaceholderCallback(propertyId)
    }

    @objc func trackClick(_ propertyId: Int, attributes: [String: Any]) -> Void {
        print(WEGConstants.TAG+" WEP: WET: trackClick called")
       let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
       weginline?.campaignData?.trackClick(attributes: attributes)
    }

    @objc func trackImpression(_ propertyId: Int, attributes: [String: Any]) -> Void {
        print(WEGConstants.TAG+" WEP: WET: trackImpression called")
       let weginline = customRegistry.instance.getWEGHinline(targetViewTag: propertyId)
       weginline?.campaignData?.trackImpression(attributes: attributes)
    }


  open override func supportedEvents() -> [String] {
    ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
  }
}

//class CallbackHandler:WECampaignCallback{
//    static let shared = CallbackHandler()
//
//    func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
//        print(WEGConstants.TAG+" WEP CC: onCampaignPrepared for \(data.targetViewTag)")
////        let campaignData: [String: Any] = generateParams(data: data)
//        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
//        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignPrepared", body: campaignData)
//        return data
//    }
//
//    func onCampaignShown(data: WEGCampaignData) {
//        print(WEGConstants.TAG+" WEP CC: onCampaignShown for \(data.targetViewTag)")
////        let campaignData: [String: Any] = generateParams(data: data)
//        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
//        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignShown", body: campaignData)
//    }
//
//    func onCampaignException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
//        print(WEGConstants.TAG+" WEP CC: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
//        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
////        let campaignData: [String: Any] = generateParams(campaignId: campaignId!, targetViewId, exception)
//        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignException", body: campaignData)
//    }
//
//    func onCampaignClicked(actionId: String, deepLink: String, data: WEGCampaignData) -> Bool {
//        print(WEGConstants.TAG+" WEP CC: onCampaignClicked for \(data.targetViewTag)")
//        let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink ?? "", "payloadData": data.toJSONString()]
////        let campaignData: [String: Any] = generateParams(actionId: actionId, deepLink: deepLink, data: data)
//
//        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignClicked", body: campaignData)
////         TODO - Access value of doesUserHandelCallbacks here
//        return false
//    }
//}
//
//
//
//extension CallbackHandler:PropertyRegistryCallback{
//    func onPropertyCacheCleared(for screenDetails: [AnyHashable : Any]) {
//        NotificationCenter.default.post(name: Notification.Name(WEGConstants.SCREEN_NAVIGATED), object: nil)
//
//    }
//}

//@objc(PersonalizationBridge)
//class PersonalizationBridge: NSObject {
//  // Swift doesn't have synthesize - just define the variable
//  // let bridge: RCTBridge!
//
//
//  @objc(multiply:withB:withResolver:withRejecter:)
//  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//    resolve(a*b)
//  }
//
//  @objc(add:withB:withResolver:withRejecter:)
//  func add(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//    resolve(a+b)
//  }
//
//  // immediateCallback
//  @objc(immediateCallback:withLocation:withCallback:withErrorCallback:)
//  func immediateCallback(title: String,location: String, callback: RCTResponseSenderBlock, ErrorCallback: RCTResponseSenderBlock) -> Void {
//    var eventId: Int = 10
//    // callback("", eventId);
//    callback([NSNull() ,eventId])
//  }
//
//  // promiseCallback
//  @objc(promiseCallback:withLocation:withResolver:withRejecter:)
//  func promiseCallback(title: String, title2: String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
//    var eventId: Int = 10
//    // callback("", eventId);
//    // callback([NSNull() ,eventId])
//    resolve(eventId * 2);
//  }
//
//  // listenerCallback
//    // Swift doesn't have synthesize - just define the variable
//
//    // @objc func listenerCallback( eventName: String ) {
//    //     self.bridge.eventDispatcher.sendAppEventWithName( "EventReminder", body: "Woot!" )
//    // }
//
//}
