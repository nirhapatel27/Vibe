package com.cse110.flashbackmusicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class LocationSystem {

    private Activity activity;
    private static Geocoder geocoder;

    public LocationSystem(Activity activity, UserState state) {
        this.activity = activity;
        geocoder = new Geocoder(activity);

        // Record the user's location whenever it changes.
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Get the new updated location of the user.
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                //Log.d("LocationSystem", "The location changed to " + lat + " " + lon);

                // Get the name of the place the user is at.
                String place = getPlace(lat, lon);


                // Update the user state with the new location.
                state.locationUpdated(lat, lon, place);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {}
        };

        // Set location permissions.
        setPermissions(locationListener);
    }

    public static String getPlace(double lat, double lon) {
        List<Address> addresses = null;
        try {
            // Get the most likely address of the location.
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Check if the address was successfully obtained.
        if (addresses != null && addresses.size() == 1) {
            String place = addresses.get(0).getAddressLine(0);
            //Log.d("LocationSystem", "The place is " + place);
            return place;
        }
        else {
            //Log.d("LocationSystem", "No place name could be extracted");
            return null;
        }
    }

    private void setPermissions(LocationListener locationListener) {
        // Assert that we have permissions to get location data.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            Log.d("LocationSystem", "Requested permission to access GPS data");
            return;
        }

        LocationManager locationManager =
                (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
    }

}
