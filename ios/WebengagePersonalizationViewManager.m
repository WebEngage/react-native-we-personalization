#import <React/RCTViewManager.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(WebengagePersonalizationViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(propertyId, NSString)
RCT_EXPORT_VIEW_PROPERTY(screenName, NSString)
RCT_EXPORT_VIEW_PROPERTY(color, NSString)

@end

//
//@interface RCT_EXTERN_MODULE(RNEventEmitter, RCTEventEmitter)
//
//RCT_EXTERN_METHOD(supportedEvents)
