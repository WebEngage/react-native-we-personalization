#ifdef RCT_NEW_ARCH_ENABLED
#import "WEPersonalizationView.h"

#if __has_include(<react_native_we_personalization/react_native_we_personalization-Swift.h>)
#import <react_native_we_personalization/react_native_we_personalization-Swift.h>
#else
#import "react_native_we_personalization-Swift.h"
#endif

#import <react/renderer/components/WEPersonalizationSpec/ComponentDescriptors.h>
#import <react/renderer/components/WEPersonalizationSpec/EventEmitters.h>
#import <react/renderer/components/WEPersonalizationSpec/Props.h>
#import <react/renderer/components/WEPersonalizationSpec/RCTComponentViewHelpers.h>

#import <React/RCTFabricComponentsPlugins.h>

using namespace facebook::react;

@interface WEPersonalizationView () <RCTWEPersonalizationViewViewProtocol>
@end

@implementation WEPersonalizationView {
    UIView * _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
    return concreteComponentDescriptorProvider<WEPersonalizationViewComponentDescriptor>();
}

+ (void)load
{
    [super load];
    NSLog(@"WE-Inline-Fabric: +load called - Registering Fabric component");
}

- (instancetype)initWithFrame:(CGRect)frame
{
    if (self = [super initWithFrame:frame]) {
        static const auto defaultProps = std::make_shared<const WEPersonalizationViewProps>();
        _props = defaultProps;
        
        _view = [WEPersonalizationViewManagerImpl createView];
        self.contentView = _view;
    }
    return self;
}

- (void)updateProps:(Props::Shared const &)props oldProps:(Props::Shared const &)oldProps
{
    const auto &oldViewProps = *std::static_pointer_cast<WEPersonalizationViewProps const>(_props);
    const auto &newViewProps = *std::static_pointer_cast<WEPersonalizationViewProps const>(props);

    if (oldViewProps.propertyId != newViewProps.propertyId || oldViewProps.screenName != newViewProps.screenName) {
        @try {
            if (!_view) {
                NSLog(@"WE-Inline-Fabric: updateProps - view is nil");
                [super updateProps:props oldProps:oldProps];
                return;
            }
            
            const char *propertyIdCStr = newViewProps.propertyId.c_str();
            const char *screenNameCStr = newViewProps.screenName.c_str();
            
            if (!propertyIdCStr || !screenNameCStr) {
                NSLog(@"WE-Inline-Fabric: updateProps - invalid C string");
                [super updateProps:props oldProps:oldProps];
                return;
            }
            
            NSString *propertyId = [[NSString alloc] initWithUTF8String:propertyIdCStr];
            NSString *screenName = [[NSString alloc] initWithUTF8String:screenNameCStr];
            
            if (!propertyId || !screenName || [propertyId length] == 0 || [screenName length] == 0) {
                NSLog(@"WE-Inline-Fabric: updateProps - invalid NSString conversion");
                [super updateProps:props oldProps:oldProps];
                return;
            }
            
            [WEPersonalizationViewManagerImpl updateView:_view propertyId:propertyId screenName:screenName];
        } @catch (NSException *exception) {
            NSLog(@"WE-Inline-Fabric: Error in updateProps: %@", exception.reason);
        }
    }

    [super updateProps:props oldProps:oldProps];
}

Class<RCTComponentViewProtocol> WEPersonalizationViewCls(void)
{
    return WEPersonalizationView.class;
}

@end
#endif
