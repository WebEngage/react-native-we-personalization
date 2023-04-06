#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

//@interface RCT_EXTERN_MODULE(WEPersonalizationBridge, NSObject)

@interface RCT_EXTERN_MODULE(WEPersonalizationBridge, RCTEventEmitter)

RCT_EXTERN_METHOD(supportedEvents)


RCT_EXTERN_METHOD(registerCampaignCallback)

RCT_EXTERN_METHOD(userWillHandleDeepLink:(BOOL)doesUserHandleCallbacks)

//unRegisterForCampaigns
RCT_EXTERN_METHOD(unRegisterCampaignCallback)

RCT_EXTERN_METHOD(registerCallback:(int)propertyId screenName:(NSString *)screenName)
//unRegisterCallback
RCT_EXTERN_METHOD(unRegisterCallback:(int)propertyId)

RCT_EXTERN_METHOD(trackClick:(int)propertyId attributes:(NSDictionary *)attributes)


RCT_EXTERN_METHOD(trackImpression:(int)propertyId attributes:(NSDictionary *)attributes)



+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
