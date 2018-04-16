package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Song implements SongSubject, FirebaseObserver {

    // The data defining this song.
    private String filename, url, title, album, artist, trackNumber;
    // The track in the album.
    private byte[] albumCover;

    // Whether or not this track is downloaded to the users phone or not.
    private boolean downloaded = false;
    private boolean queued = false;

    // A cached priority score for this song. Only valid after calling generateVibeList while
    // hasStateChanged() return false.
    private int priority = 0;

    // List of all locations and times this song has been played at and the respective users.
    // The lengths of these arrays are the same and at each index the triple shows one play of song.
    private ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<Long> times = new ArrayList<>();
    private ArrayList<String> users = new ArrayList<>();

    public Song(String filename, String url, String title, String album, String artist, String trackNumber,
                byte[] albumCover) {
        this.filename = filename;
        this.url = url;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.trackNumber = trackNumber;
        this.albumCover = albumCover;

        obs = new ArrayList<>();
    }

    public void donePlaying(UserState state) {
        // Notify that the information about the song has finished.
        notifyObservers(state.getUser(), state.getSystemTime(), state.getLocation());
    }

    public String getUser() {
        if (users.size() == 0) return null;
        return users.get(getLastIndex());
    }

    public String getTime() {
        if (times.size() == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getSystemTime());
        int hours = calendar.get(Calendar.HOUR);
        String hours_string = String.valueOf(hours);
        if (hours / 10 == 0) hours_string = "0" + hours_string;

        int minutes = calendar.get(Calendar.MINUTE);
        String minutes_string = String.valueOf(minutes);
        if (minutes / 10 == 0) minutes_string = "0" + minutes_string;

        int am_pm = calendar.get(Calendar.AM_PM);
        String am_pm_string = am_pm == Calendar.AM ? "am" : "pm";

        String time = hours_string + ":" + minutes_string + " " + am_pm_string;
        Log.d("UserStateImpl", "The time is " + time);
        return time;
    }

    public String getDate() {
        if (times.size() == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getSystemTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // January is 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String date = "" + month + "/" + day + "/" + year;
        return date;
    }
    public String getPlace() {
        if (locations.size() == 0) return null;

        Location loc = locations.get(getLastIndex());
        return LocationSystem.getPlace(loc.getLatitude(), loc.getLongitude());
    }
    public long getSystemTime() {
        if (times.size() == 0) return -1;

        // The last time this track was played is the last entry of the times array.
        return times.get(getLastIndex());
    }

    private int getLastIndex() {
        long largestTime = -1;
        int index = -1;

        for (int i = 0; i < times.size(); i++) {
            long time = times.get(i);
            if (time > largestTime) {
                largestTime = time;
                index = i;
            }
        }

        return index;
    }

    public ArrayList<Location> getLocations() { return locations; }
    public ArrayList<Long> getTimes() { return times; }
    public ArrayList<String> getUsers() { return users; }

    public String getTitle() { return title; }
    public String getAlbum() { return album; }
    public String getArtist() { return artist; }
    public String getFilename() { return filename; }
    public String getURL() { return url; }
    public String getTrackNumber() { return trackNumber; }

    public void setAlbumCover(byte[] cover) { albumCover = cover; }
    public byte[] getAlbumCover() { return albumCover; }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public boolean isQueued() {
        return queued;
    }

    public void setQueued(boolean queued) {
        this.queued = queued;
    }

    // Whether the song was favorited or disliked. Should not both be true at the same time.
    public enum LIKED_STATUS { FAVORITED, DISLIKED, NEUTRAL }
    private LIKED_STATUS likedStatus = LIKED_STATUS.NEUTRAL;

    public LIKED_STATUS getLikedStatus() { return likedStatus; }
    public void setLikedStatus(LIKED_STATUS likedStatus) { this.likedStatus = likedStatus; }
    public boolean isFavorited() { return likedStatus == LIKED_STATUS.FAVORITED; }
    public boolean isDisliked() { return likedStatus == LIKED_STATUS.DISLIKED; }

    public boolean isPlayedByFriend() {
        List<String> songUsers = this.getUsers();
        for (String id: songUsers) {
            for (List<String> contact : UserDataStorage.getContacts()) {
                if (id.compareTo(contact.get(1)) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void update(String user, Location location, long time) {
        users.add(user);
        locations.add(location);
        times.add(time);
    }

    private ArrayList<SongObserver> obs;

    @Override
    public void registerObserver(SongObserver o) {
        obs.add(o);
    }

    @Override
    public void removeObserver(SongObserver o) {
        obs.remove(o);
    }

    @Override
    public void notifyObservers(String user, long time, Location location) {
        for(SongObserver ob : obs) {
            ob.update(this, user, time, location);
        }
    }
}
