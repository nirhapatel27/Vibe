package com.cse110.flashbackmusicplayer;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.concurrent.Callable;


public class MusicSystem {

    private Activity root;
    private SongCallback songCallback;
    private String currSong = null;
    private boolean isPaused = false;

    // Classes to receiver messages from the service.
    BroadcastReceiver broadcastReceiver;
    BroadcastReceiver onCompletionListener;
    BroadcastReceiver seekbarStateReceiver;


    public MusicSystem(Activity root) {
        this.root = root;
    }

    public void setSongCallback(SongCallback songCallback) {
        this.songCallback = songCallback;
        Log.d("MusicSystem", "New song callback set");
    }

    public void playTrack(String toPlay) {
        // Create an intent for MediaService
        Intent serviceIntent = new Intent(root, MediaService.class);
        // Create a receiver to store the name of the current song being played
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currSong = intent.getStringExtra("NAME");
                isPaused = intent.getBooleanExtra("PAUSED", false);
                Log.d("MusicSystem", "Received song name and paused state: " + currSong + " " + isPaused);
            }
        };
        root.registerReceiver(broadcastReceiver, new IntentFilter(MediaService.ACTION_BROADCAST));
        // Create a receiver that will update the UI to display the current progress of song.
        seekbarStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra("PROGRESS", 0);
                int max = intent.getIntExtra("MAX", 0);
                if (songCallback != null) {
                    songCallback.setSeekbarTo(progress, max);
                }
                Log.d("MusicSystem", "Received progress broadcast");
            }
        };
        root.registerReceiver(seekbarStateReceiver, new IntentFilter(MediaService.ACTION_PROGRESS));

        // If song is already playing and a different one chosen, stop playback and create
        // new service for playback
        ServicePlaybackState playbackState = checkSongPlaying(currSong, toPlay);
        if (playbackState == ServicePlaybackState.DIFF_SONG) {
            Log.d("MusicSystem", "Playing " + toPlay + ". This is a different song from before.");
            root.unregisterReceiver(broadcastReceiver);
            root.unregisterReceiver(seekbarStateReceiver);
            root.stopService(serviceIntent);
            serviceIntent = new Intent(root, MediaService.class);
            serviceIntent.setAction("START");
            serviceIntent.putExtra("NAME", toPlay);
            root.startService(serviceIntent);
            isPaused = false;
            if (songCallback != null) {
                songCallback.setSeekbarTo(0, 0);
            }
        }
        // if no song is currently being played, start new service for playback
        else if (playbackState == ServicePlaybackState.NO_SONG){
            Log.d("MusicSystem", "Playing " + toPlay + ". This is the first song we are playing.");
            serviceIntent = new Intent(root, MediaService.class);
            serviceIntent.setAction("START");
            serviceIntent.putExtra("NAME", toPlay);
            root.startService(serviceIntent);
            isPaused = false;
            if (songCallback != null) {
                songCallback.setSeekbarTo(0, 0);
            }
        }
        else {
            Log.d("MusicSystem", "Playing " + toPlay + " . Same as the current song we were playing.");
        }
    }


    public void playTracks(Callable<Song> func) {
        try {
            // Get the song we want to play that is not disliked.
            Song song = func.call();
            while (song != null && song.isDisliked()) song = func.call();

            // Play the song.
            if (song != null) playTrack(song.getTitle());
            else return;

            // Record when the song is done playing.
            onCompletionListener = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // Get the song we want to play.
                    Song song = null;
                    try {
                        // Play the song.
                        currSong = null;
                        song = func.call();
                        while (song != null && song.isDisliked()) song = func.call();
                        if (song != null) {
                            playTrack(song.getTitle());
                            Log.d("MusicSystem", "The next song in the list is " + song.getTitle());
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            root.registerReceiver(onCompletionListener, new IntentFilter(MediaService.ACTION_FINISHED));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void skipTrack() {
        // We can just finish the song we are currently playing. In album and flashback this will
        // get the next song. In regular mode it will do nothing.
        Intent serviceIntent = new Intent(root, MediaService.class);
        serviceIntent.setAction("STOP");
        root.startService(serviceIntent);
        Log.d("MusicSystem", "Sent STOP message to mediaservice");
    }

    public void seekTime(int currProgress) {
        Intent serviceIntent = new Intent(root, MediaService.class);
        serviceIntent.setAction("SEEK");
        serviceIntent.putExtra("PROGRESS", currProgress);
        root.startService(serviceIntent);
        Log.d("MusicSystem", "Sent SEEK message to mediaservice");
    }



    // Enum for possible states of song playback
    private enum ServicePlaybackState{
        NO_SONG, SAME_SONG, DIFF_SONG
    };

    /**
     * Determines if the user-clicked song is the same as the one already playing or if no
     * song is currently playing
     */
    private ServicePlaybackState checkSongPlaying(String playing, String toPlay) {
        if (playing != null) {
            if (playing.equals(toPlay)) {
                return ServicePlaybackState.SAME_SONG;
            }
            return ServicePlaybackState.DIFF_SONG;
        }
        return ServicePlaybackState.NO_SONG;
    }

    /**
     * Method to enable play and pause
     */
    public void togglePause(){
        if (songCallback != null) {
            songCallback.setPaused(!isPaused);
        }

        Intent serviceIntent = new Intent(root, MediaService.class);
        if (isPaused) {
            serviceIntent.setAction("PLAY");
            Log.d("MusicSystem", "Sent PLAY message to mediaservice");
        }
        else {
            serviceIntent.setAction("PAUSE");
            Log.d("MusicSystem", "Sent PAUSE message to mediaservice");
        }
        root.startService(serviceIntent);
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void destroy() {
        Intent serviceIntent = new Intent(root, MediaService.class);
        try {
            root.unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        try {
            root.unregisterReceiver(onCompletionListener);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        try {
            root.unregisterReceiver(seekbarStateReceiver);
        } catch (IllegalArgumentException e) {
            //e.printStackTrace();
        }
        root.stopService(serviceIntent);

        Log.d("MusicSystem", "Music system destroyed.");
    }

}
