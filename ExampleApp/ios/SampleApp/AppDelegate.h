#import <RCTAppDelegate.h>
#import <UIKit/UIKit.h>
#import <WEGWebEngageBridge.h>

@interface AppDelegate : RCTAppDelegate
@property (nonatomic, strong) WEGWebEngageBridge *weBridge; // Docs: Add this line to Docs
@end
