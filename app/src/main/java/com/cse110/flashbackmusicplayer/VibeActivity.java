package com.cse110.flashbackmusicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.downloadSystem;
import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class VibeActivity extends AppCompatActivity {

    // In charge of handling all requests to play music.
    MusicSystem musicSystem = null;
    // Draws and updates the UI.
    SongCallback ui = null;
    // The list of vibe mode songs.
    List<Song> songs;
    List<String> nextTrackTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("VibeActivity", "VibeActivity has been created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vibe);

        // Create the play list.
        songs = songDB.generateVibeList();

        // Download the first two songs.
        downloadNextTwoSongs();

        // Create the system that will play all the music.
        musicSystem = new MusicSystem(VibeActivity.this);
        // Set the callback so that the music system can update the UI.
        ui = new SongCallbackUI(VibeActivity.this, musicSystem);
        musicSystem.setSongCallback(ui);

        // If there are no songs downloaded, leave an error message and exit
        boolean downloaded = false;
        for (Song song : songs) {
            if (song.isDownloaded()) {
                downloaded = true;
                break;
            }
        }
        if (!downloaded) {
            Toast.makeText(this, "No songs downloaded. Please wait.", Toast.LENGTH_LONG).show();
            finish();
        }

        // Play the song with the nighest priority.
        musicSystem.playTracks(this::nextSong);

        // Go to the next song.
        final Button skip = (Button) findViewById(R.id.nextButton);
        skip.setOnClickListener(view -> {
            musicSystem.skipTrack();
            Log.d("VibeActivity", "Skipped a track");
        });
    }

    private void downloadNextTwoSongs() {
        // If the first song in the playlist is not downloaded, download it.
        if (songs.size() > 0) {
            Song s1 = songs.get(0);
            if (!s1.isQueued()) {
                s1.setQueued(true);
                downloadSystem.downloadTrack(s1.getURL());
                Log.d("VibeActivity", "Starting download of song " + s1.getTitle());
            }
        }

        if (songs.size() > 1) {
            // If the second song in the playlist is not downloaded, download it.
            Song s2 = songs.get(1);
            if (!s2.isQueued()) {
                s2.setQueued(true);
                downloadSystem.downloadTrack(s2.getURL());
                Log.d("VibeActivity", "Starting download of song " + s2.getTitle());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicSystem.destroy();
        Log.d("VibeActivity", "VibeActivity has been destroyed");

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public Song nextSong() {
        // If there are no songs in the flashback playlist or the state of the user has changed
        // since the last time the playlist was generated, attempt to generate it again.
        if (songs.isEmpty() || songDB.hasStateChanged()) {
            songs = songDB.generateVibeList();
        }

        // If there are no songs left to play in the album, finish.
        if (songs.isEmpty()) return null;

        // Search for the first song that is downloaded.
        Song next = null;
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            if (song.isDownloaded()) {
                Log.d("VibeActivity", "Retrieved song " + song.getTitle());
                next = song;
                songs.remove(i);
                break;
            }
            else {
                Log.d("VibeActivity", "Song " + song.getTitle() + " is not yet downloaded");
            }
        }

        // Download the first two songs.
        downloadNextTwoSongs();

        nextTrackTitles.clear();

        for(int i = 0; i<songs.size();i++)
        {
            nextTrackTitles.add(songs.get(i).getTitle());
        }
        ListAdapter nextSongAdapter = new ArrayAdapter<>(this, R.layout.list_white_text, R.id.list_content, nextTrackTitles);
        final ListView tracksView = (ListView) findViewById(R.id.vibe_track_list);
        tracksView.setAdapter(nextSongAdapter);

        // If next is still null, none of the songs have been downloaded yet.
        if (next == null) {
            getIntent().putExtra("TRACK_NAME", "empty_track");
        }
        else {
            // Record the name of teh track.
            getIntent().putExtra("TRACK_NAME", next.getTitle());

            // Draw the metadata for the song.
            ui.redraw();
        }

        return next;
    }

}
