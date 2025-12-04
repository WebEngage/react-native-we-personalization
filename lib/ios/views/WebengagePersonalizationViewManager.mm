#import <React/RCTViewManager.h>
#import <React/RCTConvert.h>

#import <WebEngage/WebEngage.h>
#import <WEPersonalization/WEPersonalization-Swift.h>

#if __has_include(<react_native_we_personalization/react_native_we_personalization-Swift.h>)
#import <react_native_we_personalization/react_native_we_personalization-Swift.h>
#else
#import "react_native_we_personalization-Swift.h"
#endif

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

RCT_CUSTOM_VIEW_PROPERTY(propertyId, NSString, WEInlineWidget) {
    NSLog(@"WEH: LegacyView: ViewManager: RCT_CUSTOM_VIEW_PROPERTY propertyId = %@", json);
    NSString *propertyIdValue = json ? [RCTConvert NSString:json] : nil;
    if (propertyIdValue && view) {
        [view updateProperties:propertyIdValue screenName:view.screenName ?: @""];
    }
}

RCT_CUSTOM_VIEW_PROPERTY(screenName, NSString, WEInlineWidget) {
    NSLog(@"WEH: LegacyView: ViewManager: RCT_CUSTOM_VIEW_PROPERTY screenName = %@", json);
    NSString *screenNameValue = json ? [RCTConvert NSString:json] : nil;
    if (screenNameValue && view) {
        view.screenName = screenNameValue;
    }
}

@end

