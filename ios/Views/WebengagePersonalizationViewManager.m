#import <React/RCTViewManager.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(WebengagePersonalizationViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(propertyId, NSInteger)
RCT_EXPORT_VIEW_PROPERTY(screenName, NSString)
RCT_EXPORT_VIEW_PROPERTY(width, CGFloat)
RCT_EXPORT_VIEW_PROPERTY(height, CGFloat)

@end
