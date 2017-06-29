package bravest.ptt.efastquery.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.listeners.OnFloatPanelVisibleListener;
import bravest.ptt.efastquery.service.EFastQueryMonitorService;
import bravest.ptt.efastquery.utils.Utils;
import okhttp3.internal.Util;

public class ESearchOnFloatButton implements View.OnLongClickListener, View.OnTouchListener, OnFloatPanelVisibleListener {

    public static final int STATE_COLLAPSED = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_EXPANDED = 2;

    public static final int WHAT_CHECK_UNTOUCHED_TIME = 0;
    public static final int WHAT_EXPAND_REVERSE_END = 1;
    public static final int WHAT_NORMAL = 2;

    public static final int MODE_MENU_EXPAND = 1;
    public static final int MODE_MENU_COLLAPSE = 2;

    public static final long LONG_CLICK_TIME = 1200;
    public static final long CHECK_NORMAL_UNTOUCHED_TIME = 2000;
    public static final long COLLAPSED_TIME = 4000;

    private static final String TAG = "ESearchOnFloatButton";

    /**
     * The center button radius
     */
    private static int R1;

    /**
     * The menu button diameter
     */
    private static int D2;

    /**
     *  The distance of center button and menu button
     */
    private static int L1;

    /**
     * The distance of menu button and screen edge
     */
    private static int DISTANCE_EDGE;

    /**
     * The min expanded length
     */
    private static int MIN_EXPANDED_LENGTH;

    /**
     *  The length of menu should translate in animation
     */
    private static int TRANSLATE_LENGTH;

    /**
     *  the window size when floating button (normal) is in corner
     */
    private static int CORNER_WINDOW_SIZE;

    /**
     * the window size when floatin button is in the middle of screen.
     */
    private static int MIDDLE_WINDOW_SIZE;

    private static final float DISTANCE = 15.0f;

    private Context mContext;
    private WindowManager mWm;
    private LayoutInflater mInflater;
    private WindowManager.LayoutParams mLayoutParams;
    private View mSearchView;
    private ESearchMainPanel mMainPanel;
    private OnFloatPanelVisibleListener mMainPanelVisibleListener;
    private boolean mIsShowing = false;

    private Point mFloatingButtonNormalPoint = new Point() ;

    private float mDownX, mDownY;
    private long mDownTimeMillis;

