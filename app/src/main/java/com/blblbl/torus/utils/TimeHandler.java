package com.blblbl.torus.utils;

import android.os.SystemClock;
public abstract class TimeHandler {

    public static long getTime() {
        return SystemClock.uptimeMillis()-offset;
    }

    public static void onPause() {
        mTimePause=getTime();
    }

    public static void onResume() {
        offset += getTime() - mTimePause;
    }

    public static void resetOffset() {
        offset=SystemClock.uptimeMillis();
        mTimePause=offset;
    }

    private static long mTimePause=0;
    private static long offset;
}
