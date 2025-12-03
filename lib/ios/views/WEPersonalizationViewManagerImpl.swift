import UIKit
import React

@objc
public class WEPersonalizationViewManagerImpl: NSObject {
    
    @objc
    public static func createView() -> UIView {
        return WEInlineWidget(frame: .zero)
    }
    
    @objc
    public static func updateView(_ view: UIView, propertyId: String, screenName: String) {
        guard let inlineWidget = view as? WEInlineWidget else { return }
        inlineWidget.updateProperties(propertyId, screenName: screenName)
    }
}