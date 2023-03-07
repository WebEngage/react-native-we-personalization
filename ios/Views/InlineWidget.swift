
import Foundation
import UIKit
import React
import WEPersonalization


public class WEHInlineView: UIView{
    var inlineView: WEInlineView? = nil
    var campaignData: WEGCampaignData? = nil
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


    @objc var screenName: String = ""{
        didSet {
            print(WEGConstants.TAG+" Initialization for screenName- \(screenName)")
            self.setupView()
        }
    }

    @objc var propertyId: Int = 0 {
        didSet {
            print(WEGConstants.TAG+" Initialization for propertyId - \(propertyId)")
            self.setupView()
        }
    }

    @objc func reloadViews(){
        print("WERL reloadView called for \(self.propertyId)")
        DispatchQueue.main.async {
            if let viewToreload = self.inlineView,
                  viewToreload.superview != nil{
                self.inlineView?.load(tag: self.propertyId, callbacks: self)
                }
            }

    }
    override init(frame: CGRect) {
        print(WEGConstants.TAG+"WER: init called for inlineWidget")
        super.init(frame: frame)
        setupView()
        NotificationCenter.default.addObserver(self, selector: #selector(reloadViews), name: Notification.Name(WEGConstants.SCREEN_NAVIGATED), object: nil)

    }

    deinit{
        print(WEGConstants.TAG+"WER2: deInit called for \(self.propertyId)")
           NotificationCenter.default.removeObserver(self)
        if let scrollview = self.getScrollview(view: self){
            scrollview.removeObserver(self, forKeyPath:  #keyPath(UIScrollView.contentOffset))
        }
        WEPersonalization.shared.unregisterWEPlaceholderCallback(self.propertyId)
    }


    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }


    private func setupView(){
        if(self.height > 0.1 && self.width > 0.1 && propertyId != 0) {
            inlineView = WEInlineView(frame: CGRect(x: 0, y: 0, width: self.width, height: self.height))
            inlineView?.tag = self.propertyId
            if(self.propertyId != 0) {
                print(WEGConstants.TAG+" WERL: LoadView called for - \(self.propertyId)")
//                inlineView?.tag = self.propertyId
                 inlineView?.load(tag: self.propertyId, callbacks: self)
//                PlaceHolderCallbackHandler
            }
//            print(WEGConstants.TAG+" Yay! Adding subview for - \(self.propertyId)")
            addSubview(inlineView!)
       }
    }
}


extension WEHInlineView : WEPlaceholderCallback{
    public func onRendered(data: WEGCampaignData) {
        print(WEGConstants.TAG+" WERP : onRendered \(self.propertyId)")
        let campaignData: [String: Any] = [WEGConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEGConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId ?? "", WEGConstants.PAYLOAD: data.toJSONString()]
        PersonalizationBridge.emitter.sendEvent(withName: WEGConstants.METHOD_NAME_ON_RENDERED, body: campaignData)
        if(self.isVisibleToUser) {
            data.trackImpression(attributes: nil)
        } else {
            if let scrollview = self.getScrollview(view: self){
                scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: nil)
                print(WEGConstants.TAG+" WERS: Scrollview  found")
            }else{
                print(WEGConstants.TAG+" WERS: Scrollview not found")
            }
        }

    }
    public func onDataReceived(_ data: WEGCampaignData) {
        self.campaignData = data;
        print(WEGConstants.TAG+" WERP : onDataReceived \(self.propertyId)")
        let campaignData: [String: Any] = [WEGConstants.PAYLOAD_TARGET_VIEW_ID: data.targetViewTag, WEGConstants.PAYLOAD_CAMPAIGN_ID: data.campaignId, WEGConstants.PAYLOAD: data.toJSONString()]

        PersonalizationBridge.emitter.sendEvent(withName: WEGConstants.METHOD_NAME_ON_DATA_RECEIVED, body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: Int, _ exception: Error) {
        print(WEGConstants.TAG+" WERP : onPlaceholderException \(self.propertyId)")
        let campaignData: [String: Any] = [WEGConstants.PAYLOAD_TARGET_VIEW_ID: targetViewId, WEGConstants.PAYLOAD_CAMPAIGN_ID: campaignId ?? "", WEGConstants.EXCEPTION: exception]
        PersonalizationBridge.emitter.sendEvent(withName: WEGConstants.METHOD_NAME_ON_PLACEHOLDER_EXCEPTION, body: campaignData)
    }
}

extension WEHInlineView {
    func getScrollview(view:UIView)->UIScrollView?{
        if let superview = view.superview, superview is UIScrollView{
            return view.superview as? UIScrollView
        }
        return view.superview == nil ? nil : getScrollview(view: view.superview!)
    }

    public override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        if keyPath == #keyPath(UIScrollView.contentOffset) {
            print(WEGConstants.TAG+" WERP above isVisibleToUser @@@@")
            if self.isVisibleToUser == true{
                print(WEGConstants.TAG+" WERP screen is visible in the port @@@@")
                if let scrollview = self.getScrollview(view: self){
                    // remove observer added to scrollview
                    scrollview.removeObserver(self, forKeyPath:  #keyPath(UIScrollView.contentOffset))
                    
                    if let data = self.campaignData{
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

