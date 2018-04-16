package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

/**
 * This is a priority queue of songs that orders song by how likely they are to be played
 * by the user.
 */
public class SongDatabase {

    // The state of the user, last time songDatabase was updated.
    private Location location;
    private String user;
    private int dayOfYear;

    // The array that stores all the loaded songs.
    private ArrayList<Song> songs;

    private UserState state;


    public SongDatabase(UserState state) {
        this.state = state;

        // Cache the state of the user for future reference.
        this.location = state.getLocation();
        this.user = state.getUser();
        this.dayOfYear = state.getDayOfYear();

        // Creates an array and a max heap that will store all the songs.
        this.songs = new ArrayList<>();
    }

    private class SongComparator implements Comparator<Song>
    {
        @Override
        public int compare(Song s1, Song s2)
        {

            if (s1.getPriority() > s2.getPriority())
            {
                return -1;
            }
            if (s1.getPriority() < s2.getPriority())
            {
                return 1;
            }

            // Try to break ties.
            // Break ties by looking at location.
            boolean s1PlayedHere = false, s2PlayedHere = false;
            for (Location songLocation : s1.getLocations()) {
                // Turn degrees to minutes, turn minutes to nautical miles, turn nautical miles to feet.
                float dist = songLocation.distanceTo(location);

                if (dist < 1000) {
                    s1PlayedHere = true;
                    break;
                }
            }
            for (Location songLocation : s1.getLocations()) {
                // Turn degrees to minutes, turn minutes to nautical miles, turn nautical miles to feet.
                float dist = songLocation.distanceTo(location);

                if (dist < 1000) {
                    s2PlayedHere = true;
                    break;
                }
            }
            if (s1PlayedHere != s2PlayedHere) {
                return s1PlayedHere ? -1 : 1;
            }

            // Break ties by week played.
            boolean s1PlayedRecent = false, s2PlayedRecent = false;
            Calendar calendar = Calendar.getInstance();
            for (long time : s1.getTimes()) {
                // Convert system time to day of year.
                calendar.setTimeInMillis(time);
                int songDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                if (songDayOfYear > dayOfYear - 7 && songDayOfYear <= dayOfYear) {
                    s1PlayedRecent = true;
                    break;
                }
            }
            for (long time : s2.getTimes()) {
                // Convert system time to day of year.
                calendar.setTimeInMillis(time);
                int songDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                if (songDayOfYear > dayOfYear - 7 && songDayOfYear <= dayOfYear) {
                    s2PlayedRecent = true;
                    break;
                }
            }
            if (s1PlayedRecent != s2PlayedRecent) {
                return s1PlayedRecent ? -1 : 1;
            }

            // TODO: Break ties by friend played.
            if (s1.isPlayedByFriend() != s2.isPlayedByFriend()) {
                return s1.isPlayedByFriend() ? -1 : 1;
            }

            if (s1.isFavorited() != s2.isFavorited()) {
                // Return whichever song is favorited.
                return s1.isFavorited() ? -1 : 1;
            }

            // Otherwise, the one that was most recently played has a greater priority.
            return s1.getSystemTime() > s2.getSystemTime() ? -1 : 1;
        }
    }

    public boolean hasStateChanged() {
        // Compared the cached state with the actual one.
        double lat = state.getLocation().getLatitude();
        double lon = state.getLocation().getLongitude();
        boolean changed = !(lat == location.getLatitude() && lon == location.getLongitude()) ||
                state.getDayOfYear() != dayOfYear;
        Log.d("SongDatabase", "The database state has " + (changed ? "changed" : "not changed"));
        return changed;

    }

    // Creates a list of songs ordered by priority.
    public List<Song> generateVibeList() {
        // Cache the state of the user for future reference.
        location = state.getLocation();
        user = state.getUser();
        dayOfYear = state.getDayOfYear();

        // Copy the list of songs and order it.
        ArrayList<Song> vibeList = new ArrayList<>();
        for (Song song : songs) {
            if (calculatePriority(song) > 0) {
                vibeList.add(song);
            }
        }
        vibeList.sort(new SongComparator());

        Log.d("SongDatabase", "Generated VibeList");

        return vibeList;
    }

    // Adds an element to the list of songs.
    public void insert(Song song) {
        songs.add(song);
    }

    // Gets a song from songs using name as key. Probably just do a linear search.
    // Used during regular mode, when user chooses a song to play.
    public Song get(String name) {
        // Go through all the songs in the queue and check if any match.
        for (Song song : songs) {
            if (song.getTitle().equals(name)) {
                return song;
            }
        }

        return null;
    }

    public ArrayList<Song> getAlbum(String albumName) {
        // Go through all the songs in the queue and check if any album names match.
        ArrayList<Song> album = new ArrayList<>();
        for (Song song : songs) {
            if (song.getAlbum().equals(albumName)) {
                album.add(song);
            }
        }
        album.sort((s1, s2) -> {
            int track1 = Integer.parseInt(s1.getTrackNumber().split("/")[0]);
            int track2 = Integer.parseInt(s2.getTrackNumber().split("/")[0]);
            return track1 < track2 ? -1 : 1;
        });
        return album;
    }

    /**
     * Calculates the priority of this song using the current state of the user
     * @return How likely this song is to be played.
     */
    public int calculatePriority(Song song) {
        int priority = 0;

        if (song.isDisliked()) {
            song.setPriority(0);
            return 0;
        }

        // Check if the current user location is near one where this song was played.
        for (Location songLocation : song.getLocations()) {
            // Turn degrees to minutes, turn minutes to nautical miles, turn nautical miles to feet.
            float dist = songLocation.distanceTo(location);

            if (dist < 1000) {
                // Increase the priority, and stop calculating any other locations.
                priority++;
                break;
            }
        }

        // Calculate if song was played within the last week.
        Calendar calendar = Calendar.getInstance();
        for (long time : song.getTimes()) {
            // Convert system time to day of year.
            calendar.setTimeInMillis(time);
            int songDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

            if (songDayOfYear > dayOfYear - 7 && songDayOfYear <= dayOfYear) {
                priority++;
                break;
            }
        }

        // check if it was played by a friend.
        if (song.isPlayedByFriend()) {
            priority++;
        }

        // Cache the priority.
        song.setPriority(priority);

        return priority;
    }

}
