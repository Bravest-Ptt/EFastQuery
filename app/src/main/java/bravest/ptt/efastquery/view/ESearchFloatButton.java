package bravest.ptt.efastquery.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.utils.Utils;

public class ESearchFloatButton implements View.OnLongClickListener, View.OnTouchListener {

    private static final String TAG = "ESearchFloatButton";
    private static final int STATE_IDLE = 0;
    private static final float DISTANCE = 15.0f;
    private static final int WHAT_HIDE_ALPHA = 1;

    private Context mContext;
    private WindowManager mWm;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSearchView;

    private float mDownX, mDownY, mOldDownX, mOldDownY;
    private long mDownTimeMillis;

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
        mSearchView = inflater.inflate(R.layout.layout_floating_button, null);
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
        mLayoutParams.gravity = Gravity.START | Gravity.TOP;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.alpha = 1.0f;
        mLayoutParams.x = Utils.getScreenWidth(mContext);
        mLayoutParams.y = Utils.getScreenHeight(mContext) * 2 / 4;
    }


    private ValueAnimator alignAnimator(float x, float y) {
        ValueAnimator animator;
        if (x <= Utils.getScreenWidth(mContext) / 2) {
            animator = ValueAnimator.ofObject(new PointEvaluator()
                    , new Point((int)x, (int)y), new Point(0, (int)y));
        }else {
            animator = ValueAnimator.ofObject(new PointEvaluator(),
                    new Point((int)x, (int)y), new Point(Utils.getScreenWidth(mContext), (int)y));
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
            return new Point((int)x, (int)y);
        }
    }

    private boolean mIsShowing = false;
    public void showSearchWindow() {
        if (!mIsShowing) {
            mIsShowing = true;
            mWm.addView(mSearchView,mLayoutParams);
        }
    }

    public void hideSearchWindow() {
        if (mIsShowing) {
            mIsShowing = false;
            mWm.removeView(mSearchView);
        }
    }

    private void showMainPanel() {

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                down(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                up(motionEvent);
            case MotionEvent.ACTION_MOVE:
                move(motionEvent);
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
        }
        return false;
    }


    private void down(MotionEvent event) {
        mOldDownX = mDownX;
        mOldDownY = mDownY;
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        mLayoutParams.alpha = 1.0f;
        mDownTimeMillis = System.currentTimeMillis();
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    private void up(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE) {
            if (System.currentTimeMillis() - mDownTimeMillis > 1200) {
                //long click
            } else {
                //click
                Log.d(TAG, "up: click");

            }
        } else {
            ValueAnimator animator = alignAnimator((int) event.getRawX(), (int) event.getRawY());
            animator.start();
        }
    }

    private void move(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        if (!(x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE)) {
            updateFloatLocation((int)x, (int)y);
        }
    }

    private void updateFloatLocation(int x, int y) {
        mLayoutParams.x = x - mSearchView.getMeasuredWidth() / 2;
        mLayoutParams.y = y - Utils.getStatusBarHeight(mContext) - (mSearchView.getMeasuredHeight() / 2);
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    public static class InflaterNotReadyException extends Exception {
    }
}
