import UIKit
import React

#if RCT_NEW_ARCH_ENABLED
import React_Codegen

/**
 * Fabric View Manager for WEPersonalizationView (New Architecture)
 * 
 * This view manager is specifically designed for React Native's New Architecture (Fabric) on iOS.
 * It implements the generated protocol from CodeGen and delegates core functionality
 * to the shared implementation class.
 * 
 * Key Features:
 * - Fabric-compatible view management
 * - CodeGen integration with type safety
 * - Shared business logic with Legacy architecture
 * - Property and style management
 * - WebEngage Personalization SDK integration
 */
@objc(WEPersonalizationFabricViewManager)
class WEPersonalizationFabricViewManager: RCTViewManager {
    
    // Shared implementation for business logic
    private let viewManagerImpl = WEPersonalizationViewManagerImpl()
    
    /**
     * Creates the native view instance
     * Delegates to shared implementation
     */
    override func view() -> UIView {
        return viewManagerImpl.createViewInstance()
    }
    
    /**
     * Indicates that setup should happen on main queue
     * Required for UI operations
     */
    @objc override static func requiresMainQueueSetup() -> Bool {
        return false
    }
    
    /**
     * Returns the module name for Fabric
     */
    override static func moduleName() -> String {
        return "WEPersonalizationView"
    }
    
    // MARK: - Fabric Property Setters
    
    /**
     * Sets the property ID for the personalization widget
     * This identifies which campaign/content to display
     */
    @objc func setPropertyId(_ view: UIView, propertyId: String) {
        viewManagerImpl.setPropertyId(propertyId)
        viewManagerImpl.updateProperties(
            view: view,
            propertyId: propertyId,
            screenName: viewManagerImpl.getScreenName()
        )
    }
    
    /**
     * Sets the screen name for analytics and targeting
     * Used by WebEngage for campaign targeting
     */
    @objc func setScreenName(_ view: UIView, screenName: String) {
        viewManagerImpl.setScreenName(screenName)
        viewManagerImpl.updateProperties(
            view: view,
            propertyId: viewManagerImpl.getPropertyId(),
            screenName: screenName
        )
    }
    
    // MARK: - Style Properties
    
    /**
     * Handles style updates from Fabric
     * Called when view style properties change
     */
    override func updateView(_ view: UIView, withProps props: [String: Any]) {
        super.updateView(view, withProps: props)
        
        // Handle width and height from style
        if let width = props["width"] as? NSNumber {
            let widthValue = CGFloat(width.floatValue)
            viewManagerImpl.setWidth(widthValue)
        }
        
        if let height = props["height"] as? NSNumber {
            let heightValue = CGFloat(height.floatValue)
            viewManagerImpl.setHeight(heightValue)
        }
        
        // Apply style updates
        viewManagerImpl.updateStyle(
            view: view,
            width: viewManagerImpl.getWidth(),
            height: viewManagerImpl.getHeight()
        )
    }
    
    /**
     * Called when the view is being removed/destroyed
     * Allows for cleanup of resources
     */
    override func invalidate() {
        super.invalidate()
        // Additional cleanup can be added here if needed
    }
}

#endif // RCT_NEW_ARCH_ENABLED