package com.cse110.flashbackmusicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class MediaService extends Service implements MediaPlayer.OnPreparedListener {
    // different Action Strings possible for service
    private static final String ACTION_START = "START";
    private static final String ACTION_PLAY = "PLAY";
    private static final String ACTION_PAUSE = "PAUSE";
    private static final String ACTION_STOP = "STOP";
    // generic broadcast string
    public static final String ACTION_BROADCAST = "BROADCAST";
    public static final String ACTION_FINISHED = "FINISHED";
    public static final String ACTION_PROGRESS = "PROGRESS";
    public static final String ACTION_SEEK = "SEEK";

    // Broadcast intent
    Intent sendSong;
    Intent finished;
    Intent progress;
    // song to play
    Song song;
    // actual MediaPlayer instance
    MediaPlayer player = null;
    // Handler for sending broadcast messages
    private final Handler handler = new Handler();

    @Override
    public void onCreate() {
        Log.d("MediaService", "MediaService has been created");
        // Create intent
        sendSong = new Intent(ACTION_BROADCAST);
        finished = new Intent(ACTION_FINISHED);
        progress = new Intent(ACTION_PROGRESS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Create player.
            if (intent.getAction().equals(ACTION_START)) {
                Log.d("MediaService", "MediaService started with ACTION_START");
                // Get the song we are playing.
                song = findSong(intent);

                // If the song is disliked, we do not need to play it.
                if (song.isDisliked()) {
                    Log.d("MediaService", "The song is disliked");
                    // Notify that we did not play any song.
                    sendSong.putExtra("NAME", (String) null);
                    sendSong.putExtra("PAUSED", false);
                    sendBroadcast(sendSong);
                    return START_STICKY;
                }

                // Create the media player.
                player = new MediaPlayer();
                // Record the state of the user when we started playing the song.
                UserState snapshot = MainActivity.userState.snapshot();
                String resource = song.getFilename();
                Uri source = Uri.parse(resource);
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(getApplicationContext(), source);
                    // work in background (even after locking screen)
                    player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    player.setOnPreparedListener(this);
                    player.prepareAsync(); // prepare async to not block main thread
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Notify the caller of this service when it is complete.
                player.setOnCompletionListener(mediaPlayer -> {
                    // The song is finished, so we aren't playing a song any more.
                    sendSong.putExtra("NAME", (String) null);
                    sendSong.putExtra("PAUSED", false);
                    sendBroadcast(sendSong);
                    // Notify that we have finished.
                    sendBroadcast(finished);
                    // Record the time this song was finished.
                    song.donePlaying(snapshot);
                    Log.d("MediaService", "Track has finished");
                });

                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", false);
                sendBroadcast(sendSong);
            }
            // Pause player.
            else if (intent.getAction().equals(ACTION_PAUSE)) {
                Log.d("MediaService", "MediaService has been started with ACTION_PAUSE");
                player.pause();
                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", true);
                sendBroadcast(sendSong);
            }
            // Resume player.
            else if (intent.getAction().equals(ACTION_PLAY)){
                Log.d("MediaService", "MediaService has been started with ACTION_PLAY");
                player.start();
                // Notify the Music system of the successful start.
                sendSong.putExtra("NAME", song.getTitle());
                sendSong.putExtra("PAUSED", false);
                sendBroadcast(sendSong);
            }
            // Stop playing the current song.
            else if (intent.getAction().equals(ACTION_STOP)) {
                Log.d("MediaService", "MediaService has been started with ACTION_STOP");
                sendBroadcast(finished);
                // Notify that we did not play any song.
                sendSong.putExtra("NAME", (String) null);
                sendSong.putExtra("PAUSED", false);
                sendBroadcast(sendSong);
                player.stop();
            }
            else if(intent.getAction().equals(ACTION_SEEK)) {
                Log.d("MediaService", "MediaService has been started with ACTION_SEEK");
                int position = intent.getIntExtra("PROGRESS", 0);
                player.seekTo(position);
            }
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        // remove any pending posts in queue
        handler.removeCallbacks(sendProgress);
        // post to thread
        handler.post(sendProgress);

        return START_STICKY;
    }

    private Runnable sendProgress = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                progress.putExtra("PROGRESS", player.getCurrentPosition());
                progress.putExtra("MAX", player.getDuration());
                sendBroadcast(progress);
                Log.d("MediaService", "Track progress broadcast sent");
            }
            handler.postDelayed(this, 5000);
        }
    };


    private Song findSong(Intent intent) {
        String name = intent.getExtras().getString("NAME");
        return songDB.get(name);
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // No binding necessary
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Notify that this player is displayed, so no songs are played.
        sendSong.putExtra("NAME", (String) null);
        sendSong.putExtra("PAUSED", false);
        sendBroadcast(sendSong);
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        Log.d("MediaService", "MediaService has been destroyed");
    }
}
