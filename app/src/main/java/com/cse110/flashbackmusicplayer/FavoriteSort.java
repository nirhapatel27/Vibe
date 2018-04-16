package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;


public class FavoriteSort implements SortStrategy {
    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> stateSorted = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            Song song = songDB.get(input.get(i));
            //adds all the favorited songs first, in the order they appear in the database
            if (song.isFavorited()) {
                stateSorted.add(input.get(i));
            }
        }
        for (int k = 0; k < input.size(); k++) {
            Song song = songDB.get(input.get(k));
            //adds all the nonfavorited songs last
            if (!song.isFavorited()) {
                stateSorted.add(input.get(k));
            }
        }
        return stateSorted;
    }
}
