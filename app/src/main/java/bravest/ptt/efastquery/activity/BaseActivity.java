package bravest.ptt.efastquery.activity;

import android.content.Context;

import bravest.ptt.androidlib.activity.AbstractBaseActivity;
import bravest.ptt.androidlib.net.RemoteService;
import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.net.bmob.BmobRequestManager;

public abstract class BaseActivity extends AbstractBaseActivity{
    @Override
    protected void initVariables() {
        mAbstractRequestManager = new BmobRequestManager(mContext);
    }

    protected void _NET(String apiKey, RequestParam param, AbstractRequestCallback callback) {
        RemoteService.getInstance().invoke(mActivity, apiKey, param, callback);
    }

    protected class InnerRequestCallback extends AbstractRequestCallback{

        public InnerRequestCallback(Context context) {
            super(context);
        }

        public InnerRequestCallback() {
            super(mContext);
        }
    }
}
