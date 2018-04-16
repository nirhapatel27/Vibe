package com.cse110.flashbackmusicplayer;

import android.location.Location;

public interface SongObserver {
    void update(Song song, String user, long time, Location location);
}
