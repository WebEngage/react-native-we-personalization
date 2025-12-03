#import <React/RCTViewManager.h>
#import <React/RCTConvert.h>

#import <WebEngage/WebEngage.h>
#import <WEPersonalization/WEPersonalization-Swift.h>
#import "react_native_we_personalization-Swift.h"

@interface WEPersonalizationViewManager : RCTViewManager
@end

@implementation WEPersonalizationViewManager

RCT_EXPORT_MODULE(WEPersonalizationView)

- (UIView *)view {
    NSLog(@"WEPersonalization: WEPersonalizationViewManager: view: Creating WEInlineWidget");
    UIView *createdView = [WEPersonalizationViewManagerImpl createView];
    NSLog(@"WEPersonalization: WEPersonalizationViewManager: view: Created view=%@", createdView);
    return createdView;
}

+ (BOOL)requiresMainQueueSetup {
    NSLog(@"WEH: LegacyView: ViewManager: requiresMainQueueSetup");
    return NO;
}

RCT_CUSTOM_VIEW_PROPERTY(propertyId, NSNumber, WEInlineWidget) {
    NSLog(@"WEH: LegacyView: ViewManager: RCT_CUSTOM_VIEW_PROPERTY propertyId = %@", json);
    NSNumber *propertyIdNumber = json ? [RCTConvert NSNumber:json] : nil;
    if (propertyIdNumber && view) {
        [view updateProperties:[propertyIdNumber stringValue] screenName:view.screenName ?: @""];
    }
}

RCT_CUSTOM_VIEW_PROPERTY(screenName, NSString, WEInlineWidget) {
    NSLog(@"WEH: LegacyView: ViewManager: RCT_CUSTOM_VIEW_PROPERTY screenName = %@", json);
    NSString *screenNameValue = json ? [RCTConvert NSString:json] : nil;
    if (screenNameValue && view) {
        view.screenName = screenNameValue;
    }
}

RCT_EXPORT_VIEW_PROPERTY(onDataReceived, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onRendered, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onPlaceholderException, RCTDirectEventBlock)

@end

