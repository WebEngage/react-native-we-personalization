import UIKit
import React
import WEPersonalization

@objc(PersonalizationBridge)
class PersonalizationBridge: RCTEventEmitter {

  public static var emitter: RCTEventEmitter!
    var doesUserHandleCallbacks = false;
    var propertyId = 0;


  override init() {
    super.init()
    PersonalizationBridge.emitter = self
      print("Inside PersonalizationBridge")
      WEPersonalization.shared.initialise()
      UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
      WEPersonalization.shared.registerPropertyRegistryCallbacks(CallbackHandler.shared)

  }
// campaigns
    @objc func registerCampaignCallback(_ doesUserHandleCallback: Bool) {
        print("WET: registerCampaignCallback called - \(doesUserHandleCallback)")
        WEPersonalization.shared.registerWECampaignCallback(CallbackHandler.shared)
        self.doesUserHandleCallbacks = doesUserHandleCallback
    }
    
//    unRegisterForCampaigns
    @objc func unRegisterForCampaigns() -> Void {
        print("WET: unRegisterForCampaigns called")
        WEPersonalization.shared.unregisterWECampaignCallback(CallbackHandler.shared)
//        self.doesUserHandelCallbacks = doesUserHandelCallback
    }
    
    // Custom place holders
    @objc func registerCallback(_ propertyId: Int) {
        print("WET: registerCallback called - \(propertyId)")
//        WEPersonalization.shared.registerWlaceholderCallback(propertyId, CallbackHandler.shared as! WEPlaceEPholderCallback)
        WEPersonalization.shared.registerWEPlaceholderCallback(propertyId, CustomCallbackHandler.shared)
//        TODO = create another callbackHandler for onCustomDataReceived / customException
        self.propertyId = propertyId
    }
    
    @objc func unRegisterCallback(_ propertyId: Int) {
        print("WET: unRegisterCallback called - \(propertyId)")
        WEPersonalization.shared.unregisterWEPlaceholderCallback(propertyId)
    }
    

  open override func supportedEvents() -> [String] {
    ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk", "onCampaignPrepared", "onCampaignClicked", "onCampaignException", "onCampaignShown", "onCustomDataReceived", "onCustomPlaceholderException"]
  }
}

class CallbackHandler:WECampaignCallback{
    static let shared = CallbackHandler()

    func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
        print("WEP CC: onCampaignPrepared for \(data.targetViewTag)")
//        let campaignData: [String: Any] = generateParams(data: data)
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignPrepared", body: campaignData)
        return data
    }

    func onCampaignShown(data: WEGCampaignData) {
        print("WEP CC: onCampaignShown for \(data.targetViewTag)")
//        let campaignData: [String: Any] = generateParams(data: data)
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignShown", body: campaignData)
    }

    func onCampaignException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        print("WEP CC: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
//        let campaignData: [String: Any] = generateParams(campaignId: campaignId!, targetViewId, exception)
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignException", body: campaignData)
    }

    func onCampaignClicked(actionId: String, deepLink: String, data: WEGCampaignData) -> Bool {
        print("WEP CC: onCampaignClicked for \(data.targetViewTag)")
        let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink ?? "", "payloadData": data.toJSONString()]
//        let campaignData: [String: Any] = generateParams(actionId: actionId, deepLink: deepLink, data: data)
        
        PersonalizationBridge.emitter.sendEvent(withName: "onCampaignClicked", body: campaignData)
//         TODO - Access value of doesUserHandelCallbacks here
        return false
    }
}

//campaignShown and prepared
//func generateParams(data: WEGCampaignData) -> [String: Any] {
//    let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId, "payloadData": data.toJSONString()]
//    return campaignData;
//}
//
////Exception
//func generateParams(campaignId: String?, _ targetViewId: String, _ exception: Error) -> [String: Any] {
//    let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId, "exception": exception]
//    return campaignData;
//}
//
//// clicked
//func generateParams(actionId: String, deepLink: String, data: WEGCampaignData) -> [String: Any] {
//    let campaignData: [String: Any] = ["actionId": actionId, "deepLink": deepLink, "payloadData": data.toJSONString()]
//    return campaignData;
//}




extension CallbackHandler:PropertyRegistryCallback{
    func onPropertyCacheCleared(for screenDetails: [AnyHashable : Any]) {
        NotificationCenter.default.post(name: Notification.Name("screenNavigated"), object: nil)

    }
}

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



