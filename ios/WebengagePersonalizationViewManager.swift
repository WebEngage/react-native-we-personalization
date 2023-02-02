
@objc(WebengagePersonalizationViewManager)
class WebengagePersonalizationViewManager: RCTViewManager {

  override func view() -> (UIView) {
      let nativeView = WEHInlineView(frame: CGRect(x: 0, y: 0, width: 300, height: 200))

      return nativeView

  }

  @objc override static func requiresMainQueueSetup() -> Bool {
    return false
  }
}

