import UIKit
import React
import WEPersonalization

@objc(PersonalizationBridge)
class PersonalizationBridge: RCTEventEmitter {

  public static var emitter: RCTEventEmitter!

  override init() {
    super.init()
    PersonalizationBridge.emitter = self
      print("Inside PersonalizationBridge")
      WEPersonalization.shared.initialise()
      UserDefaults.standard.setValue(false, forKey: WEPersonalization.Constants.KEY_SHOULD_AUTO_TRACK_IMPRESSIONS)
      WEPersonalization.shared.registerWECampaignCallback(CallbackHandler.shared)
            WEPersonalization.shared.registerPropertyRegistryCallbacks(CallbackHandler.shared)
  }

  open override func supportedEvents() -> [String] {
    ["onDataReceived", "onRendered", "onPropertyCacheCleared", "onPlaceholderException", "testAk"]
  }
}

class CallbackHandler:WECampaignCallback{
    static let shared = CallbackHandler()
    
    func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
        print("WEP CC: onCampaignPrepared for \(data.targetViewTag)")
        return data
    }
    
    func onCampaignShown(data: WEGCampaignData) {
        print("WEP CC: onCampaignShown for \(data.targetViewTag)")
    }
    
    func onCampaignException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WEP CC: onCampaignException for \(targetViewId) error: \(exception.localizedDescription)")
    }
    
    func onCampaignClicked(actionId: String, deepLink: String, data: WEGCampaignData) -> Bool {
        print("WEP CC: onCampaignClicked for \(data.targetViewTag)")
        return false
    }
}


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



