package com.cse110.flashbackmusicplayer;

public interface SongCallback {

    // Update the seekbar to the given percentage.
    void setSeekbarTo(int progress, int max);

    // Display the song as paused or resumed on the UI.
    void setPaused(boolean paused);

    // Change the image for the liked status of the image.
    void setLikedStatus(Song.LIKED_STATUS likedStatus);

    // Redraw the UI. Probably because the song has changed.
    void redraw();


}
