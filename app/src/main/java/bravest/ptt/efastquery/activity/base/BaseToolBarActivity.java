package bravest.ptt.efastquery.activity.base;

import bravest.ptt.androidlib.activity.toolbar.AbstractToolbarActivity;
import bravest.ptt.efastquery.net.AbstractRequestCallback;
import bravest.ptt.efastquery.net.bmob.BmobRequestManager;

/**
 * Created by root on 6/23/17.
 */

public abstract class BaseToolBarActivity extends AbstractToolbarActivity{
    @Override
    protected void initRequestManager() {
        mAbstractRequestManager = new BmobRequestManager(mContext);
    }

    protected class InnerRequestCallback extends AbstractRequestCallback {
        public InnerRequestCallback() {
            super(mContext);
        }
    }
}
