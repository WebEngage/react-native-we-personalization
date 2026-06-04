import UIKit
import React

@objc
public class WEPersonalizationViewManagerImpl: NSObject {
    
    @objc
    public static func createView() -> UIView {
        let widget = WEInlineWidget(frame: .zero)
        return widget
    }
    
    @objc
    public static func updateView(_ view: UIView, propertyId: String, screenName: String) {
        NSLog("WEPersonalization: WEPersonalizationViewManagerImpl: updateView: propertyId=%@, screenName=%@", propertyId, screenName)
        guard let inlineWidget = view as? WEInlineWidget else { 
            NSLog("WEPersonalization: WEPersonalizationViewManagerImpl: updateView: Failed to cast view to WEInlineWidget")
            return 
        }
        inlineWidget.updateProperties(propertyId, screenName: screenName)
    }
}