package bravest.ptt.efastquery.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.callback.ItemClickListener;
import bravest.ptt.efastquery.files.FileUtils;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.utils.Utils;
import bravest.ptt.efastquery.view.FileManagerAdapter;

/**
 * Created by root on 2/17/17.
 */

public class FileManagerFragment extends BaseFragment implements ItemClickListener, View.OnClickListener {
    protected Activity mActivity;

    protected LinearLayout mMainView;
    protected HorizontalScrollView mIndicatorScroller;
    protected LinearLayout mIndicator;
    protected ImageView mNewFolder;
    protected SwipeRefreshLayout mRefresher;
    protected RecyclerView mRecyclerView;
    protected LinearLayout mFooterView;
    protected LinearLayout mHeaderView;
    protected TextInputEditText mInputView;
    protected AppCompatSpinner mExtensionSpinner;
    protected TextView mFolderEmptyTip;
    protected TextView mFolderExternal;

    protected FileManagerAdapter mAdapter;
    protected ArrayList<File> mData;

    protected File mCurrentFile = Environment.getExternalStorageDirectory();

    private static final String EXTERNAL = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<>();
        mData.addAll(FileUtils.getPathContent(EXTERNAL, FileUtils.MODE_NORMAL));

        mAdapter = new FileManagerAdapter(getContext(), mData);
        mAdapter.setOnItemClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_manager, null);

        mMainView = (LinearLayout) view.findViewById(R.id.file_manager_main);
        mIndicator = (LinearLayout) view.findViewById(R.id.file_manager_folder_indicator);
        mNewFolder = (ImageView) view.findViewById(R.id.file_manager_folder_add);
        mRefresher = (SwipeRefreshLayout) view.findViewById(R.id.file_manager_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.file_manager_content);
        mFolderEmptyTip = (TextView) view.findViewById(R.id.file_manager_content_empty);
        mFooterView = (LinearLayout) view.findViewById(R.id.file_manager_footer);
        mHeaderView = (LinearLayout) view.findViewById(R.id.file_manager_header);
        mInputView = (TextInputEditText) view.findViewById(R.id.file_manager_editor);
        mExtensionSpinner = (AppCompatSpinner) view.findViewById(R.id.file_manager_spinner);
        mIndicatorScroller = (HorizontalScrollView) view.findViewById(R.id.file_manager_indicator_scroller);
        mFolderExternal = (TextView) view.findViewById(R.id.file_manager_external_folder);

        //Add new folder
        mNewFolder.setOnClickListener(this);
        mFolderExternal.setOnClickListener(this);
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
                        refreshData();
                        mRefresher.setRefreshing(false);
                    }
                }, 500);
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
        ArrayList<File> ret = FileUtils.getPathContent(mCurrentFile.getAbsolutePath(), FileUtils.MODE_NORMAL);
        if (ret != null) {
            mData.clear();
            mData.addAll(ret);
            notifyDataSetChanged();
        }
    }

    private void notifyDataSetChanged() {
        if (mData.size() == 0) {
            PLog.log(mData.size());
            mFolderEmptyTip.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mFolderEmptyTip.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(View view, int position) {
        if (mData == null) {
            return;
        }
        mCurrentFile = mData.get(position);
        PLog.log(mCurrentFile.getAbsolutePath());
        if (!mCurrentFile.exists()) {
            Toast.makeText(mActivity, mActivity.getString(R.string.file_manager_file_not_exist), Toast.LENGTH_SHORT).show();
            mCurrentFile = mCurrentFile.getParentFile();
        }

        addViewToIndicator();

        refreshData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurrentFile.getAbsolutePath().equals(EXTERNAL)) {
                //handle normally
                super.onKeyDown(keyCode, event);
            } else {
                //update header indicator
                if (mCurrentFile.isFile()
                        && !mCurrentFile.getParentFile().getAbsolutePath().equals(EXTERNAL)) {
                    //current folder
                    mIndicator.removeView(getViewByTag(mCurrentFile.getParentFile()));
                    //up-level folder
                    mCurrentFile = mCurrentFile.getParentFile().getParentFile();
                } else {
                    //current folder
                    mIndicator.removeView(getViewByTag(mCurrentFile));
                    //up-level folder
                    mCurrentFile = mCurrentFile.getParentFile();
                }
                PLog.log(mCurrentFile.getAbsolutePath());

                refreshData();
                return true;
            }
        }
        return false;
    }

    private void addViewToIndicator() {
        if (mCurrentFile.isFile()) {
            return;
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        mIndicator.addView(createTextView(mCurrentFile));

        //Scroll to right automatic
        mIndicatorScroller.post(new Runnable() {
            @Override
            public void run() {
                mIndicatorScroller.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    private void removeTagChildByTag(final File file) {
        int count = mIndicator.getChildCount();
        int index = Integer.MAX_VALUE;
        for (int i = 0; i < count; i++) {
            View view = mIndicator.getChildAt(i);
            if (view == null) {
                continue;
            }
            File tag = (File) view.getTag();
            if (tag.equals(file)) {
                index = i;
            }
            if (i > index) {
                mIndicator.removeView(view);
                i--;
            }
        }
    }

    private View getViewByTag(File file) {
        int count = mIndicator.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mIndicator.getChildAt(i);
            if (view.getTag().equals(file)) {
                return view;
            }
        }
        return null;
    }

    private TextView createTextView(final File file) {
        TextView textView = new TextView(mActivity);
        textView.setTag(file);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setText(file.getName());

        //When you set drawable in text view dynamic, the following # must be called
        //#
        Drawable drawable = mActivity.getResources().getDrawable(R.mipmap.file_folder_arrow);
        //#
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicWidth());
        textView.setCompoundDrawables(null, null, drawable, null);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentFile.equals(file)) {
                    return;
                } else {
                    //mCurrentFile = path;
                    mCurrentFile = file;
                    refreshData();
                    removeTagChildByTag(file);
                }
            }
        });
        return textView;
    }

    private void newFolderDialog() {
        //init views
        final int DP_MARGIN = 16;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final LinearLayout layout = new LinearLayout(mActivity);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setOrientation(LinearLayout.VERTICAL);
        params.setMargins(Utils.dp2px(mActivity,DP_MARGIN), 0 , Utils.dp2px(mActivity,DP_MARGIN), 0);
        final EditText editText = new EditText(mActivity);
        final TextView textView = new TextView(mActivity);
        textView.setTextColor(mActivity.getResources().getColor(R.color.red));
        textView.setVisibility(View.GONE);
        layout.addView(editText, params);
        layout.addView(textView, params);

        //Find valid folder name
        int i = 0;
        String parentPath = mCurrentFile.getAbsolutePath();
        String folderName = getString(R.string.file_manager_new_folder, "");
        while (true) {
            if (mCurrentFile.isFile()) {
                parentPath = mCurrentFile.getParentFile().getAbsolutePath();
            }

            if (i != 0) {
                folderName = getString(R.string.file_manager_new_folder, i+"");
            }

            File file = new File(parentPath + "/" + folderName);
            if (!file.exists()) {
                break;
            }
            i++;
        }

        final String finalParentPath = parentPath;

        //set edit text
        editText.setText(folderName);
        editText.setSelection(0, folderName.length());
        Utils.popSoftInput(mActivity, editText);

        //build dialog
        builder.setTitle(mActivity.getString(R.string.file_manager_new_folder, ""))
                .setView(layout)
                .setPositiveButton(R.string.file_manager_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createFolder(dialogInterface,
                                textView,
                                finalParentPath,
                                editText.getText().toString());
                        refreshData();
                    }
                })
                .setNegativeButton(R.string.file_manager_cancel, null)
                .show();
    }

    private void createFolder(DialogInterface dialog, final TextView textView, final String finalParentPath, final String folderName) {
        File folder = new File(finalParentPath + "/" + folderName);
        if(folder.exists()) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(getString(R.string.file_manager_file_already_exist));
            enableDialog(dialog, false);
        } else {
            try {
                folder.createNewFile();
            } catch (IOException e) {
                textView.setVisibility(View.VISIBLE);
                textView.setText(getString(R.string.file_manager_file_name_invalid));
                enableDialog(dialog, false);
                e.printStackTrace();
            }
            folder.delete();
            folder.mkdirs();
            enableDialog(dialog, true);
        }
    }

    private void enableDialog(DialogInterface dialog, boolean enable) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, enable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.file_manager_folder_add:
                newFolderDialog();
                break;
            case R.id.file_manager_external_folder:
                mIndicator.removeAllViews();
                mCurrentFile = new File(EXTERNAL);
                refreshData();
                break;
            default:
                break;
        }
    }
}
