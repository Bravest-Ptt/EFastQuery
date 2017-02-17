package bravest.ptt.efastquery.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.files.FileUtils;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.view.FileManagerAdapter;

/**
 * Created by root on 2/17/17.
 */

public class FileManagerFragment extends Fragment implements ItemClickListener{
    protected Activity mActivity;

    protected LinearLayout mMainView;
    protected HorizontalScrollView mIndicator;
    protected ImageView mNewFolder;
    protected SwipeRefreshLayout mRefresher;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mFooterView;
    protected LinearLayout mHeaderView;
    protected TextInputEditText mInputView;
    protected AppCompatSpinner mExtensionSpinner;

    protected FileManagerAdapter mAdapter;
    protected ArrayList<File> mData;

    protected File mCurrentFile = Environment.getExternalStorageDirectory();

    private static final String EXTERNAL = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = FileUtils.getPathContent(EXTERNAL, FileUtils.MODE_NORMAL);

        mAdapter = new FileManagerAdapter(getContext(), mData);
        mAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_manager, null);

        mMainView = (LinearLayout) view.findViewById(R.id.file_manager_main);
        mIndicator = (HorizontalScrollView) view.findViewById(R.id.file_manager_folder_indicator);
        mNewFolder = (ImageView) view.findViewById(R.id.file_manager_folder_add);
        mRefresher = (SwipeRefreshLayout) view.findViewById(R.id.file_manager_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.file_manager_content);
        mFooterView = (LinearLayout) view.findViewById(R.id.file_manager_footer);
        mHeaderView = (LinearLayout) view.findViewById(R.id.file_manager_header);
        mInputView = (TextInputEditText) view.findViewById(R.id.file_manager_editor);
        mExtensionSpinner = (AppCompatSpinner) view.findViewById(R.id.file_manager_spinner);

        initRecyclerView();
        initRefresher();

        return view;
    }

    private void initRefresher() {
        mRefresher.setColorSchemeResources(R.color.home_red_500_4_toolbar, R.color.export_orange_500_4_toolbar, R.color.import_blue_500_4_toolbar);
        mRefresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PLog.log("on refresh");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefresher.setRefreshing(false);
                    }
                },500);
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        mRecyclerView.hasFixedSize();
        mRecyclerView.setAdapter(mAdapter);


        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int h = mRecyclerView.getHeight();
                PLog.log(h);
                PLog.log(mFooterView.getHeight());

                ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
                params.height = mRecyclerView.getHeight() - mFooterView.getHeight() - mHeaderView.getHeight();
                mRecyclerView.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private void refreshData() {
        mData = FileUtils.getPathContent(mCurrentFile.getAbsolutePath(),FileUtils.MODE_NORMAL);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(View view, int position) {
        Toast.makeText(mActivity, "position = " + position, Toast.LENGTH_SHORT).show();
        if (mData == null) {
            return;
        }
        mCurrentFile = mData.get(position);
        PLog.log(mCurrentFile.getAbsolutePath());
        refreshData();
    }


}
