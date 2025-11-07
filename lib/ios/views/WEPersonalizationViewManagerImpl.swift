import UIKit
import React

/**
 * Shared Implementation for WEPersonalizationView Manager
 * 
 * This class provides the core business logic for both Fabric (New Architecture)
 * and Legacy Architecture view managers on iOS. It ensures consistency between
 * both implementations while maintaining the WebEngage Personalization functionality.
 * 
 * Features:
 * - Shared business logic between architectures
 * - WEInlineWidget creation and management
 * - Property and style updates
 * - WebEngage Personalization SDK integration
 */
@objc
class WEPersonalizationViewManagerImpl: NSObject {
    
    // MARK: - Properties
    private var width: CGFloat = 300.0
    private var height: CGFloat = 200.0
    private var propertyId: String?
    private var screenName: String?
    
    // MARK: - View Creation
    
    /**
     * Creates a new WEInlineWidget instance
     * Used by both Fabric and Legacy view managers
     * 
     * @return Configured WEInlineWidget instance
     */
    @objc
    func createViewInstance() -> UIView {
        let frame = CGRect(x: 0, y: 0, width: width, height: height)
        let nativeView = WEInlineWidget(frame: frame)
        
        // Apply current properties if available
        if let propertyId = self.propertyId, let screenName = self.screenName {
            updateProperties(view: nativeView, propertyId: propertyId, screenName: screenName)
        }
        
        return nativeView
    }
    
    // MARK: - Property Management
    
    /**
     * Updates view properties when props change
     * Handles both propertyId and screenName updates
     * 
     * @param view The WEInlineWidget instance to update
     * @param propertyId The property ID for personalization content
     * @param screenName The screen name for analytics
     */
    @objc
    func updateProperties(view: UIView, propertyId: String?, screenName: String?) {
        guard let inlineWidget = view as? WEInlineWidget else {
            print("WEPersonalizationViewManagerImpl: View is not a WEInlineWidget instance")
            return
        }
        
        self.propertyId = propertyId
        self.screenName = screenName
        
        // Update the widget with new properties
        if let propertyId = propertyId, let screenName = screenName {
            inlineWidget.updateProperties(propertyId: propertyId, screenName: screenName)
        }
    }
    
    /**
     * Updates view style dimensions
     * Handles width and height changes
     * 
     * @param view The WEInlineWidget instance to update
     * @param width New width value
     * @param height New height value
     */
    @objc
    func updateStyle(view: UIView, width: CGFloat, height: CGFloat) {
        self.width = width
        self.height = height
        
        guard let inlineWidget = view as? WEInlineWidget else {
            print("WEPersonalizationViewManagerImpl: View is not a WEInlineWidget instance")
            return
        }
        
        // Update the widget frame
        var frame = inlineWidget.frame
        frame.size.width = width
        frame.size.height = height
        inlineWidget.frame = frame
        
        // Trigger layout update
        inlineWidget.setNeedsLayout()
        inlineWidget.layoutIfNeeded()
    }
    
    // MARK: - Property Setters
    
    /**
     * Sets the property ID for the widget
     * Used for identifying which personalization content to display
     * 
     * @param propertyId The property ID string
     */
    @objc
    func setPropertyId(_ propertyId: String?) {
        self.propertyId = propertyId
    }
    
    /**
     * Sets the screen name for the widget
     * Used for analytics and campaign targeting
     * 
     * @param screenName The screen name string
     */
    @objc
    func setScreenName(_ screenName: String?) {
        self.screenName = screenName
    }
    
    /**
     * Sets the width for the widget
     * 
     * @param width The width value
     */
    @objc
    func setWidth(_ width: CGFloat) {
        self.width = width
    }
    
    /**
     * Sets the height for the widget
     * 
     * @param height The height value
     */
    @objc
    func setHeight(_ height: CGFloat) {
        self.height = height
    }
    
    // MARK: - Property Getters
    
    /**
     * Gets the current property ID
     * 
     * @return Current property ID or nil
     */
    @objc
    func getPropertyId() -> String? {
        return propertyId
    }
    
    /**
     * Gets the current screen name
     * 
     * @return Current screen name or nil
     */
    @objc
    func getScreenName() -> String? {
        return screenName
    }
    
    /**
     * Gets the current width
     * 
     * @return Current width value
     */
    @objc
    func getWidth() -> CGFloat {
        return width
    }
    
    /**
     * Gets the current height
     * 
     * @return Current height value
     */
    @objc
    func getHeight() -> CGFloat {
        return height
    }
}