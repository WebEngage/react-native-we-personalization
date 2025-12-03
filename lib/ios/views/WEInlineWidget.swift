
import Foundation
import UIKit
import React
import WEPersonalization
public class WEInlineWidget: UIView{
    var inlineView: WEInlineView? = nil
    var campaignData: WECampaignData? = nil
    @objc var width: CGFloat = 0.1 {
        didSet {
            setupView()
        }
    }
    
    @objc var height: CGFloat = 0.1 {
        didSet {
            setupView()
        }
    }
    
    @objc public var screenName: String = ""{
        didSet {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: Initialization for screenName- \(screenName)")
            self.setupView()
        }
    }
    
    @objc var propertyId: Int = 0 {
        didSet {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: Initialization for propertyId - \(propertyId)")
            self.setupView()
        }
    }
    
    /**
     * Updates the widget properties from the view manager
     * Converts string propertyId to int for internal use
     */
    @objc public func updateProperties(_ propertyId: String, screenName: String) {
        NSLog("WEPersonalization: WEInlineWidget: updateProperties: propertyId=%@, screenName=%@", propertyId, screenName)
        if let propertyIdInt = Int(propertyId) {
            self.propertyId = propertyIdInt
        }
        self.screenName = screenName
    }
    
    @objc func reloadViews(){
        WELogger.d(WEConstants.TAG+"WEP:  WEInlineWidget: reloadView called for \(self.propertyId)")
        DispatchQueue.main.async {
            if let viewToreload = self.inlineView,
               viewToreload.superview != nil{
                self.inlineView?.load(tag: self.propertyId, callbacks: self)
            }
        }
    }
    
    override init(frame: CGRect) {
        NSLog("WEPersonalization: WEInlineWidget: init: frame=%@", NSCoder.string(for: frame))
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: init called for inlineWidget")
        super.init(frame: frame)
        NotificationCenter.default.addObserver(self, selector: #selector(reloadViews), name: Notification.Name(WEConstants.SCREEN_NAVIGATED), object: nil)
    }
    
    public override func layoutSubviews() {
        super.layoutSubviews()
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: layoutSubviews - bounds: \(self.bounds)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: layoutSubviews - current width: \(self.width), height: \(self.height)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: layoutSubviews - propertyId: \(self.propertyId), screenName: \(self.screenName)")
        if self.width != self.bounds.width || self.height != self.bounds.height {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: layoutSubviews - Updating dimensions from \(self.width)x\(self.height) to \(self.bounds.width)x\(self.bounds.height)")
            self.width = self.bounds.width
            self.height = self.bounds.height
        }
    }
    
    deinit{
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: deInit called for \(self.propertyId)")
        NotificationCenter.default.removeObserver(self)
        if let scrollview = self.getScrollview(view: self){
            scrollview.removeObserver(self, forKeyPath:  #keyPath(UIScrollView.contentOffset))
        }
        WEPersonalization.shared.unregisterWEPlaceholderCallback(self.propertyId)
        WEPersonalization.shared.deRegisterCampaignControlGroupCallback(tag: self.propertyId)
    }
    
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }
    
    
    private func setupView(){
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - width: \(self.width), height: \(self.height), propertyId: \(self.propertyId)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - bounds: \(self.bounds)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - frame: \(self.frame)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - screenName: \(self.screenName)")
        
        if(self.height > 0.1 && self.width > 0.1 && propertyId != 0) {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - Conditions met, creating inlineView")
            inlineView?.removeFromSuperview()
            inlineView = WEInlineView(frame: CGRect(x: 0, y: 0, width: self.width, height: self.height))
            inlineView?.tag = self.propertyId
            if(self.propertyId != 0) {
                WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: LoadView called for - \(self.propertyId)")
                WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: Calling load with tag: \(self.propertyId)")
                inlineView?.load(tag: self.propertyId, callbacks: self)
                monitorVisibilityAndFireEvent()
            }
            addSubview(inlineView!)
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: inlineView added to subview")
        } else {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: setupView - Conditions NOT met - height: \(self.height > 0.1), width: \(self.width > 0.1), propertyId: \(propertyId != 0)")
        }
    }
    
    func fireCGEvent(){
        WEPersonalization.shared.trackCGEvent(forPropertyId: inlineView!.tag)
        WEPersonalization.shared.registerCampaignControlGroupCallback(tag: inlineView!.tag, callback: self)
    }
    
    private var observerContextCG = 0
    
    func monitorVisibilityAndFireEvent(){
            // for cg
            if(self.isVisibleToUser){
                fireCGEvent()
            }else{
                if let scrollview = self.getScrollview(view: self){
                    scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: &observerContextCG)
                }
                
            }
        }
}


