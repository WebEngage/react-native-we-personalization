package com.webengage.sdk.android.actions.deeplink;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.webengage.sdk.android.Action;
import com.webengage.sdk.android.actions.render.CallToAction;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;
import java.util.Map;

class DeepLinkAction extends Action {

    private Context applicationContext = null;
    Bundle extras = null;
    CallToAction.TYPE type = null;
    boolean launchAppIfInvalid = false;

    DeepLinkAction(Context context) {
        super(context);
        applicationContext = context.getApplicationContext();
    }

    @Override
    protected Object preExecute(Map<String, Object> actionAttributes) {
        Intent intent = (Intent) actionAttributes.get(DeepLinkActionController.ACTION_DATA);
        extras = intent.getExtras();
        if (extras != null) {
            launchAppIfInvalid = extras.getBoolean(WebEngageConstant.LAUNCH_APP_IF_INVALID, false);
            String uri = extras.getString(WebEngageConstant.URI);
            if (uri != null) {
                List<String> params = null;
                try {
                    params = Uri.parse(uri).getPathSegments();
                } catch (Exception e) {

                }
                if (params != null) {
                    if (params.size() > 0) {
                        this.type = CallToAction.TYPE.valueFromString(params.get(0));
                    }
                    if (this.type != null && params.size() > 1) {
                        return params.get(1);
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Object execute(Object data) {
        if (data == null) {
            if (launchAppIfInvalid) {
                launchApp();
            }
        } else {
            if (this.type != null) {
                switch (this.type) {
                    case LAUNCH_ACTIVITY:
                        String activityPath = (String) data;
                        String packageName = this.applicationContext.getPackageName();
                        Bundle customData = null;
                        if (extras != null) {
                            customData = extras.getBundle(WebEngageConstant.CUSTOM_DATA);
                        }

                        Intent activityIntent = new Intent();
                        if (customData != null) {
                            activityIntent.putExtras(customData);
                        }
                        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activityIntent.setClassName(packageName, activityPath);
                        if (activityIntent.resolveActivityInfo(this.applicationContext.getPackageManager(), 0) == null) {
                            if (launchAppIfInvalid) {
                                launchApp();
                            } else {
                                throw new IllegalArgumentException("Received activity path is not valid");
                            }
                        } else {
                            this.applicationContext.startActivity(activityIntent);
                        }

                        break;


                    case LINK:
                        Uri uri = Uri.parse((String) data);
                        Intent deeplinkIntent = new Intent(Intent.ACTION_VIEW, uri);
                        customData = null;
                        if (extras != null) {
                            customData = extras.getBundle(WebEngageConstant.CUSTOM_DATA);
                        }
                        if (customData != null) {
                            deeplinkIntent.putExtras(customData);
                        }
                        deeplinkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        List<ResolveInfo> resolveInfoList = this.applicationContext.getPackageManager().queryIntentActivities(deeplinkIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        if (resolveInfoList != null && !resolveInfoList.isEmpty()) {
                            for (ResolveInfo resolveInfo : resolveInfoList) {
                                if (resolveInfo != null && resolveInfo.activityInfo != null && this.applicationContext.getPackageName().equals(resolveInfo.activityInfo.packageName)) {
                                    deeplinkIntent.setPackage(this.applicationContext.getPackageName());
                                    break;
                                }
                            }
                        }
                        if (resolveInfoList != null && !resolveInfoList.isEmpty()) {
                            this.applicationContext.startActivity(deeplinkIntent);
                        } else {
                            if (launchAppIfInvalid) {
                                launchApp();
                            } else {
                                throw new IllegalArgumentException("No App can handle implicit intent with link : " + data);
                            }
                        }
                        break;

                    default:
                        if (launchAppIfInvalid) {
                            launchApp();
                        }
                        break;
                }
            } else {
                if (launchAppIfInvalid) {
                    launchApp();
                }
            }
        }
        return null;
    }

    @Override
    protected void postExecute(Object data) {

    }

    private void launchApp() {
        Intent launchIntent = this.applicationContext.getPackageManager().getLaunchIntentForPackage(this.applicationContext.getPackageName());
        if (extras != null && extras.getBundle(WebEngageConstant.CUSTOM_DATA) != null) {
            launchIntent.putExtras(extras.getBundle(WebEngageConstant.CUSTOM_DATA));
        }
        this.applicationContext.startActivity(launchIntent);
    }
}
