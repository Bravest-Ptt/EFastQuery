package bravest.ptt.efastquery.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.wordbook.DocBuilder;
import bravest.ptt.efastquery.data.wordbook.WordBook;
import bravest.ptt.efastquery.data.wordbook.XmlBuilder;
import bravest.ptt.efastquery.provider.FavoriteManager;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by root on 2/13/17.
 */

public class ExportFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = "ExportFragment";

    private AppCompatSpinner mFileSpinner;
    private AppCompatSpinner mGroupSpinner;
    private TextView mDirectorypathTv;
    private Button mSaveButton;
    private TextInputEditText mExportInput;

    private Activity mActivity;

    private FavoriteManager mFm;

    String dir_name;
    String group_name;
    String file_name;
    String file_extension;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFm = new FavoriteManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_manager, null);
        mFileSpinner = (AppCompatSpinner) view.findViewById(R.id.export_input_li_spinner);
        mGroupSpinner = (AppCompatSpinner) view.findViewById(R.id.export_group_spinner);
        mDirectorypathTv = (TextView) view.findViewById(R.id.export_text_directory_path);

        mSaveButton = (Button) view.findViewById(R.id.export_save_button);
        mSaveButton.setOnClickListener(this);

        mExportInput = (TextInputEditText) view.findViewById(R.id.export_input_li_editor);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] groups = FavoriteManager.getGroup(getContext());

        ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item,
                mActivity.getResources().getStringArray(R.array.export_spinner_entries));
        ArrayAdapter<String> groupAdapter = new ArrayAdapter<String>(mActivity,
                android.R.layout.simple_spinner_item,
                groups);

        fileAdapter.setDropDownViewResource(R.layout.drop_down_item);
        groupAdapter.setDropDownViewResource(R.layout.drop_down_item);

        mFileSpinner.setAdapter(fileAdapter);
        mGroupSpinner.setAdapter(groupAdapter);

        mFileSpinner.setOnItemSelectedListener(this);
        mGroupSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.export_input_li_spinner:
                file_extension = (String)adapterView.getSelectedItem();
                dir_name = file_extension.substring(1);
                String text = mActivity.getString(R.string.export_directory_path, dir_name);
                mDirectorypathTv.setText(text);
                break;
            case R.id.export_group_spinner:
                group_name = (String)adapterView.getSelectedItem();
                Log.d(TAG, "onItemSelected: group_name = " + group_name);
//                if (TextUtils.equals(group_name, getString(R.string.export_group_default))) {
//                    group_name = null;
//                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.export_save_button) {
            file_name = mExportInput.getText().toString();
            if (TextUtils.isEmpty(file_name)){
                Toast.makeText(mActivity,getString(R.string.export_file_name_need), Toast.LENGTH_SHORT).show();
                return;
            }

            String finalDirPath = XmlBuilder.EXTERNAL_DIR + dir_name;
            String finalFilePath = finalDirPath + "/" + file_name + file_extension;

            //dir exist?
            File dirFile = new File(finalDirPath);
            if (dirFile.exists() && !dirFile.isDirectory()) {
                dirFile.delete();
            } else if (!dirFile.exists()){
                dirFile.mkdirs();
            }

            File file = new File(finalFilePath);
            if (file.exists()) {
                Toast.makeText(mActivity, getString(R.string.file_manager_file_already_exist), Toast.LENGTH_SHORT).show();
                return;
            }else {
                try {
                    Log.d(TAG, "onClick: finalPath = " + finalFilePath);
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, "onClick: "  + e);
                    Toast.makeText(mActivity, getString(R.string.file_manager_file_name_invalid), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    return;
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            View hello = mActivity.getLayoutInflater().inflate(R.layout.dialog_gif, null);
            GifImageView hellogif = (GifImageView) hello.findViewById(R.id.dialog_gif_iv);
            try {
                hellogif.setImageDrawable(new GifDrawable(mActivity.getAssets(), "hello1.gif"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.setView(hello).setNegativeButton("Check", null).setPositiveButton("Share", null).setNeutralButton("Cancel", null).show();
            //Below should load async
            FavoriteManager fm = new FavoriteManager(getContext());
            ArrayList<WordBook> data = fm.getGroupFavorite(group_name);

            if (TextUtils.equals(dir_name, getString(R.string.export_doc_dir))) {
                DocBuilder.getInstance(mActivity).createDoc(file, data);
            } else if (TextUtils.equals(dir_name, getString(R.string.export_txt_dir))) {

            } else if (TextUtils.equals(dir_name, getString(R.string.export_xml_dir))) {

            }
        }
    }
}
