#import <RCTAppDelegate.h>
#import <UIKit/UIKit.h>
#import <WEGWebEngageBridge.h>
#import <UserNotifications/UNUserNotificationCenter.h>

@interface AppDelegate : RCTAppDelegate <UNUserNotificationCenterDelegate>
@property (nonatomic, strong) WEGWebEngageBridge *webEngageBridge;
@end
