#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>

#import <WebEngage/WebEngage.h>
#import <WEPersonalization/WEPersonalization-Swift.h>

#ifdef RCT_NEW_ARCH_ENABLED

#import "WEPersonalizationSpec.h"
@interface WEPersonalizationBridge : RCTEventEmitter <NativeWEPersonalizationBridgeSpec>
#else
#import <React/RCTBridgeModule.h>
@interface WEPersonalizationBridge : RCTEventEmitter <RCTBridgeModule>
#endif

@end
