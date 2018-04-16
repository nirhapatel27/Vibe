package com.cse110.flashbackmusicplayer;


import android.support.test.espresso.PerformException;
import android.support.test.espresso.action.CoordinatesProvider;
import android.support.test.espresso.util.HumanReadables;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by Amritansh on 2/18/2018.
 */

public class SeekBarThumbCoordinatesProvider implements CoordinatesProvider {
    int mProgress;

    public SeekBarThumbCoordinatesProvider(int progress) {
        mProgress = progress;
    }

    private static float[] getVisibleLeftTop(View view) {
        final int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        return new float[]{ (float) xy[0], (float) xy[1] };
    }

    @Override
    public float[] calculateCoordinates(View view) {
        if (!(view instanceof SeekBar)) {
            throw new PerformException.Builder()
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new RuntimeException(String.format("SeekBar expected"))).build();
        }
        SeekBar seekBar = (SeekBar) view;
        int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
        double progress = mProgress == 0 ? seekBar.getProgress() : mProgress;
        int xPosition = (int) (seekBar.getPaddingLeft() + width * progress / seekBar.getMax());
        float[] xy = getVisibleLeftTop(seekBar);
        return new float[]{ xy[0] + xPosition, xy[1] + 10 };
    }
}
