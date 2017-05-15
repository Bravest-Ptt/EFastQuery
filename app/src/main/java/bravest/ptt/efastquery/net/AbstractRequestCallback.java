package bravest.ptt.efastquery.net;

import android.content.Context;
import android.content.DialogInterface;

import bravest.ptt.androidlib.net.RequestCallback;
import bravest.ptt.androidlib.utils.DialogUtils;
import bravest.ptt.efastquery.R;

/**
 * Created by pengtian on 2017/5/12.
 */

public abstract class AbstractRequestCallback implements RequestCallback {

    private Context mContext;

    public AbstractRequestCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSuccess(String content) {
    }

    @Override
    public void onFail(String errorMessage) {
        DialogUtils.showAlert(mContext
                , R.style.Dialog_Error
                , "Error"
                , errorMessage
                , "OK"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
                , null
                , null
        );
    }

    @Override
    public void onCookieExpired() {
        DialogUtils.showPrompt(mContext, "请重新登录");
    }
}
