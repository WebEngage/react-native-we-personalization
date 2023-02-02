
import Foundation
import UIKit
import React
import WEPersonalization


public class WEHInlineView:UIView{
    var campaignData: WEGCampaignData? = nil
    let TAG = "WebEngage-Hybrid"
    @objc var screenName: String = "test-screen"
    @objc var propertyId: String = "" {
        didSet {
            print(TAG+"Inside propertyId-- screen-"+screenName+" \n prop-"+propertyId)
        }
    }

    @objc var color: String = "" {
        didSet {
            print(TAG+"Inside color did set screen-"+screenName+" \n prop-"+propertyId)
            //          self.backgroundColor = hexStringToUIColor(hexColor: color)
        }
    }



    override init(frame: CGRect) {
        super.init(frame: frame)
        setupView()

    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupView()
    }

    private func setupView(){
        NSLog("WEP: Inside setupView")
        // TODO - Below code will send data to listener of react-native
        PersonalizationBridge.emitter.sendEvent(withName: "testAk", body: "body")
        var inlineView = WEInlineView(frame: CGRect(x: 0, y: 0, width: 300, height: 40))
        inlineView.tag = 12

        DispatchQueue.main.asyncAfter(deadline: .now()+1, execute: {
            inlineView.load(tag: 12, callbacks: self)
            if let scrollview = self.getScrollview(view: self){
                scrollview.addObserver(self, forKeyPath: #keyPath(UIScrollView.contentOffset), options: [.old, .new], context: nil)
            }else{
                print("WEP: Scrollview not found")
            }
        })

        //        WEPropertyRegistry.shared.register(callback: self, forTag: 13)
        //        addSubview(headerView)
        addSubview(inlineView)


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
        print("WEP : onRendered ")

    }
    public func onDataReceived(_ data: WEGCampaignData) {
        self.campaignData = data;
        print("WEP : onDataReceived")
    }
    public func onPlaceholderException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WEP : onPlaceholderException")
    }
}

extension WEHInlineView:WECampaignCallback{
    public func onCampaignPrepared(_ data: WEGCampaignData) -> WEGCampaignData {
        print("WEP : onCampaignPrepared")
        return data
    }
    public func onCampaignShown(data: WEGCampaignData) {
        print("WEP : onCampaignShown")
    }
    public func onCampaignException(_ campaignId: String?, _ targetViewId: String, _ exception: Error) {
        print("WEP : onCampaignException")
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
            print("WER above isVisibleToUser @@@@")
            if self.isVisibleToUser == true{
                print("WER screen is visible in the port @@@@")
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
