#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

// WEPersonalizationBridge
//@interface RCT_EXTERN_MODULE(WEPersonalizationBridge, NSObject)

@interface RCT_EXTERN_MODULE(WEPersonalizationBridge, RCTEventEmitter)

RCT_EXTERN_METHOD(supportedEvents)


RCT_EXTERN_METHOD(registerWECampaignCallback)

//unRegisterForCampaigns
RCT_EXTERN_METHOD(deregisterWECampaignCallback)

RCT_EXTERN_METHOD(registerProperty:(int)propertyId screenName:(NSString *)screenName)
RCT_EXTERN_METHOD(deregisterProperty:(int)propertyId)

RCT_EXTERN_METHOD(trackClick:(int)propertyId attributes:(NSDictionary *)attributes)


RCT_EXTERN_METHOD(trackImpression:(int)propertyId attributes:(NSDictionary *)attributes)



+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
