//
//  WEPersonalizationBridge-Bridging-Header.h
//  
//
//  Created by Akshay on 13/11/25.
//

#ifndef WEPersonalizationBridge_Bridging_Header_h
#define WEPersonalizationBridge_Bridging_Header_h

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import "WEPersonalizationSpec.h"
#endif

#import <WebEngage/WebEngage.h>
#import <WEPersonalization/WEPersonalization-Swift.h>

#endif /* WEPersonalizationBridge_Bridging_Header_h */
