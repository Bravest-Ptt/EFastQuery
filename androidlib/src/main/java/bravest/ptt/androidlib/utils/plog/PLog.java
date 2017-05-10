package bravest.ptt.androidlib.utils.plog;

import android.util.Log;

/**
 * Created by root on 2/17/17.
 */

public class PLog {
    private static final String TAG = "ptt";
    private static final boolean DEBUG = true;

    public static void log(Object o) {
        if (DEBUG) {
            Log.d(TAG, o.toString());
        }
    }

    public static void d(String tag, String str) {
        if (DEBUG) {
            Log.d(tag, str);
        }
    }
}
