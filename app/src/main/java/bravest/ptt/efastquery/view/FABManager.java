package bravest.ptt.efastquery.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bravest.ptt.efastquery.R;

/**
 * Created by root on 1/11/17.
 */

public class FABManager {
    private static final String TAG = "FABManager";
    private static final int ANIMATION_DELAY = 300;

    public static final int ACTION_INPUT_NULL = 0;
    public static final int ACTION_TRANS_SUCCESS = 1;
    public static final int ACTION_ITEM_SELECTED = 2;
    public static final int ACTION_ITEM_DESELECTED = 3;

    private HashMap<Integer, View> mFabsMap;

    public FABManager() {
        mFabsMap = new HashMap<>();
    }

    public void addFAB(View view) {
        if (view == null) {
            return;
        }
        if (mFabsMap.containsKey(view.getId())) {
            return;
        }
        mFabsMap.put(view.getId(), view);
    }

    public void popUpFAB(int action) {
        switch (action) {
            case ACTION_INPUT_NULL:
                goneAllFABs();
                break;
            case ACTION_TRANS_SUCCESS:
                popUpAnimation(mFabsMap.get(R.id.main_panel_speak));
                popUpAnimation(mFabsMap.get(R.id.main_panel_favorite));
                break;
            case ACTION_ITEM_SELECTED:
                break;
            case ACTION_ITEM_DESELECTED:
                break;
            default:
                break;
        }
    }

    public void pullDownFAB(int action) {
        switch (action) {
            case ACTION_INPUT_NULL:
                //goneAllFABs();
                pullDownAnimation(mFabsMap.get(R.id.main_panel_speak));
                pullDownAnimation(mFabsMap.get(R.id.main_panel_favorite));
                break;
            case ACTION_TRANS_SUCCESS:
                break;
            case ACTION_ITEM_SELECTED:
                break;
            case ACTION_ITEM_DESELECTED:
                break;
            default:
                break;
        }
    }

    public void goneAllFABs() {
        for (Integer key : mFabsMap.keySet()) {
            mFabsMap.get(key).setVisibility(View.GONE);
        }
    }

    private void popUpAnimation(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return;
        }
        Log.d(TAG, "popUpAnimation: ");
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);

        int[] locations = new int[2];
        int height = view.getMeasuredHeight();
        view.getLocationOnScreen(locations);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(view, "translationY", (float) (locations[1] - height - height), locations[1]);
        transAnimator.setDuration(ANIMATION_DELAY);
        transAnimator.setInterpolator(new OvershootInterpolator());

        ObjectAnimator alphaAnimator =  ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alphaAnimator.setDuration(ANIMATION_DELAY);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        set.playTogether(transAnimator, alphaAnimator);
        set.start();
    }

    private void pullDownAnimation(final View view) {
        if (view.getVisibility() == View.GONE) {
            return;
        }
        int[] locations = new int[2];
        int height = view.getMeasuredHeight();
        view.getLocationOnScreen(locations);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(view, "translationY", locations[1], (float) (locations[1] - height - height));
        transAnimator.setDuration(ANIMATION_DELAY);
        transAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator alphaAnimator =  ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        alphaAnimator.setDuration(ANIMATION_DELAY);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
        set.playTogether(transAnimator, alphaAnimator);
        set.start();
    }

}
