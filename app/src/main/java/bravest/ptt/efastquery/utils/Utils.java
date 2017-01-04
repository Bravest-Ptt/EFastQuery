package bravest.ptt.efastquery.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by root on 12/27/16.
 */

public class Utils {

    public static int dp2px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfoList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceInfoList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceInfoList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceInfoList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (TextUtils.equals(serviceName.getClassName(), className)) {
                return true;
            }
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return (int) context.getResources().getDimension(resourceId);
    }
}
