package bravest.ptt.efastquery.view;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.data.TranslateListener;
import bravest.ptt.efastquery.data.TranslateManager;
import bravest.ptt.efastquery.utils.Utils;
import bravest.ptt.efastquery.view.ESearchFloatButton.*;

/**
 * Created by root on 1/4/17.
 */

class ESearchMainPanel implements View.OnClickListener, TranslateListener{

    private Context mContext;
    private WindowManager mWm;
    private TranslateManager mTm;
    private View mMain;
    private View mMainPanel;
    private ImageButton mMainSearch;
    private EditText mMainInput;
    private View mMainShowResultPanel;
    private EditText mMainShowResultText;
    private ListView mMainShowHistory;

    public ESearchMainPanel(Context context, WindowManager wm) throws InflaterNotReadyException {
        mContext = context;
        mWm = wm;
        mTm = new TranslateManager(mContext);
        mTm.setTranslateListener(this);

        initViews();
    }

    private void initViews() throws InflaterNotReadyException {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            throw new InflaterNotReadyException();
        }
        mMain = inflater.inflate(R.layout.layout_search_main_panel, null);
        mMainPanel = mMain.findViewById(R.id.main_panel);
        mMainSearch = (ImageButton) mMain.findViewById(R.id.main_panel_search_button);
        mMainInput = (EditText) mMain.findViewById(R.id.main_panel_search_edit);

        //Main
        mMain.setOnClickListener(this);

        //Panel
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)mMainPanel.getLayoutParams();
        layoutParams.width = Utils.getScreenWidth(mContext) * 7 / 12;
        layoutParams.height = Utils.getScreenHeight(mContext) * 5 / 12;
        mMainPanel.setLayoutParams(layoutParams);

        //Search button
        mMainSearch.setOnClickListener(this);

        //Search edit text
        mMainInput.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mMainInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                }
                return true;
            }
        });

        initShowPanel();
    }

    private void initShowPanel() {
        mMainShowResultPanel = mMain.findViewById(R.id.main_panel_show_result);
        mMainShowResultText = (EditText) mMain.findViewById(R.id.main_panel_show_result_text);
        mMainShowHistory = (ListView) mMain.findViewById(R.id.main_panel_show_history);

        //Init history

    }

    private void initLayoutParams() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }

    private void doSearch() {

    }

    @Override
    public void onTranslateStart() {

    }

    @Override
    public void onTranslateSuccess(Result result) {

    }

    @Override
    public void onTranslateFailed(String error) {

    }
}
