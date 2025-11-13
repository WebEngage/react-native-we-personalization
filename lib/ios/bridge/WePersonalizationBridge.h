#import <Foundation/Foundation.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <WEPersonalizationSpec/WEPersonalizationSpec.h>
@interface WEPersonalizationBridge : RCTEventEmitter <NativeWEPersonalizationBridgeSpec>
#else
#import <React/RCTBridgeModule.h>
@interface WEPersonalizationBridge : RCTEventEmitter <RCTBridgeModule>
#endif

@end
