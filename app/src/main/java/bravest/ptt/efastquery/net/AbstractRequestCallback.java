package bravest.ptt.efastquery.net;

import android.content.Context;

import bravest.ptt.androidlib.net.RequestCallback;
import bravest.ptt.androidlib.utils.DialogUtils;

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
                ,"Error"
                ,errorMessage
                ,"OK"
                ,null
                ,null
                ,null
        );
    }

    @Override
    public void onCookieExpired() {
        DialogUtils.showPrompt(mContext, "请重新登录");
    }
}
