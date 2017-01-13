package bravest.ptt.efastquery.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import java.util.HashMap;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.utils.Utils;

/**
 * Created by root on 1/11/17.
 */

public class FABManager {
    private static final String TAG = "FABManager";
    private static final int ANIMATION_DELAY = 250;

    public static final int ACTION_INPUT_NULL = 0;
    public static final int ACTION_TRANS_SUCCESS = 1;
    public static final int ACTION_ITEM_SELECTED = 2;
    public static final int ACTION_ITEM_DESELECTED = 3;

    private HashMap<Integer, View> mFabsMap;
    private Context mContext;

    public FABManager(Context context) {
        mFabsMap = new HashMap<>();
        mContext = context;
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
        Log.d(TAG, "popUpFAB: action = " + action);
        switch (action) {
            case ACTION_INPUT_NULL:
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

    private void popUpAnimation(final View view) {
        if (view.getVisibility() == View.VISIBLE) {

        }
        Log.d(TAG, "popUpAnimation: ");

        int height = view.getMeasuredHeight();
        Log.d(TAG, "popUpAnimation: height = " + height);
        float targetY = Utils.getScreenHeight(mContext) * 1 / 4  - Utils.getStatusBarHeight(mContext)- height ;
        float srcY = Utils.getScreenHeight(mContext) * 1 / 4 + height;
        float targetX = Utils.getScreenWidth(mContext) * 1 / 6;
        float visibleCount = 0;
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        for (Integer key : mFabsMap.keySet()) {
            if (mFabsMap.get(key).getId() == view.getId()) {
                continue;
            }
            visibleCount += (mFabsMap.get(key).getVisibility() == View.VISIBLE ? 1 : 0);
        }
        final float x = targetX + (visibleCount * (view.getMeasuredWidth() + view.getMeasuredWidth() * 1 / 10));


        Log.d(TAG, "popUpAnimation: targetY = " + targetY);
        Log.d(TAG, "popUpAnimation: srcY = " + srcY);
        Log.d(TAG, "POP targetx= " + targetX);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(view, "translationY", srcY, targetY );
        transAnimator.setDuration(ANIMATION_DELAY);
        transAnimator.setInterpolator(new OvershootInterpolator());
        transAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setTranslationX(x);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });

        ObjectAnimator alphaAnimator =  ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        alphaAnimator.setDuration(ANIMATION_DELAY);
        alphaAnimator.setInterpolator(new LinearInterpolator());

        set.playTogether(transAnimator, alphaAnimator);
        set.start();
    }

    private void pullDownAnimation(final View view) {
//        if (view.getVisibility() == View.GONE) {
//            return;
//        }
        int height = view.getMeasuredHeight();
        float srcY = Utils.getScreenHeight(mContext) * 1 / 4  - Utils.getStatusBarHeight(mContext)- height ;
        float targetY = Utils.getScreenHeight(mContext) * 1 / 4 + height;
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator transAnimator = ObjectAnimator.ofFloat(view, "translationY", srcY, targetY);
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
