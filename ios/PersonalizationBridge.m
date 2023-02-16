#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

//@interface RCT_EXTERN_MODULE(PersonalizationBridge, NSObject)

@interface RCT_EXTERN_MODULE(PersonalizationBridge, RCTEventEmitter)

RCT_EXTERN_METHOD(supportedEvents)

RCT_EXTERN_METHOD(registerCampaignCallback)


//RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
//                 withResolver:(RCTPromiseResolveBlock)resolve
//                 withRejecter:(RCTPromiseRejectBlock)reject)
//
//RCT_EXTERN_METHOD(add:(float)a withB:(float)b
//                 withResolver:(RCTPromiseResolveBlock)resolve
//                 withRejecter:(RCTPromiseRejectBlock)reject)
//
//
//RCT_EXTERN_METHOD(immediateCallback:(NSString *)title withLocation:(NSString *)location
//                 withCallback: (RCTResponseSenderBlock)callback
//                 withErrorCallback: (RCTResponseSenderBlock)errorCallback)
//
//RCT_EXTERN_METHOD(promiseCallback:(NSString *)title withLocation:(NSString *)location
//                 withResolver:(RCTPromiseResolveBlock)resolve
//                 withRejecter:(RCTPromiseRejectBlock)reject)

// RCT_EXTERN_METHOD( testEvent:(NSString *)eventName )


// RCT_EXPORT_METHOD(immediateCallback:(NSString *)title location:(NSString *)location callback: (RCTResponseSenderBlock)callback)
// {
//  NSInteger eventId = "123";
//  callback(@[@(eventId)]);

//  RCTLogInfo(@"Pretending to create an event %@ at %@", title, location);
// }


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
