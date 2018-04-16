package com.cse110.flashbackmusicplayer;

import android.location.Location;
import android.location.LocationManager;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SongClassTest {

    private MockUserState state;

    @Before
    public void init() {
        state = new MockUserState();
    }

//    @Test
//    public void startedPlaying() throws Exception {
//        // Play a song in the following state.
//        byte[] albumCover = {0, 1, 0, 1};
//        Song song = new Song("filename", "url", "title", "album", "artist", "trackNumber", albumCover);
//
//        state.setDayOfWeek(Calendar.SUNDAY);
//        state.setTimeSegment(TimeSegment.NOON);
//        Location loc = new Location(LocationManager.GPS_PROVIDER);
//        loc.setLatitude(1.0); loc.setLongitude(-1.0);
//        state.setLocation(loc);
//
//        String time = "23:32:16"; state.setTime(time);
//        String date = "2/13/2018"; state.setDate(date);
//        String place = "Revelle, UCSD"; state.setPlace(place);
//        long sysTime = 100121503; state.setSystemTime(sysTime);
//
//        song.donePlaying(state);
//
//        // Check if the song object, remembers all of this.
//        assertEquals(time, song.getTime());
//        assertEquals(date, song.getDate());
//        assertEquals(place, song.getPlace());
//        assertEquals(sysTime, song.getSystemTime());
//
//        boolean[] daysOfWeek = new boolean[7];
//        daysOfWeek[Calendar.SUNDAY - 1] = true;
//        assertArrayEquals(daysOfWeek, song.getDaysOfWeek());
//
//        boolean[] timeSegments = new boolean[TimeSegment.numSegments];
//        timeSegments[TimeSegment.NOON.getIndex()] = true;
//        assertArrayEquals(timeSegments, song.getTimeSegments());
//
//        Location comp = song.getLocations().get(0);
//        assertTrue(loc.getLatitude() == comp.getLatitude());
//        assertTrue(loc.getLongitude() == comp.getLongitude());
//    }
//
//    @Test
//    public void getDaysOfWeek() throws Exception {
//        boolean[] daysOfWeek = new boolean[7];
//        // Test not playing the song on any day of the week.
//        byte[] albumCover = {0, 1, 0, 1};
//        Song song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        assertArrayEquals(daysOfWeek, song.getDaysOfWeek());
//
//        // Test playing once on Sunday.
//        state.setDayOfWeek(Calendar.SUNDAY);
//        daysOfWeek[0] = true;
//        song.donePlaying(state);
//        assertArrayEquals(daysOfWeek, song.getDaysOfWeek());
//
//        // Play a song on Monday and Tuesday.
//        song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        daysOfWeek = new boolean[7];
//        // monday
//        state.setDayOfWeek(Calendar.MONDAY);
//        daysOfWeek[1] = true;
//        song.donePlaying(state);
//        // tuesday
//        state.setDayOfWeek(Calendar.TUESDAY);
//        daysOfWeek[2] = true;
//        song.donePlaying(state);
//        assertArrayEquals(daysOfWeek, song.getDaysOfWeek());
//
//        // Play a song on all 3 days of the week.
//        song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        daysOfWeek = new boolean[7];
//        for (int i = 0; i < 7; i++) {
//            state.setDayOfWeek(i + 1);
//            daysOfWeek[i] = true;
//            song.donePlaying(state);
//        }
//        assertArrayEquals(daysOfWeek, song.getDaysOfWeek());
//    }
//
//    @Test
//    public void getTimeSegments() throws Exception {
//        boolean[] timeSegments = new boolean[TimeSegment.numSegments];
//        // Test not playing the song in any segment.
//        byte[] albumCover = {0, 1, 0, 1};
//        Song song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        assertArrayEquals(timeSegments, song.getTimeSegments());
//
//        // Test playing in the morning.
//        state.setTimeSegment(TimeSegment.MORNING);
//        timeSegments[TimeSegment.MORNING.getIndex()] = true;
//        song.donePlaying(state);
//        assertArrayEquals(timeSegments, song.getTimeSegments());
//
//        // Play a song in the noon and evening.
//        song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//
//        // noon
//        timeSegments = new boolean[TimeSegment.numSegments];
//        state.setTimeSegment(TimeSegment.NOON);
//        timeSegments[TimeSegment.NOON.getIndex()] = true;
//        song.donePlaying(state);
//        // evening
//        state.setTimeSegment(TimeSegment.EVENING);
//        timeSegments[TimeSegment.EVENING.getIndex()] = true;
//        song.donePlaying(state);
//        assertArrayEquals(timeSegments, song.getTimeSegments());
//
//        // Play a song in all 3 sections of the day.
//        song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        // morning
//        timeSegments = new boolean[TimeSegment.numSegments];
//        state.setTimeSegment(TimeSegment.MORNING);
//        timeSegments[TimeSegment.MORNING.getIndex()] = true;
//        song.donePlaying(state);
//        // noon
//        state.setTimeSegment(TimeSegment.NOON);
//        timeSegments[TimeSegment.NOON.getIndex()] = true;
//        song.donePlaying(state);
//        // evening
//        state.setTimeSegment(TimeSegment.EVENING);
//        timeSegments[TimeSegment.EVENING.getIndex()] = true;
//        song.donePlaying(state);
//        assertArrayEquals(timeSegments, song.getTimeSegments());
//    }
//
//    @Test
//    public void getLocations() throws Exception {
//        // Create a bunch of different locations.
//        ArrayList<Location> locations = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Location location = new Location(LocationManager.GPS_PROVIDER);
//            location.setLongitude(i*i*0.14 - 6.09*i + 14.605);
//            location.setLatitude(-i*i*5.14 + 110.11*i + 9.403);
//            locations.add(location);
//        }
//
//        // "Play" the song in these locations.
//        byte[] albumCover = {0, 1, 0, 1};
//        Song song = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        for (Location location : locations) {
//            state.setLocation(location);
//            song.donePlaying(state);
//        }
//
//        // Check if all the locations we played were added to the array.
//        for (int i = 0; i < locations.size(); i++) {
//            Location comp = song.getLocations().get(i);
//            Location loc = locations.get(i);
//            assertTrue(loc.getLatitude() == comp.getLatitude());
//            assertTrue(loc.getLongitude() == comp.getLongitude());
//        }
//    }
//
//    @Test
//    public void getters() throws Exception {
//        // Create a couple random songs using the first constructor.
//        byte[] albumCover = {0, 1, 0, 1};
//        Song s1 = new Song("filename", "title", "album", "artist", "trackNumber", albumCover);
//        Song s2 = new Song("_123_go", "123 go", "New and Best of Keaton Simons", "Keaton Simons",
//                "1/10", albumCover);
//
//        // Make sure that all of the getters return the correct field.
//        assertEquals("filename", s1.getFilename());
//        assertEquals("title", s1.getTitle());
//        assertEquals("album", s1.getAlbum());
//        assertEquals("artist", s1.getArtist());
//        assertEquals("trackNumber", s1.getTrackNumber());
//        assertArrayEquals(albumCover, s1.getAlbumCover());
//
//        assertEquals("_123_go", s2.getFilename());
//        assertEquals("123 go", s2.getTitle());
//        assertEquals("New and Best of Keaton Simons", s2.getAlbum());
//        assertEquals("Keaton Simons", s2.getArtist());
//        assertEquals("1/10", s2.getTrackNumber());
//        assertArrayEquals(albumCover, s2.getAlbumCover());
//    }

}