    private int mState = STATE_NORMAL;
    private long mNormalActiveTime = System.currentTimeMillis();
    private boolean mInRight = true;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_CHECK_UNTOUCHED_TIME:
                    Log.d(TAG, "handleMessage: ");
                    if (System.currentTimeMillis() - mNormalActiveTime > COLLAPSED_TIME
                            && mState == STATE_NORMAL) {
                        collapseFloatButton();
                    } else {
                        mHandler.sendEmptyMessageDelayed(
                                WHAT_CHECK_UNTOUCHED_TIME, CHECK_NORMAL_UNTOUCHED_TIME);
                    }
                    break;
                case WHAT_EXPAND_REVERSE_END:
                case WHAT_NORMAL:
                    Log.d(TAG, "handleMessage: WHAT_EXPAND_REVERSE_END WHAT_NORMAL");
                    normalFloatButton();
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
        initVariables();
        initView();
        initLayoutParams();
        initMainPanel();
        initCheckUnTouchedHandler();
    }

    private void initVariables() {
        R1 = mContext.getResources().getDimensionPixelSize(R.dimen.search_expanded_level1_size) / 2;

        D2 = mContext.getResources().getDimensionPixelSize(R.dimen.search_expanded_level2_size);

        L1 = mContext.getResources().getDimensionPixelSize(R.dimen.search_expanded_length_inner);

        DISTANCE_EDGE = mContext.getResources().getDimensionPixelSize(R.dimen.search_expanded_distance_outer);

        MIN_EXPANDED_LENGTH = R1 + D2 + L1 + DISTANCE_EDGE;

        TRANSLATE_LENGTH = R1 + D2 / 2 + L1;

        CORNER_WINDOW_SIZE = R1 * 2 + L1 + D2 + DISTANCE_EDGE * 2;

        MIDDLE_WINDOW_SIZE = (R1 + L1 + D2 + DISTANCE_EDGE) * 2;
    }

    private void initCheckUnTouchedHandler() {
        updateActiveTime();
        mHandler.sendEmptyMessageDelayed(WHAT_CHECK_UNTOUCHED_TIME, 1000);
    }

    private void collapseFloatButton() {
        mState = STATE_COLLAPSED;
        Log.d(TAG, "collapseFloatButton: ");
        //get normal button position.
        final int[] locations = new int[2];
        final int normalSize = R1 * 2;
        mSearchView.getLocationOnScreen(locations);
        mWm.removeView(mSearchView);

        if (mInRight) {
            mSearchView = mInflater.inflate(R.layout.layout_floating_button_collapsed_right, null);
        } else {
            mSearchView = mInflater.inflate(R.layout.layout_floating_button_collapsed_left, null);
        }
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandFloatButton();
            }
        });
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWm.addView(mSearchView, mLayoutParams);

        float x;
        if (mInRight) {
            x = locations[0] + (normalSize / 2);
        } else {
            x = locations[0] - (normalSize / 2);
        }
        Log.d(TAG, "collapseFloatButton:  x = " + x);
        ValueAnimator animator = alignAnimator(x, locations[1] + Utils.getStatusBarHeight(mContext));
        animator.start();
    }

    private void normalFloatButton() {
        mHandler.sendEmptyMessageDelayed(WHAT_CHECK_UNTOUCHED_TIME, 1000);
        mState = STATE_NORMAL;
        if (mSearchView != null) {
            mWm.removeView(mSearchView);
        }
        mSearchView = mInflater.inflate(R.layout.layout_floating_button_normal, null);
        mSearchView.setOnTouchListener(this);
        mSearchView.setOnLongClickListener(this);

        mLayoutParams.x = mFloatingButtonNormalPoint.x;
        mLayoutParams.y = mFloatingButtonNormalPoint.y - Utils.getStatusBarHeight(mContext);
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        mWm.addView(mSearchView, mLayoutParams);
    }

    /**
     *  六种展开形态
     *  定义一级菜单半径为R1, 一个二级菜单的直径为D2, 与一级圆球距离为L1. DISTANCE为二级菜单与屏幕边界间距。
     *  二级菜单分别定义为：M1, M2, M3
     *
     *  （0,0）
     *  | 一 一 一 一 一 → X坐标 0°
     *  |
     *  |
     *  |
     *  ↓Y坐标, 90°
     *
     *  左上角形态：条件： x = 0, y <  R1 + D2 + L1 + DISTANCE  弹射角度：M1 → 0° M2 → 45°M3 → 90°
     *  | 一 一 → M1
     *  |  ↘
     *  ↓    ↘ M2
     *  M3
     *
     *  右上角形态：条件： x = width, y < R1 + D2 + L1 + DISTANCE  弹射角度：M1 → 180° M2 → 135°M3 → 90°
     *   M1 ← 一 一|
     *           ↙ |
     *     M2 ↙    ↓
     *              M3
     *
     *
     *  右中角形态：条件： x = width, R1 + D2 + L1 + DISTANCE <= y <=  height - (R1 +D2 + L1 + DISTANCE)
     *  弹射角度：M1 → 270° M2 → 180°M3 → 90°
     *
     *          M1
     *          ↑
     *  M2 ← 一|
     *          ↓
     *          M3
     *
     *  右下角形态：条件：x = width, y > height - (R1 +D2 + L1 + DISTANCE)  弹射角度：M1 → 270° M2 → 225°M3 → 180°
     *
     *              M1
     *    M2 ↖    ↑
     *          ↖ |
     *  M3 ← 一 一|
     *
     *  左下角形态：条件：x = 0, y > height - (R1 +D2 + L1 + DISTANCE)  弹射角度：M1 → 270° M2 → 315°M3 → 0°
     *
     *   M1
     *  ↑    ↗ M2
     *  |  ↗
     *  | 一 一 → M3
     *
     *  左中角形态：条件： x = 0, R1 + D2 + L1 + DISTANCE <= y <=  height - (R1 +D2 + L1 + DISTANCE)
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

        //[1]  init some variables
        final int[] locations = new int[2];
        mSearchView.getLocationOnScreen(locations);
        mFloatingButtonNormalPoint.x = locations[0];
        mFloatingButtonNormalPoint.y = locations[1];

        final int screenHeight = Utils.getScreenHeight(mContext);
        final int centerY = locations[1] + mSearchView.getMeasuredHeight() / 2;

        Log.d(TAG, "expandFloatButton: screenHeight = " + screenHeight + ", centerY = " + centerY);

        //[2] remove old view and add new expanded view
        //init some views
        mWm.removeView(mSearchView);
        mSearchView = mInflater.inflate(R.layout.layout_floating_button_expanded, null);

        ImageView center = (ImageView) mSearchView.findViewById(R.id.center);
        final ImageView search = (ImageView) mSearchView.findViewById(R.id.search);
        ImageView select = (ImageView) mSearchView.findViewById(R.id.select);
        ImageView exit = (ImageView) mSearchView.findViewById(R.id.exit);

        final  MenuAnimator searchAnimator = new MenuAnimator(search, MODE_MENU_EXPAND);
        final MenuAnimator selectAnimator = new MenuAnimator(select, MODE_MENU_EXPAND);
        final MenuAnimator exitAnimator = new MenuAnimator(exit, MODE_MENU_EXPAND);

        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAnimator.reverse();
                selectAnimator.reverse();
                exitAnimator.reverse();
                updateActiveTime();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateActiveTime();
            }
        });
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateActiveTime();
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateActiveTime();
            }
        });
        mWm.addView(mSearchView, mLayoutParams);

        //[3] animation of the expanded button
        //four animation : alpha , translationX, Y, rotation, scaleX , Y


        boolean inCorner = false;
        int relativeRule1;
        int relativeRule2;
        relativeRule1 = relativeRule2 = RelativeLayout.ALIGN_PARENT_TOP;

        if (mInRight) {
            relativeRule1 = RelativeLayout.ALIGN_PARENT_RIGHT;
            if (centerY < MIN_EXPANDED_LENGTH) {
                searchAnimator.setTranslation(-TRANSLATE_LENGTH, 0);
                selectAnimator.setTranslation(
                        (float) (Math.sin(45) * (-TRANSLATE_LENGTH)),
                        (float) (Math.sin(45) * (TRANSLATE_LENGTH)));
                exitAnimator.setTranslation(0, TRANSLATE_LENGTH);

                inCorner = true;
                relativeRule2 = RelativeLayout.ALIGN_PARENT_TOP;

            } else if (centerY >= MIN_EXPANDED_LENGTH
                    && centerY <= screenHeight - MIN_EXPANDED_LENGTH){
                searchAnimator.setTranslation(0, -TRANSLATE_LENGTH);
                selectAnimator.setTranslation(-TRANSLATE_LENGTH, 0);
                exitAnimator.setTranslation(0, TRANSLATE_LENGTH);

                inCorner = false;
                relativeRule2 = RelativeLayout.CENTER_VERTICAL;

                mLayoutParams.y -= MIDDLE_WINDOW_SIZE / 2 - R1;

            } else if (centerY > screenHeight - MIN_EXPANDED_LENGTH) {
                searchAnimator.setTranslation(0, -TRANSLATE_LENGTH);
                selectAnimator.setTranslation(
                        (float) (Math.sin(45) * (-TRANSLATE_LENGTH)),
                        (float) (Math.sin(45) * (-TRANSLATE_LENGTH)));
                exitAnimator.setTranslation(-TRANSLATE_LENGTH, 0);

                inCorner = true;
                relativeRule2 = RelativeLayout.ALIGN_PARENT_BOTTOM;

                mLayoutParams.y -= MIDDLE_WINDOW_SIZE / 2 - R1;
            }
        } else {
            relativeRule1 = RelativeLayout.ALIGN_PARENT_LEFT;
            if (centerY < MIN_EXPANDED_LENGTH) {
                searchAnimator.setTranslation(TRANSLATE_LENGTH, 0);
                selectAnimator.setTranslation(
                        (float) (Math.sin(45) * (TRANSLATE_LENGTH)),
                        (float) (Math.sin(45) * (TRANSLATE_LENGTH)));
                exitAnimator.setTranslation(0, TRANSLATE_LENGTH);

                inCorner = true;
                relativeRule2 = RelativeLayout.ALIGN_PARENT_TOP;

            } else if (centerY >= MIN_EXPANDED_LENGTH
                    && centerY <= screenHeight - MIN_EXPANDED_LENGTH){
                searchAnimator.setTranslation(0, -TRANSLATE_LENGTH);
                selectAnimator.setTranslation(TRANSLATE_LENGTH, 0);
                exitAnimator.setTranslation(0, TRANSLATE_LENGTH);

                inCorner = false;
                relativeRule2 = RelativeLayout.CENTER_VERTICAL;
                mLayoutParams.y -= MIDDLE_WINDOW_SIZE / 2 - R1;

            } else if (centerY > screenHeight - MIN_EXPANDED_LENGTH) {
                searchAnimator.setTranslation(0, -TRANSLATE_LENGTH);
                selectAnimator.setTranslation(
                        (float) (Math.sin(45) * (TRANSLATE_LENGTH)),
                        (float) (Math.sin(45) * (-TRANSLATE_LENGTH)));
                exitAnimator.setTranslation(TRANSLATE_LENGTH, 0);

                inCorner = true;
                relativeRule2 = RelativeLayout.ALIGN_PARENT_BOTTOM;
                mLayoutParams.y -= MIDDLE_WINDOW_SIZE / 2 - R1;
            }
        }

        relayoutChild(center, relativeRule1, relativeRule2);
        relayoutChild(search, relativeRule1, relativeRule2);
        relayoutChild(select, relativeRule1, relativeRule2);
        relayoutChild(exit, relativeRule1, relativeRule2);

        resizeSearchView(inCorner? CORNER_WINDOW_SIZE : MIDDLE_WINDOW_SIZE);

        searchAnimator.start();
        selectAnimator.start();
        exitAnimator.start();
    }

    private void resizeSearchView(int size) {
        ViewGroup.LayoutParams pa = mSearchView.getLayoutParams();
        pa.width = pa.height = size;
        mSearchView.setLayoutParams(pa);
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    private void relayoutChild(View view, int rule1, int rule2) {
        if (mSearchView instanceof ViewGroup) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.addRule(rule1);
            params.addRule(rule2);
            if (mInRight) {
                if (view.getId() != R.id.center) {
                    params.rightMargin = R1 - D2 / 2;
                }
            } else {
                if (view.getId() != R.id.center) {
                    params.leftMargin = R1 - D2 / 2;
                }
            }
            ((ViewGroup) mSearchView).updateViewLayout(view,params);
        }
    }

    private void initView() throws InflaterNotReadyException {
        mInflater = LayoutInflater.from(mContext);
        if (mInflater == null) {
            throw new InflaterNotReadyException();
        }
        mSearchView = mInflater.inflate(R.layout.layout_floating_button_normal, null);
        mSearchView.setOnTouchListener(this);
        mSearchView.setOnLongClickListener(this);
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
        mLayoutParams.y = Utils.getScreenHeight(mContext) / 4;
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
            mHandler.removeMessages(WHAT_CHECK_UNTOUCHED_TIME);
            if (mMainPanelVisibleListener != null) {
                mMainPanelVisibleListener.onHide();
            }
        }
    }

    public void forceCloseFloatButton() {
        if (mIsShowing) {
            mWm.removeView(mSearchView);
            mHandler.removeMessages(WHAT_CHECK_UNTOUCHED_TIME);
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

    private void updateActiveTime() {
        if (mState != STATE_COLLAPSED) {
            mNormalActiveTime = System.currentTimeMillis();
        }
    }

    private void down(MotionEvent event) {
        mDownX = event.getRawX();
        mDownY = event.getRawY();
        mLayoutParams.alpha = 1.0f;
        mDownTimeMillis = System.currentTimeMillis();
        mWm.updateViewLayout(mSearchView, mLayoutParams);
        updateActiveTime();
    }

    private void gotoAccessibilitySettings() {
        if (!Utils.isAccessibilitySettingsOn(mContext, EFastQueryMonitorService.class)) {
            Utils.powerAccessibilityPermission(mContext);
        }
    }

    private void up(MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        updateActiveTime();
        if (x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE) {
            if (System.currentTimeMillis() - mDownTimeMillis > LONG_CLICK_TIME) {
                //long click
                if (mState != STATE_COLLAPSED) {
                    gotoAccessibilitySettings();
                }
            } else {
                //click
                Log.d(TAG, "up: click");
                if (mMainPanel.isShowing()) {
                    mMainPanel.hideSearchPanel();
                } else {
                    switch (mState) {
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
        updateActiveTime();

        if (!(x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE)) {
            updateFloatLocation((int)x, (int)y);
            if (mState == STATE_COLLAPSED) {
                mHandler.sendEmptyMessage(WHAT_NORMAL);
            }
        } else if (System.currentTimeMillis() - mDownTimeMillis > LONG_CLICK_TIME){
            //long click

            if (mState != STATE_COLLAPSED) {
                gotoAccessibilitySettings();
            }
        }
    }

    private void updateFloatLocation(int x, int y) {
        Log.d(TAG, "updateFloatLocation:  = " +mSearchView.getMeasuredWidth());
        int width, height;
        if(mState == STATE_COLLAPSED) {
            width = mContext.getResources().getDimensionPixelSize(R.dimen.search_collapsed);
            height = R1 * 2;
        } else if (mState == STATE_NORMAL) {
            width = R1 * 2;
            height = R1 * 2;
        } else {
            width = mSearchView.getMeasuredWidth();
            height = mSearchView.getMeasuredHeight();
        }
        mLayoutParams.x = x - width / 2;
        mLayoutParams.y = y - Utils.getStatusBarHeight(mContext) - (height / 2);
        mWm.updateViewLayout(mSearchView, mLayoutParams);
    }

    @Override
    public boolean onLongClick(View view) {
        if (mState == STATE_NORMAL) {
            gotoAccessibilitySettings();
        }
        return false;
    }

    public static class InflaterNotReadyException extends Exception {
    }

    public void setViewVisibleListener(OnFloatPanelVisibleListener listener) {
        mMainPanelVisibleListener = listener;
    }

    private class MenuAnimator {

        private static final float END_ALPHA = 0.4f;

        private int mode;
        private View targetView;

        ObjectAnimator alphaExpandedAnimator;
        ObjectAnimator transXAnimator;
        ObjectAnimator transYAnimator;
        ObjectAnimator rotationAnimator;
        ObjectAnimator scaleXAnimator;
        ObjectAnimator scaleYAnimator;

        private AnimatorSet set = new AnimatorSet();

        public MenuAnimator(View view, int mode) {
            this.targetView = view;
            this.mode = mode;
            initAnimator();
        }

        private void initAnimator() {
            if (mode == MODE_MENU_EXPAND) {
                alphaExpandedAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 0.4f, 1f);
                rotationAnimator = ObjectAnimator.ofFloat(targetView, "rotation", 0f, 1080f);
                rotationAnimator.setInterpolator(new OvershootInterpolator());
                scaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 0f, 1f);
                scaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 0f, 1f);

            } else if (mode == MODE_MENU_COLLAPSE) {
                alphaExpandedAnimator = ObjectAnimator.ofFloat(targetView, "alpha", 1f, 0f);
                rotationAnimator = ObjectAnimator.ofFloat(targetView, "rotation", 1080f, 0f);
                rotationAnimator.setInterpolator(new OvershootInterpolator());
                scaleXAnimator = ObjectAnimator.ofFloat(targetView, "scaleX", 1f, 0f);
                scaleYAnimator = ObjectAnimator.ofFloat(targetView, "scaleY", 1f, 0f);
            }
        }

        public void setTranslation(float xLength, float yLength) {
            transXAnimator = ObjectAnimator.ofFloat(targetView, "translationX", xLength);
            transXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            transYAnimator = ObjectAnimator.ofFloat(targetView, "translationY", yLength);
            transYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        }

        public void start() {
            if (transXAnimator == null || transYAnimator == null) {
                throw new IllegalStateException("Method setTranslation should be called before");
            }
            set.playTogether(
                    alphaExpandedAnimator,
                    transXAnimator,
                    transYAnimator,
                    rotationAnimator,
                    scaleXAnimator,
                    scaleYAnimator);
            alphaExpandedAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Log.d(TAG, "onAnimationUpdate: " + valueAnimator.getAnimatedValue() + " , height = " + mSearchView.getMeasuredHeight());
                    if (isReversing) {
                        float endValue = (float)valueAnimator.getAnimatedValue();
                        if (endValue == END_ALPHA) {
                            mHandler.sendEmptyMessage(WHAT_EXPAND_REVERSE_END);
                        }
                    }
                }
            });
            set.setDuration(400);
            set.setInterpolator(new OvershootInterpolator());
            set.start();
        }

        public void reverse() {
            isReversing = true;
            alphaExpandedAnimator.reverse();
            transXAnimator.reverse();
            transYAnimator.reverse();
            rotationAnimator.reverse();
            scaleXAnimator.reverse();
            scaleYAnimator.reverse();
        }

        private boolean isReversing = false;
    }
}
