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
    UIView *createdView = [WEPersonalizationViewManagerImpl createView];
    return createdView;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

RCT_CUSTOM_VIEW_PROPERTY(propertyId, NSString, WEInlineWidget) {
    @try {
        if (!view || ![view isKindOfClass:[WEInlineWidget class]]) {
            return;
        }
        
        NSString *propertyIdValue = json ? [RCTConvert NSString:json] : nil;
        if (!propertyIdValue || [propertyIdValue length] == 0) {
            return;
        }
        
        NSString *screenName = view.screenName ?: @"";
        [view updateProperties:propertyIdValue screenName:screenName];
    } @catch (NSException *exception) {
        NSLog(@"WE-Inline-Legacy: Error setting propertyId: %@", exception.reason);
    }
}

RCT_CUSTOM_VIEW_PROPERTY(screenName, NSString, WEInlineWidget) {
    @try {
        if (!view || ![view isKindOfClass:[WEInlineWidget class]]) {
            return;
        }
        
        NSString *screenNameValue = json ? [RCTConvert NSString:json] : nil;
        if (!screenNameValue || [screenNameValue length] == 0) {
            return;
        }
        
        view.screenName = screenNameValue;
    } @catch (NSException *exception) {
        NSLog(@"WE-Inline-Legacy: Error setting screenName: %@", exception.reason);
    }
}

@end

