package bravest.ptt.efastquery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.data.wordbook.W;
import bravest.ptt.efastquery.provider.FavoriteManager;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.view.adapter.FDPagerAdapter;
import bravest.ptt.efastquery.view.adapter.recycler.FavoritePreAdapter;

/**
 * Created by root on 2/13/17.
 */

public class FavoriteFragment extends BaseFragment implements ItemClickListener{

    private ArrayList<W> mData = new ArrayList<>();

    private FavoriteManager mFm;
    private FavoritePreAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private View mSwipeCardsPanel;
    private View mEmptyView;

    private String mCurrentGroup;
    private ViewPager mFDPager;
    private PagerAdapter mPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFm = new FavoriteManager(getContext());
        mAdapter = new FavoritePreAdapter();
        mAdapter.setData(mData)
                .setContext(getContext())
                .setOnItemClickListener(this);
        mCurrentGroup = getContext().getString(R.string.group_default);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.favorite_recycler_view);
        mRefresher = (SwipeRefreshLayout) view.findViewById(R.id.favorite_refresher);
        mSwipeCardsPanel= view.findViewById(R.id.favorite_cards_panel);
        mFDPager = (ViewPager) view.findViewById(R.id.fd_pager);
        mEmptyView = view.findViewById(R.id.favorite_empty);

        initPageAdapter();
        initRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkoutGroup(mCurrentGroup);
    }

    private void initPageAdapter() {
        mPagerAdapter = new FDPagerAdapter(getContext(), mData);
        mFDPager.setAdapter(mPagerAdapter);
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Get Data from database
     * @param group
     */
    private void checkoutGroup(String group) {
        if (mData == null || TextUtils.isEmpty(group)) {
            return;
        }
        mData.clear();
        mFm.getFavoriteByGroup(mData, group, FavoriteManager.MODE_RESULT);

        //Update UI Show
        if (mRecyclerView != null) {
            if (mData.size() == 0) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void refreshData() {
        super.refreshData();
        checkoutGroup(mCurrentGroup);
    }

    @Override
    protected void onDataRefresh() {
        super.onDataRefresh();
    }

    @Override
    public void onItemClicked(View view, int position) {
        showHideCardPanel(true);
        mFDPager.setCurrentItem(position, false);
        PLog.log("item click");
    }


    private void showHideCardPanel(boolean show) {
        if (show) {
            mSwipeCardsPanel.setVisibility(View.VISIBLE);
        } else {
            mSwipeCardsPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean isRootPanel() {
        return !(mSwipeCardsPanel != null && mSwipeCardsPanel.getVisibility() == View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mSwipeCardsPanel != null && mSwipeCardsPanel.getVisibility() == View.VISIBLE) {
                showHideCardPanel(false);
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
