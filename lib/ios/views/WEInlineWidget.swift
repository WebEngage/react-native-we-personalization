
import Foundation
import UIKit
import React
import WEPersonalization
public class WEInlineWidget: UIView{
    var inlineView: WEInlineView? = nil
    var campaignData: WECampaignData? = nil
    private var isViewSetup = false
    private var isLoadInProgress = false
    private var isReloading = false
    @objc var width: CGFloat = 0.1 {
        didSet {
            if !isReloading {
                setupView()
            }
        }
    }
    
    @objc var height: CGFloat = 0.1 {
        didSet {
            if !isReloading {
                setupView()
            }
        }
    }
    
    @objc public var screenName: String = ""{
        didSet {
            WELogger.d(WEConstants.TAG+" Initialization: screenName=\(screenName)")
            self.setupView()
        }
    }
    
    @objc var propertyId: Int = 0 {
        didSet {
            WELogger.d(WEConstants.TAG+" Initialization: propertyId=\(propertyId)")
            self.setupView()
        }
    }
    
    /**
     * Updates the widget properties from the view manager
     * Converts string propertyId to int for internal use
     */
    @objc public func updateProperties(_ propertyId: String, screenName: String) {
        NSLog("\(WEConstants.TAG) updateProperties: property=%@, screen=%@", propertyId, screenName)
        let wasReloading = isReloading
        if wasReloading {
            isReloading = false
        }
        
        if let propertyIdInt = Int(propertyId) {
            self.propertyId = propertyIdInt
        }
        self.screenName = screenName
        
        if wasReloading && self.propertyId != 0 && self.width > 0.1 && self.height > 0.1 && !self.screenName.isEmpty {
            self.setupView()
        }
    }
    
    @objc func reloadViews(){
        WELogger.d(WEConstants.TAG+" reloadView: property=\(self.propertyId)")
        guard Thread.isMainThread else {
            DispatchQueue.main.async { self.reloadViews() }
            return
        }
        
        self.inlineView?.removeFromSuperview()
        self.inlineView = nil
        self.isViewSetup = false
        self.isLoadInProgress = false
        self.campaignData = nil
        self.isReloading = true
    }
    
    override init(frame: CGRect) {
        NSLog("\(WEConstants.TAG) init: frame=%@", NSCoder.string(for: frame))
        WELogger.d(WEConstants.TAG+" init called")
        super.init(frame: frame)
        NotificationCenter.default.addObserver(self, selector: #selector(reloadViews), name: Notification.Name(WEConstants.SCREEN_NAVIGATED), object: nil)
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
        WELogger.d(WEConstants.TAG+" layoutSubviews: bounds=\(self.bounds)")
        WELogger.d(WEConstants.TAG+" layoutSubviews: width=\(self.width), height=\(self.height)")
        WELogger.d(WEConstants.TAG+" layoutSubviews: property=\(self.propertyId), screen=\(self.screenName)")
        if self.width != self.bounds.width || self.height != self.bounds.height {
            WELogger.d(WEConstants.TAG+" layoutSubviews: updating dimensions \(self.width)x\(self.height) → \(self.bounds.width)x\(self.bounds.height)")
            self.width = self.bounds.width
            self.height = self.bounds.height
        }
        
        if isReloading && self.propertyId != 0 && self.width > 0.1 && self.height > 0.1 && !self.screenName.isEmpty {
            WELogger.d(WEConstants.TAG+" layoutSubviews: triggering setup after reload")
            isReloading = false
            setupView()
        }
    }
    
    deinit{
        WELogger.d(WEConstants.TAG+" deinit: property=\(self.propertyId)")
        NotificationCenter.default.removeObserver(self)
        removeScrollObserver()
        WEPersonalization.shared.unregisterWEPlaceholderCallback(self.propertyId)
        WEPersonalization.shared.deRegisterCampaignControlGroupCallback(tag: self.propertyId)
    }
    
    private func removeScrollObserver() {
        guard isObserverAdded, let scrollview = observedScrollView else { return }
        scrollview.removeObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset))
        isObserverAdded = false
        observedScrollView = nil
    }
    
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    
    private func setupView() {
        guard Thread.isMainThread else {
            DispatchQueue.main.async { self.setupView() }
            return
        }
        
        WELogger.d(WEConstants.TAG+" setupView: width=\(self.width), height=\(self.height), property=\(self.propertyId)")
        
        if isReloading {
            WELogger.d(WEConstants.TAG+" setupView: skipping - waiting for new props after reload")
            return
        }
        
        if self.height > 0.1 && self.width > 0.1 && propertyId != 0 && !isViewSetup {
            WELogger.d(WEConstants.TAG+" setupView: conditions met, creating inlineView")
            isViewSetup = true
            inlineView?.removeFromSuperview()
            inlineView = WEInlineView(frame: CGRect(x: 0, y: 0, width: self.width, height: self.height))
            inlineView?.tag = self.propertyId
            if(self.propertyId != 0 && !isLoadInProgress) {
                WELogger.d(WEConstants.TAG+" loadView: property=\(self.propertyId)")
                WELogger.d(WEConstants.TAG+" load: tag=\(self.propertyId)")
                isLoadInProgress = true
                inlineView?.load(tag: self.propertyId, callbacks: self)
                monitorVisibilityAndFireEvent()
            }
            if let view = inlineView {
                addSubview(view)
                WELogger.d(WEConstants.TAG+" inlineView added to subview")
            }
        } else {
            WELogger.d(WEConstants.TAG+" setupView: conditions not met - height=\(self.height > 0.1), width=\(self.width > 0.1), property=\(propertyId != 0), isViewSetup=\(isViewSetup)")
        }
    }
    
    func fireCGEvent(){
        guard let view = inlineView else { return }
        WEPersonalization.shared.trackCGEvent(forPropertyId: view.tag)
        WEPersonalization.shared.registerCampaignControlGroupCallback(tag: view.tag, callback: self)
    }
    
    private var observerContextCG = 0
    private var isObserverAdded = false
    private weak var observedScrollView: UIScrollView?
    
    func monitorVisibilityAndFireEvent(){
            // for cg
            if(self.isVisibleToUser){
                fireCGEvent()
            }else{
                if let scrollview = self.getScrollview(view: self), !isObserverAdded {
                    scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: &observerContextCG)
                    observedScrollView = scrollview
                    isObserverAdded = true
                }
                
            }
        }
}


