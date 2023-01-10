package com.webengage.sdk.android;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.webengage.sdk.android.utils.ReflectionUtils;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.ArrayList;
import java.util.List;


class LocationManagerImpl extends LocationManager implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public Context applicationContext;
    static GoogleApiClient googleApiClient = null;
    LocationRequest locationRequest = null;

    public LocationManagerImpl(Context context) {
        this.applicationContext = context.getApplicationContext();
        googleApiClient = new GoogleApiClient.Builder(this.applicationContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    @Override
    protected void registerLocationUpdates(long interval, long fastestInterval, float smallestDisplacement, int priority) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(fastestInterval);
        locationRequest.setSmallestDisplacement(smallestDisplacement);
        locationRequest.setPriority(priority);
        _registerForLocationUpdate(locationRequest, this.applicationContext, googleApiClient);
    }

    @Override
    protected void unregisterLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            if (PendingIntentFactory.doesLocationPendingIntentExists(this.applicationContext)) {
                Logger.d(WebEngageConstant.TAG, "UnRegistering from location updates ");
                PendingIntent pendingIntent = PendingIntentFactory.constructLocationPendingIntent(this.applicationContext);
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, pendingIntent);
                pendingIntent.cancel();
            } else {
                Logger.d(WebEngageConstant.TAG, "Location pending intent does not exists, no need to unregister");
            }
        }
    }

    @Override
    public Location getLastKnownLocation() {
        if (googleApiClient != null && googleApiClient.isConnecting()) {
            synchronized (this) {
                try {
                    wait(5000);
                } catch (InterruptedException e) {

                }
            }
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        synchronized (this) {
            notifyAll();
        }
        try {
            _registerForLocationUpdate(locationRequest, this.applicationContext, googleApiClient);
        } catch (Exception e) {

        }
    }

    private void _registerForLocationUpdate(LocationRequest locationRequest, Context context, GoogleApiClient googleApiClient) {
        if (locationRequest != null && context != null && googleApiClient != null && googleApiClient.isConnected()) {
            Logger.d(WebEngageConstant.TAG, "Registering for location updates");
            PendingIntent pendingIntent = PendingIntentFactory.constructLocationPendingIntent(context.getApplicationContext());
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, pendingIntent);
        }
    }

    @Override
    public synchronized void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public synchronized void onConnectionFailed(ConnectionResult connectionResult) {
        synchronized (this) {
            notifyAll();
        }
    }

    @Override
    public Location parseLocation(Intent intent) {
        if (ReflectionUtils.isLocationResultPresent()) {
            if (LocationResult.hasResult(intent)) {
                return LocationResult.extractResult(intent).getLastLocation();
            }
        } else {
            if (ReflectionUtils.isFusedLocationDependencyAdded()) {
                Bundle extras = intent.getExtras();
                if (extras != null && extras.containsKey(FusedLocationProviderApi.KEY_LOCATION_CHANGED)) {
                    return extras.getParcelable(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
                }
            }
        }
        return null;
    }

    @Override
    public void registerGeoFence(double latitude, double longitude, float radius, String id, WebEngageConfig config) {
        if (ReflectionUtils.isGeoFencePresent() && ReflectionUtils.isGeoFenceRequestPresent()) {
            Geofence geofence = new Geofence.Builder()
                    .setCircularRegion(latitude, longitude, radius)
                    .setRequestId(id)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            if (googleApiClient != null && googleApiClient.isConnecting()) {
                synchronized (this) {
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {

                    }
                }
            }
            if (googleApiClient != null && googleApiClient.isConnected()) {
                GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                        .addGeofence(geofence)
                        .build();

                LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, PendingIntentFactory.constructGeoFencePendingIntent(this.applicationContext));

                if (config != null && config.getLocationTrackingStrategy() != LocationTrackingStrategy.ACCURACY_BEST) {
                    Logger.w(WebEngageConstant.TAG, "Current location tracking strategy is " + config.getLocationTrackingStrategy() + ", for better geofencing results use WebEngage.get().setLocationTrackingStrategy(LocationTrackingStrategy.ACCURACY_BEST)");
                }
            }
        }
    }

    @Override
    public void unregisterGeoFence(List<String> ids) {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, ids);
        }
    }

    @Override
    public List<GeoFenceTransition> detectGeoFenceTransition(Intent intent) {
        if (ReflectionUtils.isGeoFenceEventPresent()) {
            GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
            if (!geofencingEvent.hasError()) {
                List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
                if (geofenceList != null && geofenceList.size() > 0) {
                    List<GeoFenceTransition> list = new ArrayList<GeoFenceTransition>();
                    for (Geofence geofence : geofenceList) {
                        GeoFenceTransition geoFenceTransition = new GeoFenceTransition(geofence.getRequestId(), geofencingEvent.getTriggeringLocation(), geofencingEvent.getGeofenceTransition());
                        list.add(geoFenceTransition);
                    }
                    return list;
                }
            }
        }
        return null;

    }


    public class GeoFenceTransition {

        String id = null;
        Location location = null;
        int transition;

        public GeoFenceTransition(String id, Location location, int transition) {
            this.id = id;
            this.location = location;
            this.transition = transition;
        }

        public String getId() {
            return id;
        }

        public Location getLocation() {
            return location;
        }

        public int getTransition() {
            return transition;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof GeoFenceTransition) {
                GeoFenceTransition obj = (GeoFenceTransition) o;
                if (this.id != null) {
                    return this.id.equals(obj.id);
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (this.id != null) {
                return id.hashCode();
            }
            return 0;
        }

        @Override
        public String toString() {
            return "GeoFenceTransition: {\n id: " + id + ", Location: " + location + ", Transition: " + transition + "\n}";
        }
    }
}
