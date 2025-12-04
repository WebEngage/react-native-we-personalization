#import "WEPersonalizationBridge.h"
#include <Foundation/NSObjCRuntime.h>

#if __has_include(<react_native_we_personalization/react_native_we_personalization-Swift.h>)
#import <react_native_we_personalization/react_native_we_personalization-Swift.h>
#else
#import "react_native_we_personalization-Swift.h"
#endif

#import <WebEngage/WebEngage-Swift.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import "WEPersonalizationSpec.h"
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

RCT_EXPORT_METHOD(registerProperty:(nonnull id)propertyId screenName:(nonnull NSString *)screenName)
{
  @try {
    if (!propertyId || !screenName) {
      NSLog(@"WEPersonalizationBridge: registerProperty called with nil parameters");
      return;
    }
    
    NSNumber *propertyIdNum = nil;
    
    if ([propertyId isKindOfClass:[NSNumber class]]) {
      propertyIdNum = (NSNumber *)propertyId;
    } else if ([propertyId isKindOfClass:[NSString class]]) {
      NSString *strValue = (NSString *)propertyId;
      NSScanner *scanner = [NSScanner scannerWithString:strValue];
      NSInteger intValue;
      if ([scanner scanInteger:&intValue] && [scanner isAtEnd]) {
        propertyIdNum = @(intValue);
      } else {
        NSLog(@"WEPersonalizationBridge: Invalid propertyId. Must be a valid number. Got: %@", strValue);
        return;
      }
    } else {
      NSLog(@"WEPersonalizationBridge: Invalid propertyId type. Must be a number or numeric string. Got: %@", propertyId);
      return;
    }
    
    [[WEPersonalizationBridgeImpl shared] registerProperty:[propertyIdNum integerValue] screenName:screenName];
  } @catch (NSException *exception) {
    NSLog(@"WEPersonalizationBridge: Error in registerProperty: %@", exception.reason);
  }
}

RCT_EXPORT_METHOD(deregisterProperty:(nonnull id)propertyId)
{
  @try {
    if (!propertyId) {
      NSLog(@"WEPersonalizationBridge: deregisterProperty called with nil propertyId");
      return;
    }
    
    NSNumber *propertyIdNum = nil;
    
    if ([propertyId isKindOfClass:[NSNumber class]]) {
      propertyIdNum = (NSNumber *)propertyId;
    } else if ([propertyId isKindOfClass:[NSString class]]) {
      NSString *strValue = (NSString *)propertyId;
      NSScanner *scanner = [NSScanner scannerWithString:strValue];
      NSInteger intValue;
      if ([scanner scanInteger:&intValue] && [scanner isAtEnd]) {
        propertyIdNum = @(intValue);
      } else {
        NSLog(@"WEPersonalizationBridge: Invalid propertyId. Must be a valid number. Got: %@", strValue);
        return;
      }
    } else {
      NSLog(@"WEPersonalizationBridge: Invalid propertyId type. Must be a number or numeric string. Got: %@", propertyId);
      return;
    }
    
    [[WEPersonalizationBridgeImpl shared] deregisterProperty:[propertyIdNum integerValue]];
  } @catch (NSException *exception) {
    NSLog(@"WEPersonalizationBridge: Error in deregisterProperty: %@", exception.reason);
  }
}

RCT_EXPORT_METHOD(trackClick:(nonnull id)propertyId attributes:(NSDictionary *)attributes)
{
  @try {
    if (!propertyId) {
      NSLog(@"WEPersonalizationBridge: trackClick called with nil propertyId");
      return;
    }
    
    NSNumber *propertyIdNum = nil;
    
    if ([propertyId isKindOfClass:[NSNumber class]]) {
      propertyIdNum = (NSNumber *)propertyId;
    } else if ([propertyId isKindOfClass:[NSString class]]) {
      NSString *strValue = (NSString *)propertyId;
      NSScanner *scanner = [NSScanner scannerWithString:strValue];
      NSInteger intValue;
      if ([scanner scanInteger:&intValue] && [scanner isAtEnd]) {
        propertyIdNum = @(intValue);
      } else {
        NSLog(@"WEPersonalizationBridge: Invalid propertyId for trackClick. Must be a valid number. Got: %@", strValue);
        return;
      }
    } else {
      NSLog(@"WEPersonalizationBridge: Invalid propertyId type for trackClick. Got: %@", propertyId);
      return;
    }
    
    [[WEPersonalizationBridgeImpl shared] trackClick:[propertyIdNum integerValue] attributes:attributes];
  } @catch (NSException *exception) {
    NSLog(@"WEPersonalizationBridge: Error in trackClick: %@", exception.reason);
  }
}

RCT_EXPORT_METHOD(trackImpression:(nonnull id)propertyId attributes:(NSDictionary *)attributes)
{
  @try {
    if (!propertyId) {
      NSLog(@"WEPersonalizationBridge: trackImpression called with nil propertyId");
      return;
    }
    
    NSNumber *propertyIdNum = nil;
    
    if ([propertyId isKindOfClass:[NSNumber class]]) {
      propertyIdNum = (NSNumber *)propertyId;
    } else if ([propertyId isKindOfClass:[NSString class]]) {
      NSString *strValue = (NSString *)propertyId;
      NSScanner *scanner = [NSScanner scannerWithString:strValue];
      NSInteger intValue;
      if ([scanner scanInteger:&intValue] && [scanner isAtEnd]) {
        propertyIdNum = @(intValue);
      } else {
        NSLog(@"WEPersonalizationBridge: Invalid propertyId for trackImpression. Must be a valid number. Got: %@", strValue);
        return;
      }
    } else {
      NSLog(@"WEPersonalizationBridge: Invalid propertyId type for trackImpression. Got: %@", propertyId);
      return;
    }
    
    [[WEPersonalizationBridgeImpl shared] trackImpression:[propertyIdNum integerValue] attributes:attributes];
  } @catch (NSException *exception) {
    NSLog(@"WEPersonalizationBridge: Error in trackImpression: %@", exception.reason);
  }
}

RCT_EXPORT_METHOD(addListener:(NSString *)eventType)
{
  [super addListener:eventType];
  [[WEPersonalizationBridgeImpl shared] addListener:eventType];
}

RCT_EXPORT_METHOD(removeListeners:(double)count)
{
  [super removeListeners:count];
  [[WEPersonalizationBridgeImpl shared] removeListeners:count];
}

- (void)startObserving
{
  // Called when the first listener is added
}

- (void)stopObserving
{
  // Called when the last listener is removed
}

#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(const facebook::react::ObjCTurboModule::InitParams &)params
{
  return std::make_shared<facebook::react::NativeWEPersonalizationBridgeSpecJSI>(params);
}
#endif

@end
