package com.ilya.photomap.ui.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationFinder {

    public interface Listener {
        void onLocationProvided(Location location);
    }

    private LocationManager manager;

    public LocationFinder(Context context) {
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public void requestLocation(Listener listener) {
        if (!isGpsEnabled()) {
            listener.onLocationProvided(null);
            return;
        }

//        Location lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        if (lastKnownLocation != null) {
//            listener.onLocationProvided(lastKnownLocation);
//            return;
//        }

        manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                listener.onLocationProvided(location);
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) { }
            @Override public void onProviderEnabled(String provider) { }
            @Override public void onProviderDisabled(String provider) { }
        }, null);
    }

    public boolean isGpsEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
