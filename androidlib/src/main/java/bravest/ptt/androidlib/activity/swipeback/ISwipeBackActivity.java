package bravest.ptt.androidlib.activity.swipeback;

/**
 * Created by root on 6/16/17.
 */

public interface ISwipeBackActivity {

    /**
     * 获取此activity的SwipeBackLayout
     */
    SwipeBackLayout getSwipeBackLayout();

    /**
     * 获取上一个activity。 这点很重要,当大家维护一个activity栈时,一定要获取正确。尤其是遇到旋转屏或者activity因内存不足被杀死时。
     */
    ISwipeBackActivity getPreActivity();

    /**
     * 限制SwipeBack的条件,默认栈内Fragment数 <= 1时 , 优先滑动退出Activity , 而不是Fragment
     *
     * @return true: Activity可以滑动退出, 并且总是优先; false: Activity不允许滑动退出
     */
    boolean swipeBackPriority();

    /**
     * 设置此SwipeBackLayout是否支持滑动返回
     * @param enable true
     */
    void setSwipeBackEnable(boolean enable);

    /**
     * 设置activity背景是否透明,默认返回true。若是最底层的activity或者是不支持滑动返回时,请设置为为false
     */
    boolean isTransparent();
}
