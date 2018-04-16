package com.cse110.flashbackmusicplayer;

import java.util.ArrayList;
import java.util.List;

import static com.cse110.flashbackmusicplayer.MainActivity.songDB;

public class DefaultSort implements SortStrategy {

    public ArrayList<String> sort(List<String> input) {
        ArrayList<String> sorted = new ArrayList<>(input);

        sorted.sort((s1, s2) -> {
            List<Long> times1 = songDB.get(s1).getTimes();
            List<Long> times2 = songDB.get(s2).getTimes();
            return times1.get(times1.size() - 1) < times2.get(times2.size() - 1) ? -1 : 1;
        });

        return sorted;
    }
}
