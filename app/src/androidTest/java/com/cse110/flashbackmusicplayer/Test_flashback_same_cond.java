package com.cse110.flashbackmusicplayer;

/**
 * Created by Amritansh on 2/18/2018.
 */

import android.location.Location;
import android.location.LocationManager;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.SeekBar;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;

import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
@LargeTest
@RunWith(AndroidJUnit4.class)

public class Test_flashback_same_cond {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testFlashbackCond () {
        MockUserState state = new MockUserState();
        MainActivity.userState = state;

        //changind db in mainAcivity
        MainActivity.songDB = new SongDatabase(state);
//        MainActivity.songDB.generateFlashbackList();
//        Song s1 = new Song("after_the_storm", "After The Storm", "Origins - The Best of Terry Oldfield", "Terry Oldfield", "6", null);
//        Song s2 = new Song("flight_of_the_eagle", "Flight of the Eagle", "Origins - The Best of Terry Oldfield", "Terry Oldfield", "2", null);
//
//        //Only these two songs will be playable
//        MainActivity.songDB.insert(s1);
//        MainActivity.songDB.insert(s2);
//
//        Location loc = new Location(LocationManager.GPS_PROVIDER);
//
//        //setting up some random values
//        state.setDayOfWeek(2);
//        state.setDate("2/23/2018");
//        loc.setLongitude(12.0);
//        loc.setLatitude(-114.5);
//        state.setTimeSegment(TimeSegment.EVENING);
        state.setSystemTime(1);
//        state.setLocation(loc);
        state.setTime("10:20:00");


        //Through the mock state, the songs somehow dont need to be played fully
        //So not dragging them all te way

        //Finding andclickng the tracks button
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.tracksDisplayButton), withText("Tracks"),
                        isDisplayed()));
        appCompatButton.perform(click());

        //Playing after the storm
        DataInteraction appCompatTextView = onData(anything())
                .inAdapterView(allOf(withId(R.id.songsView)))
                .atPosition(1);
        appCompatTextView.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatbBar = onView(allOf(withId(R.id.slideTrack), isDisplayed()));
        appCompatbBar.perform(scrubSeekBarAction(2500000));

        //Going back
        ViewInteraction backButton = onView(
                allOf(withId(R.id.backButton), isDisplayed()));
        backButton.perform(click());

        appCompatButton.perform(click());

        state.setSystemTime(2);

        //PLaying Flying the Eagle
        DataInteraction appCompatTextView2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.songsView)))
                .atPosition(13);
        appCompatTextView2.perform(click());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        appCompatbBar.perform(scrubSeekBarAction(3000000));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Going back
        backButton.perform(click());

        ///Launching flashback mode
        ViewInteraction appFlashButton = onView(
                allOf(withId(R.id.switchMode), isDisplayed()));
        appFlashButton.perform(click());

        //Firsts song played should be Flying the eagle
        //TODO is not tho. Fix this
        ViewInteraction titleView2 = onView(
                allOf(withId(R.id.songTitle), isDisplayed()));
        titleView2.check(matches(withText("Flight of the Eagle\nTerry Oldfield")));

    }

    public static ViewAction scrubSeekBarAction(int progress) {
        return actionWithAssertions(new GeneralSwipeAction(
                Swipe.SLOW,
                new SeekBarThumbCoordinatesProvider(0),
                new SeekBarThumbCoordinatesProvider(progress),
                Press.PINPOINT));
    }



}


