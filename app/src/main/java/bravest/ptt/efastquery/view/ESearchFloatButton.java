package bravest.ptt.efastquery.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.utils.Utils;

/**
 * Created by root on 12/26/16.
 */

public class ESearchFloatButton implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final int STATE_IDLE = 0;
    private static final int STATE_SEARCHING = 1;
    private static final int STATE_SHOWING_EDITER = 2;
    private static final int LOCATION_RIGHT = 0;
    private static final int LOCATION_LEFT = 1;

    private Context mContext;
    private WindowManager mWm;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSearchView;

    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private int mState = STATE_IDLE;

    public ESearchFloatButton(Context context) throws InflaterNotReadyException {
        mContext = context;
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        initView();
        initLayoutParams();
    }

    private void initView() throws InflaterNotReadyException {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            throw new InflaterNotReadyException();
        }
        mSearchView = inflater.inflate(R.layout.layout_search_view, null);
        mSearchView.setOnTouchListener(this);
    }

    private void initLayoutParams() {
        mLayoutParams.flags = mLayoutParams.flags
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.dimAmount = 0.2f;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.alpha = 1.0f;
        mLayoutParams.x = mOffsetX;
        mLayoutParams.y = mOffsetY + getStatusBarHeight(mContext);
    }

    private int locationInScreen() {
        int width = Utils.getScreenWidth(mContext);
        if (mOffsetX > (width >> 1)) {
            return LOCATION_LEFT;
        }
        return LOCATION_RIGHT;
    }

    private void showSearchPanel(int location) {
    }

    private void hideSearchEdit(int location) {

    }

    private void toggleSearchButton() {
    }

    private ValueAnimator alignAnimator(float x, float y) {
        ValueAnimator animator = null;
        if (x <= Utils.getScreenWidth(mContext) / 2) {
            animator = ValueAnimator.ofObject(new PointEvaluator(), new Point((int)x, (int)y), new Point(0, (int)y));
        }else {
            animator = ValueAnimator.ofObject(new PointEvaluator(), new Point((int)x, (int)y), new Point(Utils.getScreenWidth(mContext), (int)y));
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Point point = (Point) valueAnimator.getAnimatedValue();
                updateFloatLocation(point.x, point.y);
            }
        });
        animator.setDuration(400);
        return animator;
    }

    private class PointEvaluator implements TypeEvaluator {
        @Override
        public Object evaluate(float fraction, Object from, Object to) {
            Point startPoint = (Point) from;
            Point endPoint = (Point) to;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            Point point = new Point((int)x, (int)y);
            return point;
        }
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
        }
    }

    private static final String TAG = "ESearchFloatButton";
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                up(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                move(motionEvent);
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
        }
        return true;
    }

    private void down(MotionEvent event) {

    }

    private void up(MotionEvent event) {
        ValueAnimator animator = alignAnimator((int)event.getRawX(), (int)event.getRawY());
        animator.start();
    }

    private void move(MotionEvent event) {
        updateFloatLocation((int)event.getRawX(), (int)event.getRawY());
    }

    private void updateFloatLocation(int x, int y) {
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public class InflaterNotReadyException extends Exception {
    }

    public static int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (int) context.getResources().getDimension(resourceId);
    }
}
