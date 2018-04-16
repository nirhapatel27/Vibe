package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager implements SongObserver, FirebaseSubject {

    private DatabaseReference myFirebaseRef;

    public FirebaseManager (SongDatabase database) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myFirebaseRef = firebaseDatabase.getReference();

        // If any songs are added in the future, add them to the database.
        myFirebaseRef.child("Songs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String title = dataSnapshot.getKey();
                Log.d("Firebase Manager", "Added a new child: " + title);
                // If we don't have a song with this title in the database, add it.
                if (database.get(title) == null) {
                    DataSnapshot metadata = dataSnapshot.child("metadata");

                    String album = metadata.child("album").getValue(String.class);
                    String artist = metadata.child("artist").getValue(String.class);
                    String filename = metadata.child("filename").getValue(String.class);
                    String url = metadata.child("url").getValue(String.class);
                    String tracknumber = metadata.child("tracknumber").getValue(String.class);

                    Song song = new Song(filename, url, title, album, artist, tracknumber, null);
                    database.insert(song);

                    song.registerObserver(FirebaseManager.this);
                    FirebaseManager.this.registerObserver(song);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void registerObserver(FirebaseObserver song) {
        Log.d("Firebase Manager", "Registered the song " + song.getTitle());
        // If we already have this song in the database, do nothing. Else add it.
        myFirebaseRef.child("Songs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(song.getTitle())) {
                    Map<String,Object> entry = new HashMap<>();
                    entry.put("title", song.getTitle());
                    entry.put("album", song.getAlbum());
                    entry.put("artist", song.getArtist());
                    entry.put("filename", song.getFilename());
                    entry.put("url", song.getURL());
                    entry.put("tracknumber", song.getTrackNumber());
                    myFirebaseRef.child("Songs").child(song.getTitle()).child("metadata").setValue(entry);
                }
                getPlaybackHistoryRef(song.getTitle()).addChildEventListener(new SongPlayedEventListener(song));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }


    @Override
    public void update(Song song, String user, long time, Location location) {
        Log.d("Firebase Manager", "Updating " + song.getTitle() + " in Firebase database");
        DatabaseReference history = getPlaybackHistoryRef(song.getTitle());
        Map<String,Object> entry = new HashMap<>();
        entry.put("user", user);
        entry.put("location", location);
        entry.put("time", time);
        history.push().setValue(entry);
    }

    private class SongPlayedEventListener implements ChildEventListener {

        private FirebaseObserver song;

        public SongPlayedEventListener(FirebaseObserver song) {
            this.song = song;
        }

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("Firebase Manager", "Firebase song node " + song.getTitle() + " has been updated by another user");
            String user = dataSnapshot.child("user").getValue(String.class);
            double latitude = dataSnapshot.child("location").child("latitude").getValue(Double.class);
            double longitude = dataSnapshot.child("location").child("longitude").getValue(Double.class);
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLongitude(longitude);
            location.setLatitude(latitude);
            long time = dataSnapshot.child("time").getValue(Long.class);
            song.update(user, location, time);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    private DatabaseReference getPlaybackHistoryRef(String title) {
        return myFirebaseRef.child("Songs").child(title).child("PlaybackHistory");
    }
}
