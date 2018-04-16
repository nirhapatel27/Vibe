package com.cse110.flashbackmusicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.musicSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class AlbumActivity extends AppCompatActivity {

    ArrayList<Song> songs = null;
    List<String> trackTitles = new ArrayList<>();
    SongCallback ui = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("AlbumActivity", "AlbumActivity has been created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        // Get the name of the songs we are playing.
        String albumName = getIntent().getExtras().getString("ALBUM_NAME");
        songs = songDB.getAlbum(albumName);

        // Display a list of all the songs in this album.
        ArrayList<Song> songs = songDB.getAlbum(albumName);
        for(int i = 0; i<songs.size();i++)
        {
            trackTitles.add(songs.get(i).getTitle());
        }
        ListAdapter songAdapter = new ArrayAdapter<>(this, R.layout.list_white_text,R.id.list_content, trackTitles);
        final ListView tracksView = (ListView) findViewById(R.id.track_list);
        tracksView.setAdapter(songAdapter);

        // Get the first song in the album, and add it to the intent.
        if (!songs.isEmpty()) {
            getIntent().putExtra("TRACK_NAME", songs.get(0).getTitle());
        }
        else {
            getIntent().putExtra("TRACK_NAME", "empty_track");
        }

        // Set the callback so that the music system can update the UI.
        ui = new SongCallbackUI(AlbumActivity.this, musicSystem);
        musicSystem.setSongCallback(ui);

        // Play the song.
        musicSystem.playTracks(this::nextSong);

        // Go to the next song.
        final Button skip = (Button) findViewById(R.id.nextButton);
        skip.setOnClickListener(view -> {
            musicSystem.skipTrack();
            Log.d("AlbumActivity", "Skipped a track");
        });
    }

    public Song nextSong() {
        // If there are no songs left to play in the album, finish.
        if (songs.isEmpty()) return null;

        // Get the very first song that we will play.
        Song next = songs.get(0); songs.remove(0);
        Log.d("AlbumActivity", "Retrieved song " + next.getTitle());

        // Record the name of teh track.
        getIntent().putExtra("TRACK_NAME", next.getTitle());

        // Draw the metadata for the song.
        ui.redraw();

        return next;
    }
}
