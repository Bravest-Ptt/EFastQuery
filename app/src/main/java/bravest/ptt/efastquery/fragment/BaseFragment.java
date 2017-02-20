package bravest.ptt.efastquery.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import bravest.ptt.efastquery.MainActivity;

/**
 * Created by root on 2/18/17.
 */

public abstract class BaseFragment extends Fragment {

    protected MainActivity mActivity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    public boolean isRootPanel() {
        return true;
    }
}
