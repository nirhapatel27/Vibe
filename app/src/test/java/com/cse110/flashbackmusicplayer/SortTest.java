package com.cse110.flashbackmusicplayer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SortTest {

    SortStrategy sorter = null;
    UserState state;

    @Before
    public void setup() {
        state = new MockUserState();
        MainActivity.songDB = new SongDatabase(state);
    }

    // Test Album Sort.
    @Test
    public void testAlbumSort() {
        sorter = new AlbumSort();

        Song s1 = new Song("s1", "url1", "s1", "album", "artist", "trackNumber", null);
        Song s2 = new Song("s2", "url2", "s2", "albume", "artist", "trackNumber", null);
        Song s3 = new Song("s3", "url3", "s3", "beta", "artist", "trackNumber", null);
        Song s4 = new Song("s4", "url4", "s4", "frog", "artist", "trackNumber", null);
        Song s5 = new Song("s5", "url5", "s5", "123a", "artist", "trackNumber", null);

        List<String> songs = new ArrayList<>();
        songs.add(s1.getTitle());
        songs.add(s2.getTitle());
        songs.add(s3.getTitle());
        songs.add(s4.getTitle());
        songs.add(s5.getTitle());

        MainActivity.songDB.insert(s1);
        MainActivity.songDB.insert(s2);
        MainActivity.songDB.insert(s3);
        MainActivity.songDB.insert(s4);
        MainActivity.songDB.insert(s5);

        List<String> sorted = sorter.sort(songs);

        assertEquals("s5", sorted.get(0));
        assertEquals("s1", sorted.get(1));
        assertEquals("s2", sorted.get(2));
        assertEquals("s3", sorted.get(3));
        assertEquals("s4", sorted.get(4));
    }

    // Test Artist Sort.
    @Test
    public void testArtistSort() {
        sorter = new ArtistSort();

        Song s1 = new Song("s1", "url1", "s1", "album", "fox", "trackNumber", null);
        Song s2 = new Song("s2", "url2", "s2", "albume", "goro", "trackNumber", null);
        Song s3 = new Song("s3", "url3", "s3", "beta", "Kitty", "trackNumber", null);
        Song s4 = new Song("s4", "url4", "s4", "frog", "kitty", "trackNumber", null);
        Song s5 = new Song("s5", "url5", "s5", "123a", "cat", "trackNumber", null);

        List<String> songs = new ArrayList<>();
        songs.add(s1.getTitle());
        songs.add(s2.getTitle());
        songs.add(s3.getTitle());
        songs.add(s4.getTitle());
        songs.add(s5.getTitle());

        MainActivity.songDB.insert(s1);
        MainActivity.songDB.insert(s2);
        MainActivity.songDB.insert(s3);
        MainActivity.songDB.insert(s4);
        MainActivity.songDB.insert(s5);

        List<String> sorted = sorter.sort(songs);

        assertEquals("s5", sorted.get(0));
        assertEquals("s1", sorted.get(1));
        assertEquals("s2", sorted.get(2));
        assertEquals("s3", sorted.get(3));
        assertEquals("s4", sorted.get(4));
    }

    // Test Favorite Sort.
    @Test
    public void testFavoriteSort() {
        sorter = new FavoriteSort();

        Song s1 = new Song("s1", "url1", "s1", "album", "fox", "trackNumber", null);
        s1.setLikedStatus(Song.LIKED_STATUS.FAVORITED);
        Song s2 = new Song("s2", "url2", "s2", "albume", "goro", "trackNumber", null);
        s2.setLikedStatus(Song.LIKED_STATUS.DISLIKED);
        Song s3 = new Song("s3", "url3", "s3", "beta", "Kitty", "trackNumber", null);
        s3.setLikedStatus(Song.LIKED_STATUS.NEUTRAL);
        Song s4 = new Song("s4", "url4", "s4", "frog", "kitty", "trackNumber", null);
        s4.setLikedStatus(Song.LIKED_STATUS.FAVORITED);
        Song s5 = new Song("s5", "url5", "s5", "123a", "cat", "trackNumber", null);
        s5.setLikedStatus(Song.LIKED_STATUS.FAVORITED);

        List<String> songs = new ArrayList<>();
        songs.add(s1.getTitle());
        songs.add(s2.getTitle());
        songs.add(s3.getTitle());
        songs.add(s4.getTitle());
        songs.add(s5.getTitle());

        MainActivity.songDB.insert(s1);
        MainActivity.songDB.insert(s2);
        MainActivity.songDB.insert(s3);
        MainActivity.songDB.insert(s4);
        MainActivity.songDB.insert(s5);

        List<String> sorted = sorter.sort(songs);

        assertEquals("s1", sorted.get(0));
        assertEquals("s4", sorted.get(1));
        assertEquals("s5", sorted.get(2));
        assertEquals("s2", sorted.get(3));
        assertEquals("s3", sorted.get(4));
    }

    // Test Title Sort.
    @Test
    public void testTitleSort() {
        sorter = new TitleSort();

        Song s1 = new Song("s1", "url1", "fox", "album", "fox", "trackNumber", null);
        Song s2 = new Song("s2", "url2", "goro", "albume", "goro", "trackNumber", null);
        Song s3 = new Song("s3", "url3", "Kitty", "beta", "Kitty", "trackNumber", null);
        Song s4 = new Song("s4", "url4", "kitty", "frog", "kitty", "trackNumber", null);
        Song s5 = new Song("s5", "url5", "cat", "123a", "cat", "trackNumber", null);

        List<String> songs = new ArrayList<>();
        songs.add(s1.getTitle());
        songs.add(s2.getTitle());
        songs.add(s3.getTitle());
        songs.add(s4.getTitle());
        songs.add(s5.getTitle());

        MainActivity.songDB.insert(s1);
        MainActivity.songDB.insert(s2);
        MainActivity.songDB.insert(s3);
        MainActivity.songDB.insert(s4);
        MainActivity.songDB.insert(s5);

        List<String> sorted = sorter.sort(songs);

        assertEquals("cat", sorted.get(0));
        assertEquals("fox", sorted.get(1));
        assertEquals("goro", sorted.get(2));
        assertEquals("Kitty", sorted.get(3));
        assertEquals("kitty", sorted.get(4));
    }


}