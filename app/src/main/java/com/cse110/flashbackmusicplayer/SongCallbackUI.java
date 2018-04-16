package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;
import static com.cse110.flashbackmusicplayer.MainActivity.user;

public class SongCallbackUI implements SongCallback {

    private Activity activity;
    private MusicSystem musicSystem;

    private Song song;
    private String name;

    public SongCallbackUI(Activity activity, MusicSystem musicSystem) {
        this.activity = activity;
        this.musicSystem = musicSystem;

        // Change the like status button when it is clicked.
        Button fav_dislike = (Button) activity.findViewById(R.id.fav_dis_button);
        fav_dislike.setOnClickListener(view -> {
            switch (song.getLikedStatus()) {
                case NEUTRAL:
                    song.setLikedStatus(Song.LIKED_STATUS.FAVORITED);
                    break;
                case DISLIKED:
                    song.setLikedStatus(Song.LIKED_STATUS.NEUTRAL);
                    musicSystem.playTrack(song.getTitle());
                    break;
                case FAVORITED:
                    song.setLikedStatus(Song.LIKED_STATUS.DISLIKED);
                    musicSystem.skipTrack();
                    break;
            }
            Log.d("SongCallbackUI", "Favorite/Dislike/Neutral button pressed. Song is now " + song.getLikedStatus());
            setLikedStatus(song.getLikedStatus());
        });

        // Pause or play the song.
        final Button pauseButton = (Button) activity.findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener( view -> {
            Log.d("SongCallbackUI", "Pause button pressed");
            musicSystem.togglePause();
        } );

        // Go back to the selection screen.
        final Button switchScreen = (Button) activity.findViewById(R.id.backButton);
        switchScreen.setOnClickListener(view -> {
            Log.d("SongCallbackUI", "Going back to the previous screen.");
            activity.finish();
        });

        // If the user moves the seekbar, update the progress of the song.
        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.slideTrack);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("SongCallbackUI", "Seek bar pressed");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("SongCallbackUI", "Seek bar released on " + (seekBar.getProgress()*1.0/seekBar.getMax()) + "%");
                musicSystem.seekTime(seekBar.getProgress());
            }
        });
    }

    @Override
    public void setSeekbarTo(int progress, int max) {
        Log.d("SongCallbackUI", "Moving seekbar to " + (progress * 1.0 / max));
        // Get the seekbar.
        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.slideTrack);
        seekBar.setMax(0);
        seekBar.setMax(max);
        seekBar.setProgress(progress);
    }

    @Override
    public void setPaused(boolean paused) {
        Button pauseButton = (Button) activity.findViewById(R.id.pauseButton);
        if (paused) pauseButton.setBackgroundResource(R.drawable.playwhite);
        else pauseButton.setBackgroundResource(R.drawable.pausewhite);
    }

    @Override
    public void setLikedStatus(Song.LIKED_STATUS likedStatus) {
        Button fav_dislike = (Button) activity.findViewById(R.id.fav_dis_button);
        switch (likedStatus) {
            case NEUTRAL:
                fav_dislike.setBackgroundResource(R.drawable.neutralwhite);
                Log.d("SongCallbackUI", "Set favorite/dislike image to neutral");
                break;
            case DISLIKED:
                fav_dislike.setBackgroundResource(R.drawable.dislikewhite);
                Log.d("SongCallbackUI", "Set favorite/dislike image to dislike");
                break;
            case FAVORITED:
                fav_dislike.setBackgroundResource(R.drawable.favouritewhite);
                Log.d("SongCallbackUI", "Set favorite/dislike image to favorite");
                break;
        }
    }


    @Override
    public void redraw() {
        Log.d("SongCallbackUI", "Drawing the UI");
        // Get the name of the song we are playing.
        name = activity.getIntent().getExtras().getString("TRACK_NAME");
        song = songDB.get(name);

        if (song != null) {

            // Get the formatted strings describing when the track was last played.
            String place = song.getPlace();
            String time = song.getTime();
            String date = song.getDate();

            // If any of these do not exist, then the track is being played for the first time.
            TextView songHistory = (TextView) activity.findViewById(R.id.history);
            if (time == null || date == null) {
                // Don't write anything.
                songHistory.setText("");
            } else if (place == null) {
                String songTitleStr = "Last Played: " + song.getTime() + ", " + song.getDate();
                songHistory.setText(songTitleStr);
            } else {
                // check if last played by self, friend, or stranger
                String lastPlayedBy = "";

                if(DisplayName.lastPlayedByCurrentUser(song.getUser())){
                    lastPlayedBy = UserDataStorage.getProfile().get(1);
                    lastPlayedBy = "you";
                }

                else{
                    lastPlayedBy = DisplayName.lastPlayedByOther(song.getUser());
                }
                String songTitleStr = "Last Played: " + song.getPlace() + "\n" + song.getTime() + ", " + song.getDate() + "\n" + "Last played by: " + lastPlayedBy;

                // italics if last played by current user
                if(DisplayName.lastPlayedByCurrentUser(song.getUser())){
                    songHistory.setTypeface(null, Typeface.ITALIC);
                }
                songHistory.setText(songTitleStr);
            }

            // Access and display title and artist metadata.
            TextView songTitle = (TextView) activity.findViewById(R.id.songTitle);
            String songTitleStr = song.getTitle();
            if (song.getArtist() != null) {
                songTitleStr = songTitleStr + "\n" + song.getArtist();
            }
            songTitle.setText(songTitleStr);

            // Access and display album and track number metadata
            TextView songAlbum = (TextView) activity.findViewById(R.id.songAlbum);
            String songAlbumStr = song.getAlbum() + "\nTrack #: " + song.getTrackNumber();
            songAlbum.setText(songAlbumStr);

            // Create and display the image for album cover.
            ImageView albumcover = (ImageView) activity.findViewById(R.id.album_cover);
            byte[] albumArtData = song.getAlbumCover();
            if (albumArtData != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArtData, 0, albumArtData.length);
                albumcover.setImageBitmap(bitmap);
                albumcover.setAdjustViewBounds(true);
            } else {
                albumcover.setImageResource(R.drawable.nocover);
                albumcover.setAdjustViewBounds(true);
            }

            // Set the image of the like/dislike button.
            setLikedStatus(song.getLikedStatus());

            // Make the pause button a pause or a play symbol.
            setPaused(musicSystem.isPaused());
        }
        else {
            // If the song doesn't exist, just display a message.
            Toast.makeText(activity, "There are no available songs", Toast.LENGTH_LONG).show();
        }
    }
}
