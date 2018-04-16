package com.cse110.flashbackmusicplayer;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class DownloadSystem extends BroadcastReceiver {

    Activity activity;
    DownloadManager downloadManager;
    TrackContainer container;

    public DownloadSystem(Activity activity, TrackContainer container) {
        downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        this.container = container;
        this.activity = activity;

        // Assert that we have permissions to get location data.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            Log.d("Download System", "Requested permission to write and read to external storage");
        }
    }

    public void downloadTrack(String url) {
        Log.d("Download System", "Downloading track from " + url);

        TrackDownloader downloader = new TrackDownloader(downloadManager, activity);
        downloader.execute(url);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager.Query query = new DownloadManager.Query();
        Bundle extras = intent.getExtras();
        long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            // The below doesn't work if the title is set to something other than the file name.
            String filepath  = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
            String url = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));


            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Log.d("Download System", "Successfully downloaded " + url);
                // Check if we downloads a mp3 or zip file.
                String extension = filepath.substring(filepath.lastIndexOf('.') + 1);
                if (extension.equals("mp3")) {
                    // Record that this song is downloaded and that we can play it.
                    Song song = createSongFromFile(filepath, url);
                    // Add it to activities list of songs.
                    container.addTrack(song);
                }
                else if (extension.equals("zip")) {
                    // Unzip the file.
                    TrackUnzipper unzipper = new TrackUnzipper(filenames -> {
                        for (String file : filenames) {
                            // Record that this song is downloaded and that we can play it.
                            Song song = createSongFromFile(file, url);
                            // Add it to activities list of songs.
                            container.addTrack(song);
                        }
                    });
                    unzipper.execute(filepath);
                }
            }
            else if (status==DownloadManager.STATUS_FAILED) {
                Log.d("Download System", "Failed to download " + url + ". Trying again.");
                // Try downloading again.
                downloadTrack(url);
            }

        }

        cursor.close();
    }

    private Song createSongFromFile(String filename, String url) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        // Get the metadata from the song.
        Uri source = Uri.parse(filename);
        mmr.setDataSource(activity, source);

        // Extract information from the metadata.
        String songTitle = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String track_num = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER);
        byte[] album_art = mmr.getEmbeddedPicture();

        // Handle null case.
        songTitle = songTitle != null ? songTitle : "unnamed_song";
        albumName = albumName != null ? albumName : "no_album";
        artist = artist != null ? artist : "unnamed_artist";

        // Get rid of illegal characters in song name.
        songTitle = songTitle.replace(".", "dot").replace("#", "hash").replace("$", "S").replace("[", "(").replace("]", ")");

        Log.d("DownloadSystem", "Loaded song from " + filename + " <" +
                songTitle + ", " + albumName + ", " + artist + ", " + track_num + ">");

        // Check if a song with this information is already in the database.
        Song song = songDB.get(songTitle);
        Toast.makeText(activity, "Finished downloading " + songTitle, Toast.LENGTH_SHORT).show();
        if (song != null) {
            // Update its album art and return it.
            song.setAlbumCover(album_art);
        }
        else {
            // Create the song object from the metadata.
            song = new Song(filename, url, songTitle, albumName, artist, track_num, album_art);
        }
        song.setDownloaded(true);

        return song;
    }
}
