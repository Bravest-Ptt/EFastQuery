package bravest.ptt.androidlib.activity.swipeback;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import bravest.ptt.androidlib.R;

/**
 * Created by root on 6/16/17.
 */

public class SwipeBackLayout extends FrameLayout {

    private static final String TAG = "SwipeBackLayout";

    /**
     * 默认滑动销毁距离界限,0.5默认滑到屏幕的一半
     */
    private static final float DEFAULT_SCROLL_THRESHOLD = 0.5f;

    /**
     * 默认滑动销毁速度界限
     */
    private static final float DEFAULT_VELOCITY_THRESHOLD = 500f;

    /**
     * 拖动时透明区域的颜色
     */
    private static final int DEFAULT_SCRIM_COLOR = 0x99000000;

    private static final int FULL_ALPHA = 255;

    private float mScrollThreshold = DEFAULT_SCROLL_THRESHOLD;

    private float mVelocityThreshold = DEFAULT_VELOCITY_THRESHOLD;

    private int mScrimColor = DEFAULT_SCRIM_COLOR;

    private ViewDragHelper mViewDragHelper;

    /**
     * 阴影
     */
    private Drawable mshadowLeft;

    //是否处于onlayout状态
    private boolean mInLayout = false;

    /**
     * 拖动的界面
     */
    private View mContentView;

    /**
     * 记录左边移动的像素值
     */
    private int mContentLeft;

    /**
     * 当前滑动的宽度占屏幕宽度的比例 [0,1)
     */
    private float mScrollPercent;

    private float mScrimOpacity;

    private boolean mEnable = true;

    private boolean mPreLayoutScrollable = true;

    private boolean mHasShadow = true;

    private Rect mTmpRect = new Rect();

    private FragmentActivity mActivity;

    private Fragment mFragment;

    private List<SwipeListener> mListeners;

    public SwipeBackLayout(@NonNull Context context) {
        super(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragCallback());
        mViewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    /**
     * 设置拖动时是否有阴影
     *
     * @param hasShadow
     */
    public void setHasShadow(boolean hasShadow) {
        this.mHasShadow = hasShadow;
        if (hasShadow) {
            mshadowLeft = ContextCompat.getDrawable(getContext(), R.drawable.swipeback_shadow_left);
        }
    }

    /**
     * 设置拖动时背景是否联动
     *
     * @param scrollable
     */
    public void setPreLayoutScrollable(boolean scrollable) {
        this.mPreLayoutScrollable = scrollable;
    }

    /**
     * 设置滚动距离占宽度的百分比触发的临界阈值
     *
     * @param threshold
     */
    public void setScrollThreshold(float threshold) {
        this.mScrollThreshold = threshold;
    }

    /**
     * 设置滚动速度触发的临界阈值
     *
     * @param threshold
     */
    public void setVelocityThresHold(float threshold) {
        this.mVelocityThreshold = threshold;
    }

    /**
     * 设置是否支持滑动返回
     *
     * @param enable
     */
    public void setEnableGesture(boolean enable) {
        mEnable = enable;
    }

    /**
     * 设置拖动时移动的内容区
     *
     * @param contentView
     */
    private void setContentView(View contentView) {
        mContentView = contentView;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mInLayout = true;
        if (mContentView != null) {
            mContentView.layout(
                    mContentLeft,
                    top,
                    mContentLeft + mContentView.getMeasuredWidth(),
                    mContentView.getMeasuredHeight());
        }
        mInLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!mInLayout) {
            super.requestLayout();
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final boolean drawContent = child == mContentView;

        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (mScrimOpacity > 0 && drawContent
                && mViewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {
            if (mHasShadow) {
                //do draw shadow
                drawShadow(canvas, child);
            }
            //do draw scrim
            drawScrim(canvas, child);
        }
        return ret;
    }

    /**
     * 绘制阴影
     *
     * @param canvas
     * @param child
     */
    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mTmpRect;
        child.getHitRect(childRect);
        mshadowLeft.setBounds(
                childRect.left - mshadowLeft.getIntrinsicWidth(),
                childRect.top,
                childRect.left,
                childRect.bottom);
        mshadowLeft.setAlpha((int) (mScrimOpacity * FULL_ALPHA));
        mshadowLeft.draw(canvas);
    }

    /**
     * 绘制透明区域,拖动的越远越透明
     *
     * @param canvas
     * @param child
     */
    private void drawScrim(Canvas canvas, View child) {
        final int baseAlpha = (mScrimColor & 0xFF000000) >>> 24;
        final int alpha = (int) (baseAlpha * mScrimOpacity);
        final int color = alpha << 24 | (mScrimColor & 0xFFFFFF);
        canvas.clipRect(0, 0, child.getLeft(), getHeight());
        canvas.drawColor(color);
    }

    @Override
    public void computeScroll() {
        mScrimOpacity = 1 - mScrollPercent;
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void attachToActivity(FragmentActivity activity) {
        mActivity = activity;
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.windowBackground
        });
        int background = a.getResourceId(0, 0);
        a.recycle();

        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decor.removeView(decorChild);
        addView(decorChild);
        setContentView(decorChild);
        decor.addView(this);
    }

