package bravest.ptt.efastquery.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.data.wordbook.Word;
import bravest.ptt.efastquery.provider.FavoriteManager;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.utils.Utils;
import bravest.ptt.efastquery.view.adapter.recycler.FavoritePreAdapter;
import jp.wasabeef.blurry.Blurry;

/**
 * Created by root on 2/13/17.
 */

public class FavoriteFragment extends BaseFragment implements ItemClickListener{

    private ArrayList<Word> mData = new ArrayList<>();

    private FavoriteManager mFm;
    private FavoritePreAdapter mAdapter;

    private RecyclerView mRecyclerView;
    private View mSwipeCardsPanel;
    private View mMain;
    private ImageView mSwipeCardsBg;

    private String mCurrentGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFm = new FavoriteManager(getContext());
        mAdapter = new FavoritePreAdapter();
        mAdapter.setData(mData)
                .setContext(getContext())
                .setOnItemClickListener(this);
        mCurrentGroup = getContext().getString(R.string.group_default);
        checkoutGroup(mCurrentGroup);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.fragment_favorite, null);
        mRecyclerView = (RecyclerView) mMain.findViewById(R.id.favorite_recycler_view);
        mRefresher = (SwipeRefreshLayout) mMain.findViewById(R.id.favorite_refresher);
        mSwipeCardsPanel= mMain.findViewById(R.id.favorite_cards_panel);
        mSwipeCardsBg = (ImageView) mMain.findViewById(R.id.favorite_cards_im);

        initSwipeCards(mMain);
        initRecyclerView();
        return mMain;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkoutGroup(String group) {
        if (mData == null || TextUtils.isEmpty(group)) {
            return;
        }
        mData.clear();
        mData.addAll(mFm.getFavoritePreByGroup(group));
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
        PLog.log("item click");
    }


    private Bitmap mBitmap;
    private void showHideCardPanel(boolean show) {
        if (show) {
            mBitmap = Utils.getScreenBitmapWithoutToolbar(getActivity());
            Blurry.with(getContext()).from(mBitmap).into(mSwipeCardsBg);
            mSwipeCardsPanel.setVisibility(View.VISIBLE);
        } else {
            mSwipeCardsPanel.setVisibility(View.GONE);
            mSwipeCardsBg.setImageDrawable(null);
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
                System.gc();
            }
        }
    }

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i = 10;
    private void initSwipeCards(View view) {
        //add the view via xml or programmatically
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.frame);

        al = new ArrayList<>();
        al.add("pengtian");
        al.add("suye");

        //choose your favorite adapter
        arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.item, R.id.helloText, al );

        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                al.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(getContext(), "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(getContext(), "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here
                al.add("XML ".concat(String.valueOf(i)));
                arrayAdapter.notifyDataSetChanged();
                Log.d("LIST", "notified");
                i++;
            }

            @Override
            public void onScroll(float v) {
            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(getContext(), "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
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
