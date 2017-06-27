package bravest.ptt.efastquery.ui;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.listeners.OnFloatPanelVisibleListener;
import bravest.ptt.efastquery.utils.Utils;

public class ESearchOnFloatButton implements View.OnLongClickListener, View.OnTouchListener, OnFloatPanelVisibleListener {

    public static final int STATE_COLLAPSED = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_EXPANDED = 2;

    public static final int WHAT_CHECK_UNTOUCHED_TIME = 0;

    private static final String TAG = "ESearchOnFloatButton";
    private static final float DISTANCE = 15.0f;

    private Context mContext;
    private WindowManager mWm;
    private LayoutInflater mInflater;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSearchView;
    private ESearchMainPanel mMainPanel;
    private OnFloatPanelVisibleListener mMainPanelVisibleListener;
    private boolean mIsShowing = false;

    private float mDownX, mDownY;
    private long mDownTimeMillis;

    private int mState = STATE_NORMAL;
    private long mUpTimeMillis = System.currentTimeMillis();
    private boolean mInRight = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CHECK_UNTOUCHED_TIME:
                    Log.d(TAG, "handleMessage: ");
                    if (System.currentTimeMillis() - mUpTimeMillis > 4000
                            && mState == STATE_NORMAL) {
                        collapseFloatButton();
                    }
                    mHandler.sendEmptyMessageDelayed(WHAT_CHECK_UNTOUCHED_TIME, 2000);
                    break;
                default:
                    break;
            }
        }
    };

    public ESearchOnFloatButton(Context context) throws InflaterNotReadyException {
        mContext = context;
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams = new WindowManager.LayoutParams();
        initView();
        initLayoutParams();
        initMainPanel();
        initCheckUnTouchedHandler();
    }

    private void initCheckUnTouchedHandler() {
        mUpTimeMillis = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(WHAT_CHECK_UNTOUCHED_TIME, 1000);
    }

    private void collapseFloatButton() {
        mState = STATE_COLLAPSED;
        Log.d(TAG, "collapseFloatButton: ");
        //get normal button position.
        final int[] locations = new int[2];
        final int normalSize = mSearchView.getMeasuredWidth();
        mSearchView.getLocationOnScreen(locations);
        mWm.removeView(mSearchView);

        if (mInRight) {
            mSearchView = mInflater.inflate(R.layout.layout_floating_button_collapsed_right, null);
        } else {
            mSearchView = mInflater.inflate(R.layout.layout_floating_button_collapsed_left, null);
        }
        mSearchView.setOnTouchListener(this);
        mWm.addView(mSearchView, mLayoutParams);

        float x;
        if (mInRight) {
            x = locations[0] + (normalSize / 2);
        } else {
            x = locations[0] - (normalSize / 2);
        }
        ValueAnimator animator = alignAnimator(x, locations[1]);
        animator.start();
    }

    private void normalFloatButton() {
        mState = STATE_NORMAL;
    }

    /**
     *  六种展开形态
     *  定义一级菜单半径为R1, 一个二级菜单的直径为R2, 与一级圆球距离为L1. DISTANCE为二级菜单与屏幕边界间距。
     *  二级菜单分别定义为：M1, M2, M3
     *
     *  （0,0）
     *  | 一 一 一 一 一 → X坐标 0°
     *  |
     *  |
     *  |
     *  ↓Y坐标, 90°
     *
     *  左上角形态：条件： x = 0, y <  R1 + R2 + L1 + DISTANCE  弹射角度：M1 → 0° M2 → 45°M3 → 90°
     *  | 一 一 → M1
     *  |  ↘
     *  ↓    ↘ M2
     *  M3
     *
     *  右上角形态：条件： x = width, y < R1 + R2 + L1 + DISTANCE  弹射角度：M1 → 180° M2 → 135°M3 → 90°
     *   M1 ← 一 一|
     *           ↙ |
     *     M2 ↙    ↓
     *              M3
     *
     *
     *  右中角形态：条件： x = width, R1 + R2 + L1 + DISTANCE <= y <=  height - (R1 +R2 + L1 + DISTANCE)
     *  弹射角度：M1 → 270° M2 → 180°M3 → 90°
     *
     *          M1
     *          ↑
     *  M2 ← 一|
     *          ↓
     *          M3
     *
     *  右下角形态：条件：x = width, y > height - (R1 +R2 + L1 + DISTANCE)  弹射角度：M1 → 270° M2 → 225°M3 → 180°
     *
     *              M1
     *    M2 ↖    ↑
     *          ↖ |
     *  M3 ← 一 一|
     *
     *  左下角形态：条件：x = 0, y > height - (R1 +R2 + L1 + DISTANCE)  弹射角度：M1 → 270° M2 → 315°M3 → 0°
     *
     *   M1
     *  ↑    ↗ M2
     *  |  ↗
     *  | 一 一 → M3
     *
     *  左中角形态：条件： x = 0, R1 + R2 + L1 + DISTANCE <= y <=  height - (R1 +R2 + L1 + DISTANCE)
     *  弹射角度：M1 → 270° M2 → 0°M3 → 90°
     *
     *  M1
     *  ↑
     *  | 一 → M2
     *  ↓
     *  M3
     */
    private void expandFloatButton() {
        mState = STATE_EXPANDED;

    }

    private void initView() throws InflaterNotReadyException {
        mInflater = LayoutInflater.from(mContext);
        if (mInflater == null) {
            throw new InflaterNotReadyException();
        }
        mSearchView = mInflater.inflate(R.layout.layout_floating_button, null);
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

    private void initMainPanel() {
        try {
            mMainPanel = new ESearchMainPanel(mContext, mWm, this);
            mMainPanel.setViewVisibleListener(this);
        } catch (InflaterNotReadyException e) {
            e.printStackTrace();
        }
    }

    private ValueAnimator alignAnimator(float x, float y) {
        ValueAnimator animator;
        if (x <= Utils.getScreenWidth(mContext) / 2) {
            mInRight = false;
            animator = ValueAnimator.ofObject(new PointEvaluator()
                    , new Point((int)x, (int)y), new Point(0, (int)y));
        }else {
            mInRight = true;
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

    /**
     * This is Main panel's show action instead of float button.
     */
    @Override
    public void onShow() {
        //Main panel show, float button hide
        hideFloatButton();
    }

    /**
     * This is Main panel's hide action instead of float button.
     */
    @Override
    public void onHide() {
        //Main panel hide, float button show.
        showFloatButton();
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

    public void showFloatButton() {
        if (!mIsShowing) {
            mIsShowing = true;
            if (mWm == null) {
                return;
            }
            mWm.addView(mSearchView,mLayoutParams);
            if (mMainPanelVisibleListener != null) {
                mMainPanelVisibleListener.onShow();
            }
        }
    }

    public void hideFloatButton() {
        if (mIsShowing) {
            mIsShowing = false;
            if (mWm == null) {
                return;
            }
            mWm.removeView(mSearchView);
            if (mMainPanelVisibleListener != null) {
                mMainPanelVisibleListener.onHide();
            }
        }
    }

    public void forceCloseFloatButton() {
        if (mIsShowing) {
            mWm.removeView(mSearchView);
            mMainPanel.destroy();

            mIsShowing = false;
            mWm = null;
            mMainPanel = null;
            mMainPanelVisibleListener = null;
        }
    }

    public boolean isFloatingButtonShowing() {
        return mIsShowing;
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
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        mLayoutParams.alpha = 1.0f;
        mDownTimeMillis = System.currentTimeMillis();
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    private void up(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        mUpTimeMillis = System.currentTimeMillis();
        if (x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE) {
            if (System.currentTimeMillis() - mDownTimeMillis > 1200) {
                //long click
            } else {
                //click
                Log.d(TAG, "up: click");
                if (mMainPanel.isShowing()) {
                    mMainPanel.hideSearchPanel();
                } else {
                    switch (mState) {
                        case STATE_COLLAPSED:
                            expandFloatButton();
                            break;
                        case STATE_NORMAL:
                            expandFloatButton();
                            break;
                        default:
                            break;
                    }
                }
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

    public void setViewVisibleListener(OnFloatPanelVisibleListener listener) {
        mMainPanelVisibleListener = listener;
    }
}
