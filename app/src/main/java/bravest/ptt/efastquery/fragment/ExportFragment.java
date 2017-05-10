package bravest.ptt.efastquery.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.listeners.OnBuildListener;
import bravest.ptt.efastquery.engine.word.DocBuilder;
import bravest.ptt.efastquery.interfaces.IWord;
import bravest.ptt.efastquery.engine.word.XmlBuilder;
import bravest.ptt.efastquery.utils.FileUtils;
import bravest.ptt.efastquery.db.FavoriteManager;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.utils.Utils;

import static bravest.ptt.efastquery.utils.FileUtils.EXTERNAL;

/**
 * Created by root on 2/13/17.
 */

public class ExportFragment extends FileManagerFragment implements AdapterView.OnItemSelectedListener, OnBuildListener {

    private static final String TAG = "ExportFragment";

    String group_name;
    String file_name;
    String file_extension;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData.addAll(FileUtils.getPathContent(EXTERNAL, FileUtils.MODE_NORMAL_EXPORT));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.export_spinner_entries));

        fileAdapter.setDropDownViewResource(R.layout.drop_down_item);
        mExtensionSpinner.setAdapter(fileAdapter);
        mExtensionSpinner.setOnItemSelectedListener(this);

        //xml default
        mDescTextView.setText(R.string.file_manager_desc_xml);
        mDescTextView.setTextColor(getResources().getColor(R.color.export_orange_500_4_toolbar));

        //xml default
        file_extension = mActivity.getResources().getStringArray(R.array.export_spinner_entries)[0];

        group_name = mActivity.getSelectGroup();
    }

    @Override
    protected void onDataRefresh() {
    }

    @Override
    protected void refreshData() {
        ArrayList<File> ret = FileUtils.getPathContent(mCurrentFile.getAbsolutePath(), FileUtils.MODE_NORMAL_EXPORT);
        if (ret != null) {
            super.refreshData();
            mData.clear();
            mData.addAll(ret);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.file_manager_spinner:
                file_extension = (String)adapterView.getSelectedItem();
                PLog.log("file_extension = " + file_extension);
                if (file_extension.equals(getString(R.string.export_xml))) {
                    mDescTextView.setText(R.string.file_manager_desc_xml);
                } else if(file_extension.equals(getString(R.string.export_doc))){
                    mDescTextView.setText(R.string.file_manager_desc_doc);
                } else if(file_extension.equals(getString(R.string.export_txt))) {
                    mDescTextView.setText(R.string.file_manager_desc_txt);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClicked(View view, int position) {
        super.onItemClicked(view, position);
        if (mCurrentFile.isFile()) {
            FileUtils.openFile(mActivity, mCurrentFile);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.file_manager_save_button) {
            file_name = mInputView.getText().toString();
            if (TextUtils.isEmpty(file_name)){
                Toast.makeText(mActivity,getString(R.string.export_file_name_need), Toast.LENGTH_SHORT).show();
                return;
            }

            String finalFilePath = getParentFolderName() + "/" + file_name + file_extension;

            File file = new File(finalFilePath);
            if (file.exists()) {
                Toast.makeText(mActivity, getString(R.string.file_manager_file_already_exist), Toast.LENGTH_SHORT).show();
                return;
            }else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(mActivity, getString(R.string.file_manager_file_name_invalid), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //Below should load async
            FavoriteManager fm = new FavoriteManager(getContext());
            ArrayList<IWord> data = fm.getFavoriteByGroup(null, group_name, FavoriteManager.MODE_WORDBOOK);

            if (TextUtils.equals(file_extension, getString(R.string.export_doc))) {
                DocBuilder.getInstance(mActivity).setBuildListener(this).createDoc(file, data);
            } else if (TextUtils.equals(file_extension, getString(R.string.export_txt))) {

            } else if (TextUtils.equals(file_extension, getString(R.string.export_xml))) {
                XmlBuilder.getInstance().setBuildListener(this).createXML(file, data);
            }
        }
    }

    @Override
    public void onBuildingResult(final File file, boolean success) {
        if (success) {
            refreshData();
//            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
//            View hello = mActivity.getLayoutInflater().inflate(R.layout.dialog_gif, null);
//            GifImageView happyGif = (GifImageView) hello.findViewById(R.id.dialog_gif_iv);
//            TextView pathView = (TextView) hello.findViewById(R.id.dialog_gif_path_tv);
//            try {
//                happyGif.setImageDrawable(new GifDrawable(mActivity.getAssets(), FileUtils.ASSETS_GIF_HAPPY));
//                pathView.setText(file.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            builder.setView(hello).setNegativeButton(R.string.export_success_check, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    FileUtils.openFile(mActivity, file);
//                }
//            }).setPositiveButton(R.string.export_success_share, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    FileUtils.shareFile(mActivity, file);
//                }
//            }).setNeutralButton(getString(R.string.file_manager_cancel), null).show();
            Utils.hideSoftInput(mActivity, mInputView);
        }
    }
}
