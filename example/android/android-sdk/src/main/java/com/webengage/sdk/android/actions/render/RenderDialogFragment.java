package com.webengage.sdk.android.actions.render;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.webengage.sdk.android.BuildConfig;
import com.webengage.sdk.android.CallbackDispatcher;
import com.webengage.sdk.android.EventFactory;
import com.webengage.sdk.android.EventName;
import com.webengage.sdk.android.IntentFactory;
import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.NotificationClickHandlerService;
import com.webengage.sdk.android.Topic;
import com.webengage.sdk.android.WebEngage;
import com.webengage.sdk.android.WebEngageReceiver;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.exception.WebViewException;
import com.webengage.sdk.android.actions.rules.ConfigurationManager;
import com.webengage.sdk.android.actions.rules.RuleExecutionAction;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.utils.ManifestUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RenderDialogFragment extends DialogFragment {
    private RenderDialog renderDialog = null;
    private JSONObject layoutAttributes = null;
    private boolean fullScreen = false;
    private String baseUrl = "";
    private InAppNotificationData inAppNotificationData = null;
    private RelativeLayout rootLayout = null;
    private Animation entryAnimation = null;
    private Animation exitAnimation = null;
    private Handler handler = null;
    private int CLICK = 0;
    private int CLOSE = 1;
    private int ERROR = 2;
    private int OPEN = 3;
    private int ACTION = -1;
    private String clickActionId = "";
    private String clickActionLink = "";
    private boolean isPrimeClicked = false;
    private String errorStackTrace = "";
    private Context applicationContext = null;

    private boolean shouldRender() {
        if (!performBaseChecks()) {
            return false;
        }

        if (!RuleExecutorFactory.getRuleExecutor().evaluateRule(this.inAppNotificationData.getExperimentId(), WebEngageConstant.RuleCategory.PAGE_RULE)) {
            return false;
        }

        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    private boolean performBaseChecks() {
        try {
            String experimentId = this.inAppNotificationData.getExperimentId();

            ConfigurationManager configurationManager = new ConfigurationManager(this.applicationContext);
            Map<String, Object> entityObj = configurationManager.getEntityObj(experimentId, WebEngageConstant.Entity.NOTIFICATION);

            Map<String, Object> variationObj = configurationManager.getEntityVariationObj(this.inAppNotificationData.getVariationId(), entityObj);

            return RuleExecutionAction.performBaseCheck(entityObj, variationObj, WebEngageConstant.Entity.NOTIFICATION);
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while performing in-app base checks", e);
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Logger.d("RenderDialogFragment", "Attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Logger.d("RenderDialogFragment", "Detach");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Logger.d("RenderDialogFragment", "CreateDialog");
        return renderDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.d("RenderDialogFragment", "Start");
        DataHolder.get().setEntityRunningState(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Logger.d("RenderDialogFragment", "Stop");
    }

    @Override
    public void onPause() {
        super.onPause();
        DataHolder.get().setEntityRunningState(false);
        Logger.d("RenderDialogFragment", "Pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.d("RenderDialogFragment", "Resume");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("RenderDialogFragment", "Destroy");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.d("RenderDialogFragment", "CreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d("RenderDialogFragment", "Create");
        super.onCreate(savedInstanceState);
        try {
            this.applicationContext = getActivity().getApplicationContext();
            Bundle bundle = getArguments();
            handler = new Handler(Looper.myLooper());
            bundle.setClassLoader(InAppNotificationData.class.getClassLoader());
            inAppNotificationData = bundle.getParcelable("notificationData");
            layoutAttributes = inAppNotificationData.getData().optJSONObject("layoutAttributes");
            baseUrl = bundle.getString("baseUrl");
            fullScreen = bundle.getBoolean("fullscreen", false);
            entryAnimation = AnimationFactory.newAnimation(layoutAttributes.optString("entryAnimation", AnimationFactory.FADE_IN), new EntryAnimationListener(), layoutAttributes.optInt("animDuration", 1000));
            exitAnimation = AnimationFactory.newAnimation(layoutAttributes.optString("exitAnimation", AnimationFactory.FADE_OUT), new ExitAnimationListener(), layoutAttributes.optInt("animDuration", 1000));
            renderDialog = new RenderDialog(this.getActivity(), android.R.style.Theme);
        } catch (Exception e) {

        }

    }

    @Override
    public void onDestroyView() {
        Logger.d("RenderDialogFragment", "DestroyView");
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Logger.d("RenderDialogFragment", "Configuration Changed");
        resizeWindow(renderDialog.getWindow(), newConfig.orientation);
    }

    private void resizeWindow(Window window, int orientation) {
        boolean allowLandscape = layoutAttributes.optBoolean("allowLandscape", false);
        boolean allowPortrait = layoutAttributes.optBoolean("allowPortrait", false);

        if (layoutAttributes.isNull("allowPortrait")) {
            // old implementation
            if (!allowLandscape) {
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //happens only if background activity is configured with configChanges
                    window.getDecorView().setVisibility(View.GONE);
                    return;

                } else {
                    window.getDecorView().setVisibility(View.VISIBLE);

                }
            }
        } else {
            if (!(allowLandscape && allowPortrait)) {
                if (allowPortrait) {
                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        window.getDecorView().setVisibility(View.GONE);
                        return;
                    } else {
                        window.getDecorView().setVisibility(View.VISIBLE);
                    }
                }

                if (allowLandscape) {
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        window.getDecorView().setVisibility(View.GONE);
                        return;
                    } else {
                        window.getDecorView().setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Rect rect2 = new Rect();
        getActivity().getWindow().peekDecorView().getWindowVisibleDisplayFrame(rect2);
        float statusBar = rect2.top;
        float navBar = getActivity().getWindow().peekDecorView().getMeasuredHeight() - rect2.bottom;

        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;

        double x = 0;
        double y = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            statusBar = getActivity().getWindowManager().getCurrentWindowMetrics().getWindowInsets().getInsets(WindowInsets.Type.statusBars()).top;
            navBar = getActivity().getWindowManager().getCurrentWindowMetrics().getWindowInsets().getInsets(WindowInsets.Type.navigationBars()).bottom;

            float availableHeight = getActivity().getWindowManager().getCurrentWindowMetrics().getBounds().height();
            height = availableHeight - statusBar - navBar;
        }

        if (displayMetrics.widthPixels / displayMetrics.density < layoutAttributes.optInt("responsiveThreshold", 500)) {
            Logger.d("RenderDialogFragment", "Portrait");
            JSONObject portrait = layoutAttributes.optJSONObject("portrait");
            if (portrait != null) {
                int widthInDp = portrait.optInt("width", 0);
                width = widthInDp != 0 ? (int) (widthInDp * displayMetrics.density) : width;

                int heightInDp = portrait.optInt("height", 0);
                height = heightInDp != 0 ? (int) (heightInDp * displayMetrics.density + layoutAttributes.optInt("logoHeight", 0) * displayMetrics.density) : height;
            }
        } else {
            Logger.d("RenderDialogFragment", "Landscape");
            JSONObject landscape = layoutAttributes.optJSONObject("landscape");
            if (landscape != null) {
                int widthInDp = landscape.optInt("width", 0);
                width = widthInDp != 0 ? (int) (widthInDp * displayMetrics.density) : width;

                int heightInDp = landscape.optInt("height", 0);
                height = heightInDp != 0 ? (int) (heightInDp * displayMetrics.density + layoutAttributes.optInt("logoHeight", 0) * displayMetrics.density) : height;
            }
        }

        InAppNotificationData.InAppType inAppType = inAppNotificationData.getInAppType();

        if (!fullScreen) {
            if (statusBar == 0) {
                final Resources resources = this.getActivity().getApplicationContext().getResources();
                final int resourceIdStatusBar = resources.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceIdStatusBar > 0) {
                    statusBar = resources.getDimensionPixelSize(resourceIdStatusBar);
                    Logger.d("RenderDialogFragment", "statusBar by resource = " + statusBar);
                } else {
                    statusBar = (int) Math.ceil((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? 24 : 25) * resources.getDisplayMetrics().density);
                    Logger.d("RenderDialogFragment", "statusBar by constant = " + statusBar);
                }
            }
        }


        switch (inAppType) {

            case HEADER:
            case FOOTER:
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                // getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);//passes touch event to siblings,on pressing back, activity gets dismissed
                break;
        }

        /**
         * For Android 11 and up , the navigation bar is to be considered
         * in the calculation of the offset.
         **/

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            navBar = 0;
        if (inAppType.equals(InAppNotificationData.InAppType.FOOTER)) {
            y = rect2.centerY() - (statusBar / 2) - height / 2 - navBar / 2;

        } else {
            y = -rect2.centerY() + (statusBar / 2) + statusBar + height / 2 - navBar / 2;
        }

        window.setLayout((int) (width), (int) (height));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.x = (int) Math.ceil(x);
        attributes.y = (int) Math.ceil(y);

        window.setAttributes(attributes);

    }

    final class RenderDialog extends Dialog {
        private JSBridge jsBridge = null;

        public RenderDialog(Context context, int themeResId) {
            super(context, themeResId);
            try {
                requestWindowFeature(Window.FEATURE_NO_TITLE);
                getWindow().setBackgroundDrawable(new ColorDrawable(0));
                //getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
                if (fullScreen) {
                    getWindow().setFlags(1024, 1024);
                }
                rootLayout = new RelativeLayout(this.getContext());
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                rootLayout.setLayoutParams(layoutParams);

                jsBridge = new JSBridge(inAppNotificationData, RenderDialogFragment.this);
                resizeWindow(getWindow(), getActivity().getResources().getConfiguration().orientation);
                if (entryAnimation != null) {
                    getWindow().getDecorView().setVisibility(View.GONE);
                }

                WebViewRenderer webViewRenderer = new WebViewRenderer(baseUrl, "text/html", "UTF-8",
                        jsBridge, getActivity(), layoutAttributes);
                WebView webView = webViewRenderer.initWebView();
                webView.setTag(WebEngageConstant.TAG_WE_WEB_VIEW);

                rootLayout.addView(webView);
                //Disabling WebView touch to prevent accidental clicks even before the view event has been fired.
                webView.setOnTouchListener((v, event) -> true);
                this.setContentView(rootLayout);
            } catch (Exception e) {
                Logger.e("ExceptionDialog", e.toString());
                Intent intent = IntentFactory.newIntent(Topic.EXCEPTION, new WebViewException(e.getMessage()), getActivity().getApplicationContext());
                WebEngage.startService(intent, getActivity().getApplicationContext());
            }
        }


        @Override
        public void onBackPressed() {
            handleClose();
        }
    }


    void startEntryAnimation() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    renderDialog.getWindow().getDecorView().setVisibility(View.VISIBLE);
                    renderDialog.getWindow().getDecorView().setEnabled(false);
                    if (entryAnimation != null) {
                        if (!entryAnimation.hasStarted()) {
                            rootLayout.startAnimation(entryAnimation);
                        }
                    } else {
                        logEvent();
                    }
                } catch (Exception e) {

                }
            }
        });

    }


    void startExitAnimation() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (renderDialog != null && renderDialog.getWindow() != null && renderDialog.getWindow().getDecorView() != null) {
                        if (renderDialog.getWindow().getDecorView().getVisibility() == View.GONE) {
                            dismissInApp();
                        } else {
                            if (exitAnimation != null) {
                                if (!exitAnimation.hasStarted()) {
                                    rootLayout.startAnimation(exitAnimation);
                                }
                            } else {
                                if (Build.VERSION.SDK_INT >= 12) {
                                    dismissAllowingStateLoss();
                                } else {
                                    dismiss();
                                }
                                logEvent();
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Render", e.toString());
                }
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void logEvent() {
        switch (this.ACTION) {
            case 0://CLICK
                DataHolder.get().setEntityRunningState(false);
                Map<String, Object> systemData = new HashMap<String, Object>();
                systemData.put(WebEngageConstant.NOTIFICATION_ID, inAppNotificationData.getVariationId());
                systemData.put(WebEngageConstant.CTA_ID, this.clickActionId);
                systemData.put(WebEngageConstant.EXPERIMENT_ID, inAppNotificationData.getExperimentId());
                Intent notificationClickEvent = IntentFactory.newIntent(Topic.EVENT,
                        EventFactory.newSystemEvent(EventName.NOTIFICATION_CLICK, systemData,
                                null, null, applicationContext), applicationContext);
                WebEngage.startService(notificationClickEvent, applicationContext);
                boolean isClickHandledByClient = CallbackDispatcher.init(this.applicationContext).onInAppNotificationClicked(this.applicationContext, inAppNotificationData, this.clickActionId);
                if (this.clickActionLink != null && !this.clickActionLink.isEmpty() && !"null".equals(this.clickActionLink) && !isClickHandledByClient) {
                    Intent intent = new Intent(this.applicationContext, NotificationClickHandlerService.class);
                    intent.setAction(WebEngageReceiver.WEBENGAGE_ACTION);
                    Bundle bundle = new Bundle();
                    bundle.putString(WebEngageReceiver.ACTION, NotificationClickHandlerService.DEEPLINK_ACTION);
                    bundle.putString(WebEngageConstant.URI, this.clickActionLink);
                    intent.putExtras(bundle);
                    this.applicationContext.startService(intent);
                }
                break;

            case 1://CLOSE
                DataHolder.get().setEntityRunningState(false);
                systemData = new HashMap<String, Object>();
                systemData.put(WebEngageConstant.NOTIFICATION_ID, inAppNotificationData.getVariationId());
                systemData.put(WebEngageConstant.EXPERIMENT_ID, inAppNotificationData.getExperimentId());
                Intent notificationCloseEvent = IntentFactory.newIntent(Topic.EVENT, EventFactory.newSystemEvent(EventName.NOTIFICATION_CLOSE, systemData, null, null, applicationContext), applicationContext);
                WebEngage.startService(notificationCloseEvent, applicationContext);
                CallbackDispatcher.init(this.applicationContext).onInAppNotificationDismissed(this.applicationContext, inAppNotificationData);
                break;

            case 2://ERROR
                DataHolder.get().setEntityRunningState(false);
                WebViewException webViewException = new WebViewException(this.errorStackTrace);
                Intent intent = IntentFactory.newIntent(Topic.EXCEPTION, webViewException, this.applicationContext);
                WebEngage.startService(intent, applicationContext);
                break;

            case 3://OPEN
                //Enabling Webview post the view event been fired
                WebView webView = (WebView) rootLayout.findViewWithTag(WebEngageConstant.TAG_WE_WEB_VIEW);
                if (null != webView) {
                    webView.setOnTouchListener((v, event) -> false);
                }
                if (ManifestUtils.checkPermission(ManifestUtils.VIBRATE, applicationContext)) {
                    Vibrator vibrator = (Vibrator) applicationContext.getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(20);
                    }
                }
                systemData = new HashMap<String, Object>();
                systemData.put(WebEngageConstant.EXPERIMENT_ID, inAppNotificationData.getExperimentId());
                systemData.put(WebEngageConstant.NOTIFICATION_ID, inAppNotificationData.getVariationId());
                Intent notificationViewIntent = IntentFactory.newIntent(Topic.EVENT,
                        EventFactory.newSystemEvent(EventName.NOTIFICATION_VIEW, systemData,
                                null, null, applicationContext), applicationContext);
                WebEngage.startService(notificationViewIntent, applicationContext);
                CallbackDispatcher.init(this.applicationContext).onInAppNotificationShown(this.applicationContext, inAppNotificationData);
                break;
        }
    }

    void handleClick(String actionId, String actionLink, boolean isPrime) {
        this.ACTION = CLICK;
        this.clickActionId = actionId;
        this.clickActionLink = actionLink;
        this.isPrimeClicked = isPrime;
        startExitAnimation();
    }

    void handleClose() {
        this.ACTION = CLOSE;
        startExitAnimation();
    }

    void handleOpen() {
        this.ACTION = OPEN;
        if (shouldRender()) {
            startEntryAnimation();
        } else {
            dismissInApp();
        }
    }

    void handleError(String stackTrace) {
        this.ACTION = ERROR;
        this.errorStackTrace = stackTrace;
        startExitAnimation();
    }

    private final class EntryAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                logEvent();
            } catch (Exception e) {

            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private final class ExitAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            try {
                if (Build.VERSION.SDK_INT >= 12) {
                    dismissAllowingStateLoss();
                } else {
                    dismiss();
                }
                logEvent();
            } catch (Exception e) {

            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private void dismissInApp() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                dismissAllowingStateLoss();
            } else {
                dismiss();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Logger.e(WebEngageConstant.TAG, "Exception while dismissing in-app fragment", e);
            }
        }
        DataHolder.get().setEntityRunningState(false);
    }

}
