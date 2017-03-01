package bravest.ptt.efastquery.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;

import bravest.ptt.efastquery.MainActivity;
import bravest.ptt.efastquery.R;

/**
 * Created by root on 2/18/17.
 */

public abstract class BaseFragment extends Fragment {

    protected MainActivity mActivity;
    protected SwipeRefreshLayout mRefresher;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRefresher();
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

    protected void initRefresher() {
        if (mRefresher == null) {
            return;
        }
        mRefresher.setColorSchemeResources(R.color.home_red_500_4_toolbar, R.color.export_orange_500_4_toolbar, R.color.import_blue_500_4_toolbar);
        mRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefresher.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }

    protected void refreshData() {
        onDataRefresh();
    }

    protected void onDataRefresh() {
    }
}
