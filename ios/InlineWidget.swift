
import Foundation
import UIKit
import React
import WEPersonalization


public class WEHInlineView:UIView{
    var campaignData: WEGCampaignData? = nil
    let TAG = "WER:"
    @objc var width: CGFloat = 0.1 {
        didSet {
            print("inside Swifts width set  \(width)")
            setupView()
        }
    }
    @objc var height: CGFloat = 0.1 {
        didSet {
            print("inside Swifts height set  \(height)")
            setupView()
        }
    }
    @objc var screenName: String = "test-screen"{
        didSet {
            print(TAG+"Inside screenName-- screen-"+screenName)
            self.setupView()
        }
    }

    @objc var propertyId: Int = 0 {
        didSet {
            print(TAG+"Inside propertyId-- prop- \(propertyId)")
            self.setupView()
        }
    }

    @objc var color: String = "" {
        didSet {
            // print(TAG+"Inside color did set screen-"+screenName+" \n prop-"+screenName)
            //          self.backgroundColor = hexStringToUIColor(hexColor: color)
        }
    }



    override init(frame: CGRect) {
        super.init(frame: frame)
        print(TAG+" called from first init! screen-"+self.screenName+" \n prop-\(self.propertyId)")
        setupView()

    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        // print(TAG+" called from second init")
        setupView()
    }
    private func setupView(){
        //        NSLog("WER: Inside setupView")

        // TODO - Below code will send data to listener of react-native
//        PersonalizationBridge.emitter.sendEvent(withName: "testAk", body: "body")

        if(self.height > 0.1 && self.width > 0.1 && propertyId != 0) {
            print(TAG+" @@@ propertyId-\(self.propertyId) | screenName-"+self.screenName)
            print(TAG+" @@@ inside setupView height -\(self.height) | width--\(self.width)")

            var inlineView = WEInlineView(frame: CGRect(x: 0, y: 0, width: self.width, height: self.height))
            inlineView.tag = self.propertyId
            if(self.propertyId != 0) {
                inlineView.tag = self.propertyId
                    inlineView.load(tag: self.propertyId, callbacks: self)
            }


            //        WEPropertyRegistry.shared.register(callback: self, forTag: 13)
            //        addSubview(headerView)
            addSubview(inlineView)

        }


    }

    func hexStringToUIColor(hexColor: String) -> UIColor {
        let stringScanner = Scanner(string: hexColor)

        if(hexColor.hasPrefix("#")) {
            stringScanner.scanLocation = 1
        }
        var color: UInt32 = 0
        stringScanner.scanHexInt32(&color)

        let r = CGFloat(Int(color >> 16) & 0x000000FF)
        let g = CGFloat(Int(color >> 8) & 0x000000FF)
        let b = CGFloat(Int(color) & 0x000000FF)

        return UIColor(red: r / 255.0, green: g / 255.0, blue: b / 255.0, alpha: 1)
    }

    lazy var headerView: UIView = {
        // WEInlineView()
        let headerView = UIView(frame: CGRect(x: 0, y: 0, width: 300, height: 40))
        headerView.backgroundColor = UIColor(red: 22/255, green: 160/255, blue: 133/255, alpha: 0.5)
        headerView.layer.shadowColor = UIColor.gray.cgColor
        headerView.layer.shadowOffset = CGSize(width: 0, height: 10)
        headerView.layer.shadowOpacity = 1
        headerView.layer.shadowRadius = 5
        return headerView
    }()

}


extension WEHInlineView : WEPlaceholderCallback{
    public func onRendered(data: WEGCampaignData) {
        print("WERP : onRendered \(self.propertyId)")
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId]
        print("WERP : Calling onRendered for -> \(self.propertyId)")
        PersonalizationBridge.emitter.sendEvent(withName: "onRendered", body: campaignData)
        if(self.isVisibleToUser) {
            data.trackImpression(attributes: nil)
        } else {
            if let scrollview = self.getScrollview(view: self){
                scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: nil)
            }else{
                print("WER: Scrollview not found")
            }
        }

    }
    public func onDataReceived(_ data: WEGCampaignData) {
        self.campaignData = data;
        print("WERP : onDataReceived \(self.propertyId)")
//        TODO - add WEGCampaignData
        let campaignData: [String: Any] = ["targetViewId": data.targetViewTag, "campaingId": data.campaignId]

        PersonalizationBridge.emitter.sendEvent(withName: "onDataReceived", body: campaignData)
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WERP : onPlaceholderException \(self.propertyId)")
        let campaignData: [String: Any] = ["targetViewId": targetViewId, "campaingId": campaignId ?? "", "exception": exception]
        PersonalizationBridge.emitter.sendEvent(withName: "onPlaceholderException", body: campaignData)
    }
}

extension WEHInlineView:WECampaignCallback{
    public func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
        print("WERP : onCampaignPrepared \(self.propertyId)")
        return data
    }
    public func onCampaignShown(data: WEGCampaignData) {
        print("WERP : onCampaignShown \(self.propertyId)")
    }
    public func onCampaignException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WERP : onCampaignException \(self.propertyId)")
    }
    public func onCampaignClicked(actionId: String, deepLink: String, data: WEGCampaignData) -> Bool {
        //print("WEP : onCampaignClicked \(_inlineView!.isVisibleToUser)")
        return false
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
            print("WERP above isVisibleToUser @@@@")
            if self.isVisibleToUser == true{
                print("WERP screen is visible in the port @@@@")
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
