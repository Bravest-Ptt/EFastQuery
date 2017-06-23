package bravest.ptt.efastquery.activity.base;

import bravest.ptt.androidlib.activity.AbstractBaseActivity;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.net.bmob.BmobRequestManager;

public abstract class BaseActivity extends AbstractBaseActivity{
    @Override
    protected void initRequestManager() {
        mAbstractRequestManager = new BmobRequestManager(mContext);
    }

    protected class InnerRequestCallback extends AbstractRequestCallback{
        public InnerRequestCallback() {
            super(mContext);
        }
    }
}
