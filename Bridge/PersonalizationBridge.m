#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

//@interface RCT_EXTERN_MODULE(PersonalizationBridge, NSObject)

@interface RCT_EXTERN_MODULE(PersonalizationBridge, RCTEventEmitter)

RCT_EXTERN_METHOD(supportedEvents)


RCT_EXTERN_METHOD(registerCampaignCallback)

RCT_EXTERN_METHOD(userWillHandleDeepLink:(BOOL)doesUserHandleCallbacks)

//unRegisterForCampaigns
RCT_EXTERN_METHOD(unRegisterCampaignCallback)

RCT_EXTERN_METHOD(registerCallback:(int)propertyId)
//unRegisterCallback
RCT_EXTERN_METHOD(unRegisterCallback:(int)propertyId)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
