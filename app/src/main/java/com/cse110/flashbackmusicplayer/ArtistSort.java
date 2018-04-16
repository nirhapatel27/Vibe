package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;


public class ArtistSort implements SortStrategy{
    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> sorted = new ArrayList<>(input);

        sorted.sort(Comparator.comparing(s -> songDB.get(s).getArtist().toLowerCase()));

        return sorted;
    }
}
