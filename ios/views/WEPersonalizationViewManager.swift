@objc(WEPersonalizationViewManager)
class WEPersonalizationViewManager: RCTViewManager {

  override func view() -> (UIView) {
      let nativeView = WEInlineWidget(frame: CGRect(x: 0, y: 0, width: 300, height: 200))
      return nativeView
  }

  @objc override static func requiresMainQueueSetup() -> Bool {
    return false
  }
}

