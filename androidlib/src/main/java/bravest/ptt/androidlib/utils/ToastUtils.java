package bravest.ptt.androidlib.utils;

/**
 * Created by pengtian on 2017/5/11.
 */

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ToastUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-12-9
 */
public class ToastUtils {
    private static Toast toast = null; //Toast的对象！

    public static void showToast(Context mContext, String id) {
        if (mContext == null) {
            throw new NullPointerException();
        }
        if (TextUtils.isEmpty(id)) {
            return;
        }
        if (toast == null) {
            toast = Toast.makeText(mContext, id, Toast.LENGTH_SHORT);
        }
        else {
            toast.setText(id);
        }
        toast.show();
    }

    public static void showToast(Context mContext, int id) {
        if (mContext == null) {
            throw new NullPointerException();
        }
        showToast(mContext, mContext.getString(id));
    }
}
