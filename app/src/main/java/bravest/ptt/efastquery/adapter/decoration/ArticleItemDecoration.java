package bravest.ptt.efastquery.adapter.decoration;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import bravest.ptt.efastquery.utils.Utils;

/**
 * Created by pengtian on 2017/8/13.
 */

public class ArticleItemDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    public ArticleItemDecoration(int color) {
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
//        int childCount = parent.getChildCount();
//        Rect rect = new Rect();
//        rect.left = parent.getPaddingLeft();
//        rect.right = parent.getWidth() - parent.getPaddingRight();
//        for (int i = 0; i < childCount; i++) {
//            View childView = parent.getChildAt(i);
//            rect.top = childView.getBottom();
//            rect.bottom = childView.getBottom() + Utils.dp2px(10);
//            c.drawRect(rect, mPaint);
//        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top += Utils.dp2px(5);
        outRect.bottom +=Utils.dp2px(5);
        outRect.left +=Utils.dp2px(10);
        outRect.right +=Utils.dp2px(10);
    }
}
