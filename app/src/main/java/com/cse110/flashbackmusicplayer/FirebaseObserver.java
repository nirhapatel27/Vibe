package com.cse110.flashbackmusicplayer;

import android.location.Location;

public interface FirebaseObserver {
    void update(String user, Location location, long time);

    public String getTitle();
    public String getAlbum();
    public String getArtist();
    public String getFilename();
    public String getURL();
    public String getTrackNumber();
}
