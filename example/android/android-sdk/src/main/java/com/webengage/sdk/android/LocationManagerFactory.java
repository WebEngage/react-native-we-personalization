package com.webengage.sdk.android;


import android.content.Context;
import android.os.Build;

import com.webengage.sdk.android.utils.ManifestUtils;
import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class LocationManagerFactory {
    static LocationManager locationManager = null;
    static LocationManager noOp = null;

    public static LocationManager getLocationManager(Context context) {
        if (!ReflectionUtils.isGoogleApiClientDependencyAdded() || !ReflectionUtils.isFusedLocationDependencyAdded()) {
            Logger.w(WebEngageConstant.TAG, "Unable to initialize location module");
            return getNoOpLocationManager();
        }

        if (!ManifestUtils.checkPermission(ManifestUtils.ACCESS_FINE_LOCATION, context.getApplicationContext())) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Logger.w(WebEngageConstant.TAG, "Location Tracking is enabled but AndroidManifest is missing required permission : " + ManifestUtils.ACCESS_FINE_LOCATION);
            } else {
                Logger.w(WebEngageConstant.TAG, "Location Tracking is enabled but location permission is not granted to application");
            }
            return getNoOpLocationManager();
        }
        if (locationManager == null) {
            locationManager = new LocationManagerImpl(context.getApplicationContext());
        }
        return locationManager;
    }

    private static LocationManager getNoOpLocationManager() {
        if (noOp == null) {
            noOp = new LocationManagerNoOpImpl();
        }
        return noOp;
    }
}
