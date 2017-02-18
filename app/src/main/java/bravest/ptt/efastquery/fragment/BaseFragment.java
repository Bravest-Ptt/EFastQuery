package bravest.ptt.efastquery.fragment;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by root on 2/18/17.
 */

public abstract class BaseFragment extends Fragment {
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        getActivity().finish();
        return false;
    }
}
