package bravest.ptt.efastquery.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.provider.FavoriteManager;

/**
 * Created by root on 2/13/17.
 */

public class ExportFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private AppCompatSpinner mFileSpinner;
    private AppCompatSpinner mGroupSpinner;
    private TextView mDirectorypathTv;

    private Activity mActivity;

    private FavoriteManager mFm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFm = new FavoriteManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_export, null);
        mFileSpinner = (AppCompatSpinner) view.findViewById(R.id.export_input_li_spinner);
        mGroupSpinner = (AppCompatSpinner) view.findViewById(R.id.export_group_spinner);
        mDirectorypathTv = (TextView) view.findViewById(R.id.export_text_directory_path);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.export_input_li_spinner:
                String text = mActivity.getString(R.string.export_directory_path, ((String)adapterView.getSelectedItem()).substring(1));
                mDirectorypathTv.setText(text);
                break;
            case R.id.export_group_spinner:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
