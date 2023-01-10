package com.webengage.sdk.android;


import android.content.Intent;
import android.location.Location;

import java.util.List;
import java.util.Map;

public abstract class LocationManager {

    protected abstract void registerLocationUpdates(long interval, long fastestInterval, float smallestDisplacement, int priority);

    protected abstract void unregisterLocationUpdates();

    public abstract void registerGeoFence(double latitude, double longitude, float radius, String id, WebEngageConfig config);

    public abstract void unregisterGeoFence(List<String> ids);

    public abstract Location getLastKnownLocation();

    public abstract Location parseLocation(Intent intent);

    public abstract List<LocationManagerImpl.GeoFenceTransition> detectGeoFenceTransition(Intent intent);


}
