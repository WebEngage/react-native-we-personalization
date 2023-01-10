package com.webengage.sdk.android.actions.render;


import android.content.Context;
import android.webkit.JavascriptInterface;

class JSBridge {
    InAppNotificationData inAppNotificationData;
    RenderDialogFragment renderDialogFragment;
    Context applicationContext;

    JSBridge(InAppNotificationData inAppNotificationData, RenderDialogFragment renderDialogFragment) {
        this.inAppNotificationData = inAppNotificationData;
        this.renderDialogFragment = renderDialogFragment;
        this.applicationContext = renderDialogFragment.getActivity().getApplicationContext();
    }

    @JavascriptInterface
    public String getLayoutId() {
        return inAppNotificationData.getLayoutId();
    }

    @JavascriptInterface
    public String getData() {
        return inAppNotificationData.getData().toString();

    }

    @JavascriptInterface
    public void onClick(String actionId, String actionLink, boolean isPrime) {
        renderDialogFragment.handleClick(actionId, actionLink, isPrime);
    }

    @JavascriptInterface
    public void onClose() {
        renderDialogFragment.handleClose();
    }

    @JavascriptInterface
    public void onOpen() {
        try {
            renderDialogFragment.handleOpen();
        } catch (Exception e) {

        }
    }

    @JavascriptInterface
    public void onError(String stackTrace) {
        renderDialogFragment.handleError(stackTrace);
    }


}