extension WEInlineWidget : WEPlaceholderCallback{
    public func onRendered(data: WECampaignData) {
        self.monitorVisibilityAndFireEvent();
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onRendered \(self.propertyId)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onRendered - campaignId: \(data.campaignId ?? "nil"), targetViewTag: \(data.targetViewTag)")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEConstants.PAYLOAD: data.toJSONString() ?? ""]
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: WEConstants.METHOD_NAME_ON_RENDERED, body: campaignData)
        if(self.isVisibleToUser) {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onRendered - View is visible, tracking impression")
            data.trackImpression(attributes: nil)
        } else {
            WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onRendered - View not visible, adding scroll observer")
            if let scrollview = self.getScrollview(view: self){
                scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: nil)
            }
        }
        
    }
    public func onDataReceived(_ data: WECampaignData) {
        self.campaignData = data;
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onDataReceived \(self.propertyId)")
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onDataReceived - campaignId: \(data.campaignId ?? "nil"), targetViewTag: \(data.targetViewTag)")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEConstants.PAYLOAD: data.toJSONString() ?? ""]
        
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: WEConstants.METHOD_NAME_ON_DATA_RECEIVED, body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        WELogger.d(WEConstants.TAG+"WEP: WEInlineWidget: onPlaceholderException \(self.propertyId)")
        let campaignData: [String: Any] = [WEConstants.PAYLOAD_TARGET_VIEW_ID: targetViewId, WEConstants.PAYLOAD_CAMPAIGN_ID: campaignId ?? "", WEConstants.EXCEPTION: exception.localizedDescription]
        WEPersonalizationBridgeImpl.emitter.sendEvent(withName: WEConstants.METHOD_NAME_ON_PLACEHOLDER_EXCEPTION, body: campaignData)
    }
}

extension WEInlineWidget {
    func getScrollview(view:UIView)->UIScrollView?{
        if let superview = view.superview, superview is UIScrollView{
            return view.superview as? UIScrollView
        }
        return view.superview == nil ? nil : getScrollview(view: view.superview!)
    }
    
    public override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == #keyPath(UIScrollView.contentOffset) {
            if self.isVisibleToUser == true{
                if let scrollview = self.getScrollview(view: self){
                    // removes observer added to scrollview
                    scrollview.removeObserver(self, forKeyPath:  #keyPath(UIScrollView.contentOffset))
                    if context == &self.observerContextCG {
                        fireCGEvent()
                    }else if let data = self.campaignData{
                        data.trackImpression(attributes: nil)
                    }
                }
            }
        }
    }
}

extension UIView{
    var isVisibleToUser: Bool {
        
        if isHidden || alpha == 0 || superview == nil || window == nil {
            return false
        }
        
        guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
            return false
        }
        
        let viewFrame = convert(bounds, to: rootViewController.view)
        
        let topSafeArea: CGFloat
        let bottomSafeArea: CGFloat
        
        if #available(iOS 11.0, *) {
            topSafeArea = rootViewController.view.safeAreaInsets.top
            bottomSafeArea = rootViewController.view.safeAreaInsets.bottom
        } else {
            topSafeArea = rootViewController.topLayoutGuide.length
            bottomSafeArea = rootViewController.bottomLayoutGuide.length
        }
        
        return viewFrame.maxX >= 0 &&
        viewFrame.minX <= rootViewController.view.bounds.width &&
        viewFrame.maxY >= topSafeArea &&
        viewFrame.minY <= rootViewController.view.bounds.height - bottomSafeArea
    }
    
       
}

extension WEInlineWidget : WECampaignControlInternalCallback{
    public func onControlGroupTriggered(propertyID: Int) {
        self.monitorVisibilityAndFireEvent()
    }
}

