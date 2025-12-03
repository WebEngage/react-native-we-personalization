#ifdef RCT_NEW_ARCH_ENABLED
#import "WEPersonalizationView.h"
#import "react_native_we_personalization-Swift.h"

#import <react/renderer/components/WEPersonalizationSpec/ComponentDescriptors.h>
#import <react/renderer/components/WEPersonalizationSpec/EventEmitters.h>
#import <react/renderer/components/WEPersonalizationSpec/Props.h>
#import <react/renderer/components/WEPersonalizationSpec/RCTComponentViewHelpers.h>

#import "RCTFabricComponentsPlugins.h"

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
        NSString *propertyId = [[NSString alloc] initWithUTF8String: newViewProps.propertyId.c_str()];
        NSString *screenName = [[NSString alloc] initWithUTF8String: newViewProps.screenName.c_str()];
        [WEPersonalizationViewManagerImpl updateView:_view propertyId:propertyId screenName:screenName];
    }

    [super updateProps:props oldProps:oldProps];
}

Class<RCTComponentViewProtocol> WEPersonalizationViewCls(void)
{
    return WEPersonalizationView.class;
}

@end
#endif
