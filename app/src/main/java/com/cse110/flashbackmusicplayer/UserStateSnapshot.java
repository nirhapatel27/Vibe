package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.Calendar;

public class UserStateSnapshot implements UserState {

    private Location location;
    private int dayOfYear;
    private String user;
    private long systemTime;

    public UserStateSnapshot(UserState state) {
        Log.d("UserStateSnapshot", "Snapshot created at " + state.getSystemTime());
        this.location = new Location(state.getLocation());
        this.dayOfYear = state.getDayOfYear();
        this.systemTime = state.getSystemTime();
        this.user = state.getUser();
    }

    @Override
    public UserState snapshot() {
        return new UserStateSnapshot(this);
    }

    @Override
    public void locationUpdated(double latitude, double longitude, String place) {
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public long getSystemTime() {
        return systemTime;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public int getDayOfYear() {
        return dayOfYear;
    }

    @Override
    public void setCalendar(Calendar c) {
        systemTime = c.getTimeInMillis();
    }

    @Override
    public void setUser(String userID) {this.user = userID;}
}
