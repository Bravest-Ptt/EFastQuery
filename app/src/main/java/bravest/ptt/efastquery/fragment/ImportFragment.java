package bravest.ptt.efastquery.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.utils.FileUtils;

import static bravest.ptt.efastquery.utils.FileUtils.EXTERNAL;

/**
 * Created by root on 2/13/17.
 */

public class ImportFragment extends FileManagerFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData.addAll(FileUtils.getPathContent(EXTERNAL, FileUtils.MODE_NORMAL_IMPORT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mFooterView.setVisibility(View.GONE);
        mNewFolder.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onItemClicked(View view, int position) {
        super.onItemClicked(view, position);
        if (mCurrentFile.isFile()) {
            createConfirmDialog();
        }
    }

    private void createConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.import_confirm)
                .setMessage(getString(R.string.import_confirm_info, mCurrentFile.getName(), mActivity.getSelectGroup()))
                .setPositiveButton(R.string.import_file, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Import
                    }
                })
                .setNeutralButton(R.string.file_manager_create_group, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mActivity.addGroupDialog();
                    }
                }).setNegativeButton(R.string.file_manager_cancel, null)
                .show();
    }

    @Override
    protected void refreshData() {
        ArrayList<File> ret = FileUtils.getPathContent(mCurrentFile.getAbsolutePath(), FileUtils.MODE_NORMAL_IMPORT);
        if (ret != null) {
            mData.clear();
            mData.addAll(ret);
            notifyDataSetChanged();
        }
    }
}
