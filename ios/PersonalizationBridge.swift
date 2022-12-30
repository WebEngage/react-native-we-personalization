import UIKit

@objc(PersonalizationBridge)
class PersonalizationBridge: NSObject {
  // Swift doesn't have synthesize - just define the variable
  // let bridge: RCTBridge!


  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

  @objc(add:withB:withResolver:withRejecter:)
  func add(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a+b)
  }

  // immediateCallback
  @objc(immediateCallback:withLocation:withCallback:withErrorCallback:)
  func immediateCallback(title: String,location: String, callback: RCTResponseSenderBlock, ErrorCallback: RCTResponseSenderBlock) -> Void {
    var eventId: Int = 10
    // callback("", eventId);
    callback([NSNull() ,eventId])
  }

  // promiseCallback
  @objc(promiseCallback:withLocation:withResolver:withRejecter:)
  func promiseCallback(title: String, title2: String,resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    var eventId: Int = 10
    // callback("", eventId);
    // callback([NSNull() ,eventId])
    resolve(eventId * 2);
  }

  // listenerCallback
    // Swift doesn't have synthesize - just define the variable

    // @objc func listenerCallback( eventName: String ) {
    //     self.bridge.eventDispatcher.sendAppEventWithName( "EventReminder", body: "Woot!" )
    // }

}
