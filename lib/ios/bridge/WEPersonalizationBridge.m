#import <React/RCTBridgeModule.h>
#import <React/RCTLog.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "WEPersonalizationSpec.h"
#endif

@interface RCT_EXTERN_MODULE(WEPersonalizationBridge, RCTEventEmitter)

RCT_EXTERN_METHOD(supportedEvents)

RCT_EXTERN_METHOD(initWePersonalization)

RCT_EXTERN_METHOD(registerWECampaignCallback)

RCT_EXTERN_METHOD(deregisterWECampaignCallback)

RCT_EXTERN_METHOD(registerProperty:(NSString *)propertyId screenName:(NSString *)screenName)
RCT_EXTERN_METHOD(deregisterProperty:(NSString *)propertyId)

RCT_EXTERN_METHOD(trackClick:(NSString *)propertyId attributes:(NSDictionary *)attributes)

RCT_EXTERN_METHOD(trackImpression:(NSString *)propertyId attributes:(NSDictionary *)attributes)

RCT_EXTERN_METHOD(addListener:(NSString *)eventType)
RCT_EXTERN_METHOD(removeListeners:(double)count)

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

@end