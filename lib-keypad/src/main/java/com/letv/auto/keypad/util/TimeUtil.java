package com.letv.auto.keypad.util;

import android.os.SystemClock;

/**
 * Created by adam on 4/10/15.
 */
public class TimeUtil {
    /**
     * Whether the time interval of two hits view is short.
     * @param hits Used to hold time.
     * @param thresholdMilliseconds The threshold time value.
     * @return
     */
    public static boolean isHitsIntervalTooShort(long[] hits, int thresholdMilliseconds) {
        System.arraycopy(hits, 1, hits, 0, hits.length - 1);
        hits[hits.length - 1] = SystemClock.uptimeMillis();
        return (hits[0] > SystemClock.uptimeMillis() - thresholdMilliseconds);
    }
}
