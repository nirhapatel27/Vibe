package com.cse110.flashbackmusicplayer;

import android.location.Location;

public interface SongSubject {
    public void registerObserver(SongObserver o);
    public void removeObserver(SongObserver o);
    public void notifyObservers(String user, long time, Location location);
}
