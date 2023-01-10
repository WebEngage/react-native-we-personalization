package com.webengage.sdk.android;


import android.content.Intent;
import android.location.Location;

import java.util.List;
import java.util.Map;

class LocationManagerNoOpImpl extends LocationManager {
    @Override
    protected void registerLocationUpdates(long interval, long fastestInterval, float smallestDisplacement, int priority) {

    }

    @Override
    protected void unregisterLocationUpdates() {

    }

    @Override
    public Location getLastKnownLocation() {
        return null;
    }

    @Override
    public Location parseLocation(Intent intent) {
        return null;
    }

    @Override
    public void registerGeoFence(double latitude, double longitude, float radius, String id, WebEngageConfig config) {

    }

    @Override
    public void unregisterGeoFence(List<String> ids) {

    }

    @Override
    public List<LocationManagerImpl.GeoFenceTransition> detectGeoFenceTransition(Intent intent) {
        return null;
    }
}
