package com.webengage.sdk.android;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class InstallReferrerHelper implements InstallReferrerStateListener {
    private Context context;
    private InstallReferrerClient referrerClient;

    InstallReferrerHelper(Context context) {
        this.context = context;
    }

    void fetch() {
        referrerClient = InstallReferrerClient.newBuilder(context).build();
        referrerClient.startConnection(this);
    }

    @Override
    public void onInstallReferrerSetupFinished(int responseCode) {
        switch (responseCode) {
            case InstallReferrerClient.InstallReferrerResponse.OK:
                // Connection established.
                ReferrerDetails response = null;
                try {
                    response = referrerClient.getInstallReferrer();
                    String referrerUrl = response.getInstallReferrer();
                    Logger.d(WebEngageConstant.TAG, "Referrer Url: " + referrerUrl);
                    Intent intent = new Intent();
                    intent.putExtra("referrer", referrerUrl);
                    WebEngage.get().analytics().installed(intent);
                } catch (RemoteException e) {
                    Logger.d(WebEngageConstant.TAG, "Exception while getting install-referrer " + e);
                }
                referrerClient.endConnection();
                break;

            case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                Logger.d(WebEngageConstant.TAG, "Install referrer API not available on the current Play Store app");
                break;

            case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                Logger.d(WebEngageConstant.TAG, "Install referrer Connection couldn't be established");
                break;
        }
    }

    @Override
    public void onInstallReferrerServiceDisconnected() {
        Logger.w(WebEngageConstant.TAG, "onInstallReferrerService Disconnected");
    }
}
