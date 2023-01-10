package com.webengage.sdk.android.actions.render;


import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.utils.WebEngageConstant;
import org.json.JSONObject;
import java.lang.ref.WeakReference;

class WebViewRenderer {
    WebView webView;
    String baseUrl;
    String mimeType;
    String encoding;
    JSBridge jsInterface;
    WeakReference<Activity> activityWeakReference;
    private String baseData1 = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <meta charset='UTF-8'>\n" +
            "    <title>Full Page Modal In-App</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "  </head>\n" +
            "  <body style = \"background-color: transparent\">\n" +
            "      <script type='text/javascript'>\n" +
            "        \n" +
            "        var webengage = {};\n" +
            "\n" +
            "        webengage.notification = {\n" +
            "\t       \n" +
            "         'x' : 1\n" +
            "        \n" +
            "        };\n" +
            "\n" +
            "        var instance = {\n" +
            "\n" +
            "          layoutId : WebEngage.getLayoutId(),\n" +
            "\n previewJson : true ," +
            "          baseURL: \"";
    private String baseData2 = "\",\n" +
            "\n" +
            "\t        data : JSON.parse(WebEngage.getData()),\n" +
            "\n" +
            "\t        click : function (actionId,actionLink,isPrime) {\n" +
            "\t           WebEngage.onClick(actionId,actionLink,isPrime);\n" +
            "\t        },\n" +
            "\n" +
            "          close : function (){\n" +
            "             WebEngage.onClose();\n" +
            "          },\n" +
            "\n" +
            "          open : function(){\n" +
            "             WebEngage.onOpen();\n" +
            "          },\n" +
            "\n" +
            "          error : function(stackTrace){\n" +
            "             WebEngage.onError(stackTrace);\n" +
            "          }\n" +
            "\n" +
            "        };\n" +
            "      </script>\n" +
            "      <script type='text/javascript' src='";
    private String baseData3 = "js/notification-prepare.js'></script>\n" +
            "      <script type='text/javascript'>\n" +
            "       var notificationInstance = webengage.notification.prepare(instance);\n" +
            "       notificationInstance.show();\n" +
            "      </script>\n" +
            "  </body>\n" +
            "</html>\n";
    private String webViewBaseData = null;

    WebViewRenderer(String baseUrl, String mimeType, String encoding, JSBridge jsInterface, Activity activity, JSONObject layoutAttributes) {
        this.baseUrl = baseUrl;
        this.mimeType = mimeType;
        this.encoding = encoding;
        this.jsInterface = jsInterface;
        this.activityWeakReference = new WeakReference<Activity>(activity);
        webView = new WebView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        webView.setLayoutParams(layoutParams);
        this.webViewBaseData = baseData1 + baseUrl + baseData2 + baseUrl + baseData3;
    }

    WebView initWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
        //This will follow the cache-control policy.
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.addJavascriptInterface(this.jsInterface, "WebEngage");
        if (BuildConfig.DEBUG) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        webView.setWebChromeClient(new WebViewChromeClientHelper());
        webView.loadDataWithBaseURL(baseUrl, this.webViewBaseData, this.mimeType, this.encoding, null);
        webView.setBackgroundColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= 14) {
            webView.getSettings().setTextZoom(100);
        }
        return webView;
    }

    private class WebViewChromeClientHelper extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Logger.d(WebEngageConstant.TAG, "onConsoleMessage: " + consoleMessage.message());
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }
    }
}