extension WEInlineWidget : WEPlaceholderCallback{
    public func onRendered(data: WECampaignData) {
        WELogger.d(WEConstants.TAG+" onRendered: property=\(self.propertyId), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEConstants.PAYLOAD: data.toJSONString() ?? ""]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: WEConstants.METHOD_NAME_ON_RENDERED, body: campaignData)
        
        if self.isVisibleToUser {
            WELogger.d(WEConstants.TAG+" onRendered: view visible, tracking impression")
            data.trackImpression(attributes: nil)
        }
    }
    public func onDataReceived(_ data: WECampaignData) {
        self.campaignData = data;
        isLoadInProgress = false
        WELogger.d(WEConstants.TAG+" onDataReceived: property=\(self.propertyId), campaign=\(data.campaignId ?? "nil")")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEConstants.PAYLOAD: data.toJSONString() ?? ""]
        
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: WEConstants.METHOD_NAME_ON_DATA_RECEIVED, body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        WELogger.d(WEConstants.TAG+" onPlaceholderException: property=\(self.propertyId), error=\(exception.localizedDescription)")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: targetViewId, WEConstants.PAYLOAD_CAMPAIGN_ID: campaignId ?? "", WEConstants.EXCEPTION: exception.localizedDescription]
        WEPersonalizationBridgeImpl.emitter?.sendEvent(withName: WEConstants.METHOD_NAME_ON_PLACEHOLDER_EXCEPTION, body: campaignData)
    }
}

extension WEInlineWidget {
    func getScrollview(view:UIView)->UIScrollView?{
        if let superview = view.superview, superview is UIScrollView{
            return view.superview as? UIScrollView
        }
        return view.superview == nil ? nil : getScrollview(view: view.superview!)
    }
    
    public override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey: Any]?, context: UnsafeMutableRawPointer?) {
        guard keyPath == #keyPath(UIScrollView.contentOffset) else {
            super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
            return
        }
        
        if self.isVisibleToUser && isObserverAdded {
            removeScrollObserver()
            if context == &self.observerContextCG {
                fireCGEvent()
            } else if let data = self.campaignData {
                data.trackImpression(attributes: nil)
            }
        }
    }
}

extension UIView {
    var isVisibleToUser: Bool {
        if isHidden || alpha == 0 || superview == nil || window == nil {
            return false
        }
        
        guard let window = self.window else {
            return false
        }
        
        let rootViewController: UIViewController?
        if #available(iOS 13.0, *) {
            rootViewController = window.windowScene?.windows.first?.rootViewController
        } else {
            rootViewController = window.rootViewController
        }
        
        guard let rootVC = rootViewController else {
            return false
        }
        
        let viewFrame = convert(bounds, to: rootVC.view)
        
        let topSafeArea: CGFloat
        let bottomSafeArea: CGFloat
        
        if #available(iOS 11.0, *) {
            topSafeArea = rootVC.view.safeAreaInsets.top
            bottomSafeArea = rootVC.view.safeAreaInsets.bottom
        } else {
            topSafeArea = rootVC.topLayoutGuide.length
            bottomSafeArea = rootVC.bottomLayoutGuide.length
        }
        
        return viewFrame.maxX >= 0 &&
            viewFrame.minX <= rootVC.view.bounds.width &&
            viewFrame.maxY >= topSafeArea &&
            viewFrame.minY <= rootVC.view.bounds.height - bottomSafeArea
    }
}

extension WEInlineWidget : WECampaignControlInternalCallback{
    public func onControlGroupTriggered(propertyID: Int) {
        self.monitorVisibilityAndFireEvent()
    }
}

