package bravest.ptt.efastquery.callback;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by root on 4/6/17.
 *  swing left and right , provide two callbacks , onRight, onLeft
 */

public class DoubleClickDetector extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG = "DoubleClickDetector";

    public interface Callback {
        void onDoubleTap();
    }

    private Callback mCallback;

    public DoubleClickDetector(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: event = " + e.getAction());
        mCallback.onDoubleTap();
        return true;
    }
}
