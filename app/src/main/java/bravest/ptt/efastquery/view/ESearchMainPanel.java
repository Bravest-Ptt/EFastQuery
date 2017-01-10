package bravest.ptt.efastquery.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.RelativeLayout;
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

class ESearchMainPanel implements View.OnClickListener, TranslateListener, ViewVisibleListener {

    private static final int WHAT_SEARCHING = 0x011;
    private static final int WHAT_DONE = 0x012;

    private Context mContext;
    private WindowManager mWm;
    private ESearchFloatButton mButton;
    private TranslateManager mTm;
    private ViewVisibleListener mButtonVisibleListener;


    private View mMain;
    private View mMainPanel;
    private ImageButton mMainSearch;
    private EditText mMainInput;
    private View mMainShowResultPanel;
    private EditText mMainShowResultText;
    private ListView mMainShowHistory;

    private WindowManager.LayoutParams mLayoutParams;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DONE:
                    break;
                case WHAT_SEARCHING:
                    break;
                default:
                    break;
            }
        }
    };

    public ESearchMainPanel(Context context, WindowManager wm, ESearchFloatButton button) throws InflaterNotReadyException {
        mContext = context;
        mWm = wm;
        mButton = button;
        mButton.setViewVisibleListener(this);
        mLayoutParams = new WindowManager.LayoutParams();
        mTm = new TranslateManager(mContext);
        mTm.setTranslateListener(this);

        initViews();
        initLayoutParams();
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

        //Main background
        mMain.setOnClickListener(this);

        //Panel
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMainPanel.getLayoutParams();
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

        //init
        mMainShowResultText.setLineSpacing(0,1.1f);

        //Init history

    }

    private void initLayoutParams() {
        mLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.alpha = 1.0f;
        mLayoutParams.format = PixelFormat.RGBA_8888;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_panel_background:
                hideSearchPanel();
                break;
            case R.id.main_panel_search_button:
                Log.d("ptt", "onClick: search_");
                doSearch();
                break;
        }
    }

    private static final String TAG = "ptt";

    private void doSearch() {
        if (mTm != null && !TextUtils.isEmpty(mMainInput.getText())) {
            String input = mMainInput.getText().toString();
            Log.d(TAG, "doSearch: input = " + input);
//            if (!input.matches("^[a-zA-Z0-9]+")) {
//                Log.d("ptt", "doSearch: return");
//                return;
//            }
            Utils.hideSoftInput(mContext, mMainInput);
            mTm.translate(input);
        }
    }

    @Override
    public void onTranslateStart() {
        mHandler.sendEmptyMessage(WHAT_SEARCHING);
        toggleVisible(true);
    }

    @Override
    public void onTranslateSuccess(Result result) {
        Log.d("ptt", "onTranslateSuccess: ");
        mMainShowResultText.setText(result.getResult());
    }

    @Override
    public void onTranslateFailed(String error) {
        Log.d("ptt", "onTranslateFailed: " + error);
        mMainShowResultText.setText(error);
    }

    private boolean mIsShowing = false;

    public void showSearchPanel() {
        if (!mIsShowing) {
            mIsShowing = true;
            mWm.addView(mMain, mLayoutParams);
            if (mButtonVisibleListener != null) {
                mButtonVisibleListener.onShow();
            }
            mMainInput.setFocusable(true);
        }
    }

    public void hideSearchPanel() {
        if (mIsShowing) {
            mIsShowing = false;
            mWm.removeView(mMain);
            if (mButtonVisibleListener != null) {
                mButtonVisibleListener.onHide();
            }
        }
    }

    public boolean isShowing() {
        return mIsShowing;
    }

    /**
     * This is Float Button's show action instead of main panel.
     */
    @Override
    public void onShow() {
        //hide main panel
        hideSearchPanel();
    }

    /**
     * This is Float Button's hide action instead of main panel.
     */
    @Override
    public void onHide() {
        //show main panel
        showSearchPanel();
    }

    public void setViewVisibleListener(ViewVisibleListener listener) {
        mButtonVisibleListener = listener;
    }

    private void toggleVisible(boolean translating) {
        mMainShowHistory.setVisibility(translating ? View.GONE : View.VISIBLE);
        mMainShowResultPanel.setVisibility(translating ? View.VISIBLE : View.GONE);
    }
}
