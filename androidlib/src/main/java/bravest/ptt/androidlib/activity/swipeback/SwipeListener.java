package bravest.ptt.androidlib.activity.swipeback;

/**
 * Created by root on 6/16/17.
 */

public interface SwipeListener {
    /**
     * Invoke when state changed
     *
     * @param state flag to describe scroll state
     * @param scrollPercent scroll percent of this view
     */
    void onScrollStateChanged(int state, float scrollPercent);

    /**
     * Invoke when edge touched
     * @param edgeFlag edge flag describing the edge being touched
     */
    void onEdgeTouch(int edgeFlag);

    /**
     * Invoke when scroll percent over the threshold for the first time
     */
    void onScrollOverThreshold();
}
