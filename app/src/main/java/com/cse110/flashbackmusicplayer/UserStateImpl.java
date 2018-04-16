package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.Calendar;

public class UserStateImpl implements UserState {

    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location(LocationManager.GPS_PROVIDER);
    String place = "";
    String user = "";

    Calendar calendar = null;

    public UserStateImpl() {}

    @Override
    public UserState snapshot() {
        return new UserStateSnapshot(this);
    }

    @Override
    public void locationUpdated(double latitude, double longitude, String place) {
        //Log.d("UserStateImpl", "Set the latitude and longitude to " + latitude + " " + longitude);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.place = place;
    }

    @Override
    public Location getLocation() {
        return new Location(location);
    }

    public long getSystemTime() {
        if (calendar == null) {
            return System.currentTimeMillis();
        }
        else {
            return calendar.getTimeInMillis();
        }
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public int getDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void setCalendar(Calendar c) {
        this.calendar = c;
    }

    @Override
    public void setUser(String userID) {
        this.user = userID;
    }

}
