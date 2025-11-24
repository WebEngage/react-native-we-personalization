#import <React/RCTViewManager.h>
#import <React/RCTConvert.h>

#import <WebEngage/WebEngage.h>
#import <WEPersonalization/WEPersonalization-Swift.h>
#import "react_native_we_personalization-Swift.h"

@interface WEPersonalizationViewManager : RCTViewManager
@end

@implementation WEPersonalizationViewManager {
    WEPersonalizationViewManagerImpl *_viewManagerImpl;
}

RCT_EXPORT_MODULE(WEPersonalizationView)

- (instancetype)init {
    if (self = [super init]) {
        _viewManagerImpl = [[WEPersonalizationViewManagerImpl alloc] init];
    }
    return self;
}

- (UIView *)view {
    return [_viewManagerImpl createViewInstance];
}

+ (BOOL)requiresMainQueueSetup {
    return NO;
}

RCT_CUSTOM_VIEW_PROPERTY(propertyId, NSNumber, WEInlineWidget) {
    NSNumber *propertyIdNumber = json ? [RCTConvert NSNumber:json] : nil;
    if (propertyIdNumber && view) {
        [view updateProperties:[propertyIdNumber stringValue] screenName:view.screenName ?: @""];
    }
}

RCT_CUSTOM_VIEW_PROPERTY(screenName, NSString, WEInlineWidget) {
    NSString *screenNameValue = json ? [RCTConvert NSString:json] : nil;
    if (screenNameValue && view) {
        view.screenName = screenNameValue;
    }
}

RCT_EXPORT_VIEW_PROPERTY(onDataReceived, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onRendered, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlaceholderException, RCTDirectEventBlock)

@end