    public void attachToFragment(Fragment fragment, View view) {
        addView(view);
        mFragment = fragment;
        setContentView(view);
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view
     *
     * @param listener the callback for do something
     */
    public void addSwipeListener(SwipeListener listener) {
        if (mListeners == null) {
            mListeners = new ArrayList<>();
        }
        mListeners.add(listener);
    }

    /**
     * Remove a listener from the set of listeners
     *
     * @param listener the listener which you want to remove
     */
    public void removeSwipeListener(SwipeListener listener) {
        if (mListeners == null) {
            return;
        }
        mListeners.remove(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mEnable) {
            return false;
        }
        try {
            return mViewDragHelper.shouldInterceptTouchEvent(ev);
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mEnable) {
            return false;
        }
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 移动背景
     */
    private void moveBackgroundLayout(SwipeBackLayout layout) {
        //mScrollPercent 变化时: 0 -> 0.95 -> 1
        //背景translationX的变化: -0.4 -> 0 -> 0
        if (layout != null) {
            float translationX =
                    (float) (0.4f / 0.95f * (mScrollPercent - 0.95) * layout.getWidth());
            if (translationX > 0) {
                translationX = 0;
            }
            Log.d(TAG, "moveBackgroundLayout: " + translationX);
            layout.setTranslationX(translationX);
        }
    }

    /**
     * 背景回到原位
     */
    private void recovery(SwipeBackLayout layout) {
        if (layout != null) {
            layout.setTranslationX(0);
        }
    }

    private class ViewDragCallback extends ViewDragHelper.Callback {

        private boolean mIsScrollOverValid;

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            boolean dragEnable = mViewDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT, pointerId);
            if (dragEnable) {
                if (mListeners != null) {
                    for (SwipeListener listener : mListeners) {
                        listener.onEdgeTouch(ViewDragHelper.EDGE_LEFT);
                    }
                }
                if (mFragment != null) {
                    //当前view在fragment中,将上一个fragment设置为透明可见
                    ISwipeBackFragment iSwipeBackFragment = ((ISwipeBackFragment) mFragment).getPreFragment();
                    Fragment preFragment = (Fragment) iSwipeBackFragment;
                    if (preFragment != null) {
                        View fragmentView = preFragment.getView();
                        if (fragmentView != null && fragmentView.getVisibility() != VISIBLE) {
                            fragmentView.setVisibility(VISIBLE);
                        }
                    }
                } else if (mActivity != null) {
                    Utils.convertActivityToTranslucent(mActivity);
                }
            }
            return dragEnable;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            if (mFragment != null) {
                return 1;
            } else if (mActivity != null) {
                ISwipeBackActivity swipeBackActivity = (ISwipeBackActivity) mActivity;
                if (swipeBackActivity.swipeBackPriority()) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return 0;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return Math.min(child.getWidth(), Math.max(left, 0));
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (mScrollPercent < mScrollThreshold && !mIsScrollOverValid) {
                mIsScrollOverValid = true;
            }
            if (mListeners != null && !mListeners.isEmpty()
                    && mViewDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING
                    && mScrollPercent >= mScrollThreshold && mIsScrollOverValid) {
                for (SwipeListener listener : mListeners) {
                    listener.onScrollOverThreshold();
                }
            }

            if (changedView == mContentView) {
                //mContentView.getWidth() 实际是屏幕宽度
                int width = mContentView.getWidth();
                if (width != 0) {
                    mScrollPercent = Math.abs((float) left / width);
                }
                mScrimOpacity = 1 - mScrollPercent;
                mContentLeft = left;

                Log.d(TAG, "onViewPositionChanged: " + mContentLeft);
                if (mPreLayoutScrollable) {
                    moveBackgroundLayout(getPreSwipeBackLayout());
                }

                invalidate();
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            final int childWidth = releasedChild.getWidth();
            int left = 0, top = 0;
            Log.d(TAG, "onViewReleased: xvel = " + xvel + ", mScrollPercent = " + mScrollPercent);
            if (xvel > mVelocityThreshold || mScrollPercent > mScrollThreshold) {
                left = childWidth + 1;
            } else {
                left = 0;
            }
            mViewDragHelper.settleCapturedViewAt(left, top);
            invalidate();
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (mListeners != null && !mListeners.isEmpty()) {
                for (SwipeListener listener : mListeners) {
                    listener.onScrollStateChanged(state, mScrollPercent);
                }
            }

            Log.d(TAG, "onViewDragStateChanged: state = " + state + ", percent = " + mScrollPercent);

            if (state == ViewDragHelper.STATE_IDLE) {
                if (mScrollPercent >= 1){
                    if (mFragment != null && !mFragment.isDetached()){
                        //结束当前fragment时,取消动画
                        ISwipeBackFragment iSwipeBackFragment = (ISwipeBackFragment)mFragment;
                        iSwipeBackFragment.getPreFragment().setLockable(true);

                        iSwipeBackFragment.setLockable(true);
                        mFragment.getFragmentManager().popBackStackImmediate();
                        iSwipeBackFragment.setLockable(false);

                        iSwipeBackFragment.getPreFragment().setLockable(false);
                    }else if (mActivity != null && !mActivity.isFinishing()){
                        mActivity.finish();
                    }
                }else{
                    recovery(getPreSwipeBackLayout());
                    if (mActivity != null) Utils.convertActivityFromTranslucent(mActivity);
                }
            }
        }
    }

    private SwipeBackLayout getPreSwipeBackLayout() {
        SwipeBackLayout swipeBackLayout = null;
        if (mFragment != null) {
            ISwipeBackFragment preFragment = ((ISwipeBackFragment) mFragment).getPreFragment();
            swipeBackLayout = preFragment.getSwipeBackLayout();
        } else if (mActivity != null) {
            ISwipeBackActivity preActivity = ((ISwipeBackActivity) mActivity).getPreActivity();
            swipeBackLayout = preActivity.getSwipeBackLayout();
        }
        return swipeBackLayout;
    }
}
