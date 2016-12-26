package bravest.ptt.efastquery.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import bravest.ptt.efastquery.R;

/**
 * Created by root on 12/26/16.
 */

public class ESearchView implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final int STATE_IDLE = 0;
    private static final int STATE_SEARCHING = 1;
    private static final int STATE_SHOWING_EDITER = 2;
    private static final int LOCATION_RIGHT = 0;
    private static final int LOCATION_LEFT = 1;

    private Activity mActivity;
    private WindowManager mWm;
    private WindowManager.LayoutParams mLayoutParams;

    private EditText mLeftEditText;
    private EditText mRightEditText;
    private ImageView mSearchButton;
    private View mSearchView;

    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private int mState = STATE_IDLE;

    public ESearchView(Activity activity) throws InflaterNotReadyException {
        mActivity = activity;
        mWm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        initView();
        initLayoutParams();
    }

    private void initView() throws InflaterNotReadyException {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        if (inflater == null) {
            throw new InflaterNotReadyException();
        }
        mSearchView = inflater.inflate(R.layout.layout_search_view, null);
        mSearchButton = (ImageView) mSearchView.findViewById(R.id.search_view_button);
        mLeftEditText = (EditText) mSearchView.findViewById(R.id.search_view_input_left);
        mRightEditText = (EditText) mSearchView.findViewById(R.id.search_view_input_right);
        mSearchButton.setOnClickListener(this);
        mSearchButton.setOnLongClickListener(this);
        mSearchView.setOnTouchListener(this);
    }

    /**
     * Call when edit text view is showing.
     */
    private void initBackgroundPanel() throws InflaterNotReadyException {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        if (inflater == null) {
            throw new InflaterNotReadyException();
        }
        View backgroundPanel = inflater.inflate(R.layout.layout_search_background_panel, null);
        backgroundPanel.setOnClickListener(this);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams
    }

    private void initLayoutParams() {
        mLayoutParams.flags = mLayoutParams.flags
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mLayoutParams.dimAmount = 0.2f;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.alpha = 1.0f;
        mLayoutParams.x = mOffsetX;
        mLayoutParams.y = mOffsetY + getStatusBarHeight(mActivity);
    }

    private int locationInScreen() {
        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        if (mOffsetX > (width >> 1)) {
            return LOCATION_LEFT;
        }
        return LOCATION_RIGHT;
    }

    private void showSearchEdit(int location) {
        if (location == LOCATION_LEFT) {
            mLeftEditText.setVisibility(View.GONE);
            mRightEditText.setVisibility(View.VISIBLE);
        } else if (location == LOCATION_RIGHT) {
            mLeftEditText.setVisibility(View.VISIBLE);
            mRightEditText.setVisibility(View.GONE);
        } else {
            mLeftEditText.setVisibility(View.GONE);
            mRightEditText.setVisibility(View.GONE);
            return;
        }
        objectAnimation(location);
    }

    private void hideSearchEdit(int location) {

    }

    private void objectAnimation(int location) {
        View view = location == LOCATION_LEFT ? mLeftEditText : mRightEditText;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,);
    }

    private void doSearch() {

    }

    public void showSearchWindow() {
        mWm.addView(mSearchView,mLayoutParams);
    }

    public void hideSearchWindow() {
        mWm.removeView(mSearchView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_view_button:
                if (mState == STATE_IDLE) {
                    mState = STATE_SHOWING_EDITER;
                    showSearchEdit(locationInScreen());
                } else if (mState == STATE_SHOWING_EDITER){
                    mState = STATE_SEARCHING;
                    doSearch();
                }
                break;
            case R.id.search_background_panel:
                if (mState == STATE_SHOWING_EDITER || mState == STATE_SEARCHING) {
                    view.setVisibility(View.GONE);
                    mWm.removeView(view);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private class InflaterNotReadyException extends Exception {
    }

    public static int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (int) context.getResources().getDimension(resourceId);
    }
}
