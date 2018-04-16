package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;

import java.util.Calendar;

public class MockUserState implements UserState {
    // The current up-to-date position of the user and the name of the closest location.
    Location location = new Location("gps");
    String place = "";
    //TimeSegment timeSegment = TimeSegment.MORNING;
    int dayOfYear = 1;
    String date = "";
    String time = "";
    long systemTime = 0;
    String userId = "1";

    public MockUserState() {}

    public Location getLocation() { return new Location(location); }
    public String getPlace() { return place; }
    //public TimeSegment getTimeSegment() { return timeSegment; }
    //public int getDayOfWeek() { return dayOfWeek; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public long getSystemTime() {
        return systemTime;
    }

    @Override
    public UserState snapshot() {
        return new UserStateSnapshot(this);
    }

    public void locationUpdated(double latitude, double longitude, String place) {
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        this.place = place;
    }
    public void setLocation(Location location) { this.location = location; }
    public void setPlace(String place) { this.place = place; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setSystemTime(long systemTime) { this.systemTime = systemTime; }


    public String getUser(){return userId;};
    public void setUser(String userID){this.userId = userID;};
    public int getDayOfYear() {return dayOfYear;}

    @Override
    public void setCalendar(Calendar c) {

    }

    ;

}