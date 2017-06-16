package bravest.ptt.androidlib.activity.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Vector;

/**
 * Created by root on 6/16/17.
 */

public class ActivityStack implements Application.ActivityLifecycleCallbacks{

    private static ActivityStack sStack = new ActivityStack();

    private Vector<Activity> mActivities = new Vector<>();

    public static ActivityStack getInstance() {
        return sStack;
    }

    public ActivityStack() {

    }

    public Activity getTopActivity() {
        if (mActivities.size() > 0) {
            return mActivities.get(mActivities.size() - 1);
        } else {
            return null;
        }
    }

    public Activity getBackActivity() {
        if (mActivities.size() > 1) {
            return mActivities.get(mActivities.size() - 2);
        } else {
            return null;
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        int index = mActivities.indexOf(activity);

        if (index == -1) {
            mActivities.add(activity);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mActivities.remove(activity);
    }
}
