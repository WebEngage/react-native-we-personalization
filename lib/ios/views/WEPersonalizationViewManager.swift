/**
 * Legacy View Manager for WEPersonalizationView (Old Architecture)
 * 
 * This view manager is designed for React Native's Legacy Architecture on iOS.
 * It delegates core functionality to the shared implementation class
 * to maintain consistency between Fabric and Legacy implementations.
 * 
 * Key Features:
 * - Legacy architecture compatibility
 * - Shared business logic with Fabric architecture
 * - Property and style management
 * - WebEngage Personalization SDK integration
 */
@objc(WEPersonalizationViewManager)
class WEPersonalizationViewManager: RCTViewManager {
    
    // Shared implementation for consistency with Fabric
    private let viewManagerImpl = WEPersonalizationViewManagerImpl()
    
    /**
     * Creates the native view instance
     * Delegates to shared implementation for consistency with Fabric
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
    
    // MARK: - Property Setters
    
    /**
     * Sets the property ID for the personalization widget
     * This identifies which campaign/content to display
     */
    @objc func setPropertyId(_ view: UIView, propertyId: NSNumber) {
        let propertyIdString = String(propertyId.intValue)
        viewManagerImpl.setPropertyId(propertyIdString)
        viewManagerImpl.updateProperties(
            view: view,
            propertyId: propertyIdString,
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
    
    /**
     * Sets the width of the widget
     */
    @objc func setWidth(_ view: UIView, width: NSNumber) {
        let widthValue = CGFloat(width.floatValue)
        viewManagerImpl.setWidth(widthValue)
        viewManagerImpl.updateStyle(
            view: view,
            width: widthValue,
            height: viewManagerImpl.getHeight()
        )
    }
    
    /**
     * Sets the height of the widget
     */
    @objc func setHeight(_ view: UIView, height: NSNumber) {
        let heightValue = CGFloat(height.floatValue)
        viewManagerImpl.setHeight(heightValue)
        viewManagerImpl.updateStyle(
            view: view,
            width: viewManagerImpl.getWidth(),
            height: heightValue
        )
    }
}

