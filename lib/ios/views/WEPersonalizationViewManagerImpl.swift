import UIKit
import React

/**
 * Shared Implementation Helper for WEPersonalizationView Manager
 * 
 * This class provides the core business logic for Legacy Architecture view manager.
 * All React Native handling is done in Objective-C++, this class only handles
 * WebEngage SDK business logic.
 * 
 * Pattern: Similar to ColoredViewManagerImpl in example-component
 * - Pure Swift business logic
 * - No React Native dependencies
 * - Called from Objective-C++ view manager
 */
@objc
public class WEPersonalizationViewManagerImpl: NSObject {
    
    private var propertyId: String?
    private var screenName: String?
    
    @objc
    public static let NAME = "WEPersonalizationViewManager"
    
    @objc
    public func createViewInstance() -> UIView {
        return WEInlineWidget(frame: .zero)
    }
    
    @objc
    public func updateProperties(view: UIView, propertyId: String?, screenName: String?) {
        guard let inlineWidget = view as? WEInlineWidget else { return }
        
        if let propertyId = propertyId, let screenName = screenName {
            inlineWidget.updateProperties(propertyId, screenName: screenName)
        }
    }
    
    @objc
    public func setPropertyId(_ propertyId: String?) {
        self.propertyId = propertyId
    }
    
    @objc
    public func setScreenName(_ screenName: String?) {
        self.screenName = screenName
    }
    
    @objc
    public func getPropertyId() -> String? {
        return propertyId
    }
    
    @objc
    public func getScreenName() -> String? {
        return screenName
    }
}