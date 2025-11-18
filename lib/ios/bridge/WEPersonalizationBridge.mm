#import "WEPersonalizationBridge.h"
#include <Foundation/NSObjCRuntime.h>
#import "react_native_we_personalization-Swift.h"
#import <WebEngage/WebEngage-Swift.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <WEPersonalizationSpec/WEPersonalizationSpec.h>
#endif
// https://github.com/react-native-community/RNNewArchitectureLibraries/tree/feat/swift-event-emitter
@implementation WEPersonalizationBridge

RCT_EXPORT_MODULE();

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

+ (id)allocWithZone:(NSZone *)zone
{
  static WEPersonalizationBridge *sharedInstance = nil;
  static dispatch_once_t onceToken;
  dispatch_once(&onceToken, ^{
    sharedInstance = [super allocWithZone:zone];
  });
  return sharedInstance;
}

- (instancetype)init
{
  self = [super init];
  if (self) {
    [[WEPersonalizationBridgeImpl shared] initialize];
    [[WEPersonalizationBridgeImpl shared] setEmitter:self];
  }
  return self;
}

- (NSArray<NSString *> *)supportedEvents
{
  return [[WEPersonalizationBridgeImpl shared] supportedEvents];
}

RCT_EXPORT_METHOD(initWePersonalization)
{
  NSLog(@"Arch: iOS.m - Initializing WE Personalization SDK");
  [[WEPersonalizationBridgeImpl shared] initWePersonalization];
}

RCT_EXPORT_METHOD(registerWECampaignCallback)
{
  [[WEPersonalizationBridgeImpl shared] registerWECampaignCallback];
}

RCT_EXPORT_METHOD(deregisterWECampaignCallback)
{
  [[WEPersonalizationBridgeImpl shared] deregisterWECampaignCallback];
}

RCT_EXPORT_METHOD(registerProperty:(NSString *)propertyId screenName:(NSString *)screenName)
{
  [[WEPersonalizationBridgeImpl shared] registerProperty:propertyId screenName:screenName];
}

RCT_EXPORT_METHOD(deregisterProperty:(NSString *)propertyId)
{
  [[WEPersonalizationBridgeImpl shared] deregisterProperty:propertyId];
}

RCT_EXPORT_METHOD(trackClick:(NSString *)propertyId attributes:(NSDictionary *)attributes)
{
  [[WEPersonalizationBridgeImpl shared] trackClick:propertyId attributes:attributes];
}

RCT_EXPORT_METHOD(trackImpression:(NSString *)propertyId attributes:(NSDictionary *)attributes)
{
  [[WEPersonalizationBridgeImpl shared] trackImpression:propertyId attributes:attributes];
}

RCT_EXPORT_METHOD(addListener:(NSString *)eventType)
{
  [[WEPersonalizationBridgeImpl shared] addListener:eventType];
}

RCT_EXPORT_METHOD(removeListeners:(double)count)
{
  [[WEPersonalizationBridgeImpl shared] removeListeners:count];
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeWEPersonalizationBridgeSpecJSI>(params);
}
#endif

@end
