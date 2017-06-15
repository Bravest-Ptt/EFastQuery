package bravest.ptt.canvasdemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import bravest.ptt.canvasdemo.entity.PieData;

/**
 * Created by pengtian on 2017/5/28.
 */

public class PieView extends View {
    //初始化应用颜色表（注意：此处定义颜色使用的是ARGB，带alpha通道）
    private int[] mColors = {
            0xffccff00,
            0xff6495ed,
            0xffe32636,
            0xff800000,
            0xffff8c69,
            0xff808080,
            0xffe6b800,
            0xff7cfc00
    };

    //饼状图初始绘制角度
    private float mStartAngle = 0;

    //数据
    private ArrayList<PieData> mData;

    //宽高
    private int mWidth, mHeight;

    //画笔
    private Paint mPaint = new Paint();

    public PieView(Context context) {
        super(context);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == mData) {
            return;
        }
        float currentStartAngle = mStartAngle;                       //当前起始角度
        canvas.translate(mWidth / 2, mHeight / 2);                   //将画布坐标原点移动到中心位置
        float r = (float) (Math.min(mWidth, mHeight) / 2 * 0.8f);    //饼状图半径
        RectF rectF = new RectF(-r, -r, r, r);                       //饼状图绘制区域
        for (int i = 0; i < mData.size(); i++) {
            PieData data = mData.get(i);
            mPaint.setColor(data.getColor());
            canvas.drawArc(rectF, currentStartAngle, data.getAngle(), true, mPaint);
            currentStartAngle += data.getAngle();
        }
    }

    public void setStartAngle(int startAngle) {
        this.mStartAngle = startAngle;
        invalidate();  //刷新
    }

    public void setData(ArrayList<PieData> data) {
        this.mData = data;
        intData(mData);
        invalidate(); //刷新
    }

    private void intData(ArrayList<PieData> data) {
        if (null == data || data.size() == 0) {
            return;
        }

        float sumValue = 0;
        for (int i = 0; i < data.size(); i++) {
            PieData pie = data.get(i);

            sumValue += pie.getValue();  //计算数值的和

            int j = i % mColors.length; //设置颜色

            pie.setColor(mColors[j]);
        }
        float sumAngle = 0;
        for (int i = 0; i < data.size(); i++) {
            PieData pie = data.get(i);

            float percentage = pie.getAngle();  // 百分比
            float angle = percentage * 360;     // 对应的角度

            pie.setPercentage(percentage);      // 记录百分比
            pie.setAngle(angle);                // 记录角度大小

            sumAngle += angle;

            Log.i("angle", "" + pie.getAngle());
        }
    }

    //提供接口
}
