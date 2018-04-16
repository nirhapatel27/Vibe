package com.cse110.flashbackmusicplayer;


import android.location.Location;
import android.location.LocationManager;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.Tap;
import android.support.test.espresso.util.HumanReadables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.SeekBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestFlashback {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testFlashback() {
        MockUserState state = new MockUserState();
        MainActivity.userState = state;

        //changind db in mainAcivity
//        MainActivity.songDB = new SongDatabase(state);
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
//        state.setSystemTime(1);
//        state.setLocation(loc);
//        state.setTime("10:20:00");
//
//        ViewInteraction appCompatButton = onView(
//                allOf(withId(R.id.tracksDisplayButton), withText("Tracks"),
//                        isDisplayed()));
//        appCompatButton.perform(click());
//
//        DataInteraction linearLayout = onData(anything())
//                .inAdapterView(allOf(withId(R.id.songsView),
//                        childAtPosition(
//                                withId(R.id.listViews),
//                                1)))
//                .atPosition(1);
//        linearLayout.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatbBar = onView(allOf(withId(R.id.slideTrack), isDisplayed()));
//        appCompatbBar.perform(scrubSeekBarAction(2000000));
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatButton2 = onView(
//                allOf(withId(R.id.backButton),
//                        isDisplayed()));
//        appCompatButton2.perform(click());
//
//        state.setSystemTime(2);
//
//        DataInteraction linearLayout2 = onData(anything())
//                .inAdapterView(allOf(withId(R.id.songsView),
//                        childAtPosition(
//                                withId(R.id.listViews),
//                                1)))
//                .atPosition(13);
//        linearLayout2.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        appCompatbBar.perform(scrubSeekBarAction(350000));
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatButton3 = onView(
//                allOf(withId(R.id.backButton),
//                        childAtPosition(
//                                childAtPosition(
//                                        withClassName(is("android.support.design.widget.CoordinatorLayout")),
//                                        0),
//                                2),
//                        isDisplayed()));
//        appCompatButton3.perform(click());
//
//        // Added a sleep statement to match the app's execution delay.
//        // The recommended way to handle such scenarios is to use Espresso idling resources:
//        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        ViewInteraction appCompatButton4 = onView(
//                allOf(withId(R.id.switchMode), withText("Enter Flashback Mode"),
//                        childAtPosition(
//                                allOf(withId(R.id.coordinatorLayout3),
//                                        childAtPosition(
//                                                withClassName(is("android.support.design.widget.CoordinatorLayout")),
//                                                0)),
//                                0),
//                        isDisplayed()));
//        appCompatButton4.perform(click());
//
//        ViewInteraction titleView2 = onView(
//                allOf(withId(R.id.songTitle), isDisplayed()));
//        titleView2.check(matches(withText("Flight of the Eagle\nTerry Oldfield")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static ViewAction scrubSeekBarAction(int progress) {
        return actionWithAssertions(new GeneralSwipeAction(
                Swipe.SLOW,
                new SeekBarThumbCoordinatesProvider(0),
                new SeekBarThumbCoordinatesProvider(progress),
                Press.PINPOINT));
    }

}
