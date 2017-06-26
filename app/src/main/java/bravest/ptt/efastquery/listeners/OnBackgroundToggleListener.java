package bravest.ptt.efastquery.listeners;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public abstract class OnBackgroundToggleListener implements View.OnTouchListener{

    private static final String TAG = "OnBackgroundToggleListe";

    private int normalColor;

    private int pressedColor;

    private float currentX, currentY;

    protected OnBackgroundToggleListener(int normalColor, int pressedColor) {
        this.normalColor = normalColor;
        this.pressedColor = pressedColor;
    }

    public void setNormalColor(int normalColor) {
        this.normalColor = normalColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Context context = view.getContext();
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        final int top = location[1];
        final int left = location[0];
        final int bottom = location[1] +  view.getMeasuredHeight();
        final int right = location[0] + view.getMeasuredWidth();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view.setBackgroundColor(context.getResources().getColor(pressedColor));
                break;
            case MotionEvent.ACTION_UP:
                view.setBackgroundColor(context.getResources().getColor(normalColor));
                if ((currentX > left && currentX < right)
                        && (currentY > top && currentY < bottom)) {
                    handleClick();
                    return false;
                } else {
                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                currentX = motionEvent.getRawX();
                currentY = motionEvent.getRawY();
                break;
            default:
                break;
        }
        return false;
    }

    protected abstract void handleClick();
}
