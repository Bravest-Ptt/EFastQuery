package bravest.ptt.efastquery.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;

import bravest.ptt.androidlib.activity.BaseActivity;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.ui.ClipView;

public class ClipImageActivity extends BaseActivity implements View.OnTouchListener {

    private ImageView mSrcImage;
    private View mConfirm;
    private ClipView mClipView;

    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();

    /** 动作标志：无 */
    private static final int NONE = 0;
    /** 动作标志：拖动 */
    private static final int DRAG = 1;
    /** 动作标志：缩放 */
    private static final int ZOOM = 2;
    /** 初始化动作标志 */
    private int mMode = NONE;

    /** 记录起始坐标 */
    private PointF mStart = new PointF();
    /** 记录缩放时两指中间点坐标 */
    private PointF mMid = new PointF();

    private float mOldDist = 1f;

    private Bitmap mBitmap;

    private Toolbar mToolbar;

    @Override
    protected void initVariables() {

    }

    @Override
    protected void initViews(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_clip_image);

        //init toolbar
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                onBackPressed();
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity= Gravity.RIGHT;
        mToolbar.addView(mTopSpinner,lp);

        mSrcImage = (ImageView) this.findViewById(R.id.src_image);
        mSrcImage.setOnTouchListener(this);

        ViewTreeObserver observer = mSrcImage.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            public void onGlobalLayout() {
                mSrcImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                initClipView(mSrcImage.getTop());
            }
        });

        mConfirm = (View) this.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleConfirm();
            }
        });
    }

    @Override
    protected void initData() {
    }

    private void handleConfirm() {
//        Bitmap clipBitmap = getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        clipBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] bitmapByte = baos.toByteArray();
//
//        Intent intent = new Intent();
//        intent.setClass(getApplicationContext(), .class);
//        intent.putExtra("bitmap", bitmapByte);
//
//        startActivity(intent);
    }

    /**
     * 初始化截图区域，并将源图按裁剪框比例缩放
     *
     * @param top
     */
    private void initClipView(int top) {
        mBitmap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.female);
        mClipView = new ClipView(ClipImageActivity.this);
        mClipView.setCustomTopBarHeight(top);
        mClipView.addOnDrawCompleteListener(new ClipView.OnDrawListenerComplete() {

            public void onDrawCompelete() {
                mClipView.removeOnDrawCompleteListener();
                int clipHeight = mClipView.getClipHeight();
                int clipWidth = mClipView.getClipWidth();
                int midX = mClipView.getClipLeftMargin() + (clipWidth / 2);
                int midY = mClipView.getClipTopMargin() + (clipHeight / 2);

                int imageWidth = mBitmap.getWidth();
                int imageHeight = mBitmap.getHeight();
                // 按裁剪框求缩放比例
                float scale = (clipWidth * 1.0f) / imageWidth;
                if (imageWidth > imageHeight) {
                    scale = (clipHeight * 1.0f) / imageHeight;
                }

                // 起始中心点
                float imageMidX = imageWidth * scale / 2;
                float imageMidY = mClipView.getCustomTopBarHeight()
                        + imageHeight * scale / 2;
                mSrcImage.setScaleType(ImageView.ScaleType.MATRIX);

                // 缩放
                mMatrix.postScale(scale, scale);
                // 平移
                mMatrix.postTranslate(midX - imageMidX, midY - imageMidY);

                mSrcImage.setImageMatrix(mMatrix);
                mSrcImage.setImageBitmap(mBitmap);
            }
        });

        this.addContentView(mClipView, new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                // 设置开始点位置
                mStart.set(event.getX(), event.getY());
                mMode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDist = spacing(event);
                if (mOldDist > 10f) {
                    mSavedMatrix.set(mMatrix);
                    midPoint(mMid, event);
                    mMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == DRAG) {
                    mMatrix.set(mSavedMatrix);
                    mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
                            - mStart.y);
                } else if (mMode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = newDist / mOldDist;
                        mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                    }
                }
                break;
        }
        view.setImageMatrix(mMatrix);
        return true;
    }

    /**
     * 多点触控时，计算最先放下的两指距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 多点触控时，计算最先放下的两指中心坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * 获取裁剪框内截图
     *
     * @return
     */
    private Bitmap getBitmap() {
        // 获取截屏
        View view = this.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap finalBitmap = Bitmap.createBitmap(view.getDrawingCache(),
                mClipView.getClipLeftMargin(), mClipView.getClipTopMargin()
                        + statusBarHeight, mClipView.getClipWidth(),
                mClipView.getClipHeight());

        // 释放资源
        view.destroyDrawingCache();
        return finalBitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PLog.log("onBackPressed");
    }
}
