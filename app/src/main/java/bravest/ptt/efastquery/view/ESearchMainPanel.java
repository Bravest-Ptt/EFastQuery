package bravest.ptt.efastquery.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.data.TranslateListener;
import bravest.ptt.efastquery.data.TranslateManager;
import bravest.ptt.efastquery.model.HistoryModule;
import bravest.ptt.efastquery.provider.HistoryManager;
import bravest.ptt.efastquery.utils.Utils;
import bravest.ptt.efastquery.view.ESearchFloatButton.*;
import bravest.ptt.efastquery.view.Loader.Loader;

/**
 * Created by root on 1/4/17.
 */

class ESearchMainPanel implements View.OnClickListener, TranslateListener, FloatPanelVisibleListener,
        TextToSpeech.OnInitListener, TextWatcher ,View.OnKeyListener{

    private static final int WHAT_SEARCHING = 0x011;
    private static final int WHAT_DONE = 0x012;
    private static final int STATE_INPUT = 0x033;
    private static final int STATE_TRANS_SUCCESS = 0x044;

    private Context mContext;
    private WindowManager mWm;
    private ESearchFloatButton mButton;
    private TranslateManager mTm;
    private TextToSpeech mTTS;
    private HistoryManager mHm;
    private FABManager mFm;
    private FloatPanelVisibleListener mButtonVisibleListener;

    private int mState = STATE_INPUT;

    private View mMain;
    private View mMainPanel;
    private ImageButton mMainSearch;
    private EditText mMainInput;
    private View mMainShowResultPanel;
    private EditText mMainShowResultText;
    private RecyclerView mMainShowHistory;

    private FloatingActionButton mFabSpeak;
    private FloatingActionButton mFabFavorite;

    private WindowManager.LayoutParams mLayoutParams;

    private Result mLastResult;
    private ArrayList<HistoryModule> mHistoryArray;

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
        mTTS = new TextToSpeech(mContext, this);
        mHm = new HistoryManager(mContext);
        mFm = new FABManager(mContext);
        mHistoryArray = new ArrayList<>();

        initHistoryData();
        initViews();
        initLayoutParams();
    }

    private void initHistoryData() {
        Loader.init(mHm).progress(null);
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
        mMain.setFocusable(true);
        mMain.setFocusableInTouchMode(true);

        mMain.setOnKeyListener(this);

        //Panel
        RelativeLayout.LayoutParams mainParams = (RelativeLayout.LayoutParams) mMainPanel.getLayoutParams();
        mainParams.width = Utils.getScreenWidth(mContext) * 8 / 12;
        mainParams.height = Utils.getScreenHeight(mContext) * 6 / 12;
        mMainPanel.setLayoutParams(mainParams);

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
        mMainInput.addTextChangedListener(this);
        mMainInput.setOnKeyListener(this);

        //init functions
        mFabSpeak = (FloatingActionButton) mMain.findViewById(R.id.main_panel_speak);
        mFabFavorite = (FloatingActionButton) mMain.findViewById(R.id.main_panel_favorite);
        mFabSpeak.setOnClickListener(this);
        mFabFavorite.setOnClickListener(this);

        mFm.addFAB(mFabSpeak);
        mFm.addFAB(mFabFavorite);

        initShowPanel();
    }

    private void initShowPanel() {
        mMainShowResultPanel = mMain.findViewById(R.id.main_panel_show_result);
        mMainShowResultText = (EditText) mMain.findViewById(R.id.main_panel_show_result_text);
        mMainShowHistory = (RecyclerView) mMain.findViewById(R.id.main_panel_show_history);

        //init
        mMainShowResultText.setLineSpacing(0, 1.1f);

        //Init history
        //slide to left show 'delete' from history database
        //slide to right show 'favorite' status & 'voice'
        //click show details, dismiss history
        //long click show checkbox && show fab in top (favorite, delete, select all, deselect all)
//        mMainShowHistory

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
            case R.id.main_panel_speak:
                if (mLastResult != null) {
                    if (TextUtils.equals(mMainInput.getText(), mLastResult.query)) {
                        startTTS(mLastResult.query);
                    }
                }
                Log.d(TAG, "onClick: speak");
                break;
            case R.id.main_panel_favorite:
                Log.d(TAG, "onClick: favorite");
                break;
        }
    }

    private static final String TAG = "ptt";
    private String mRequest;

    private void doSearch() {
        if (mTm != null && !TextUtils.isEmpty(mMainInput.getText())) {
            mRequest = mMainInput.getText().toString();
            Log.d(TAG, "doSearch: input = " + mRequest);
//            if (!input.matches("^[a-zA-Z0-9]+")) {
//                Log.d("ptt", "doSearch: return");
//                return;
//            }
            Utils.hideSoftInput(mContext, mMainInput);
            mTm.translate(mRequest);
        }
    }

    @Override
    public void onTranslateStart() {
        mHandler.sendEmptyMessage(WHAT_SEARCHING);
    }

    @Override
    public void onTranslateSuccess(Result result) {
        Log.d("ptt", "onTranslateSuccess: ");
        toggleVisible(true);
        mState = STATE_TRANS_SUCCESS;
        mLastResult = result;
        mFm.popUpFAB(FABManager.ACTION_TRANS_SUCCESS);
        mMainShowResultText.setText(result.getResultWithQuery());
        if (!mHm.isRequestExist(mRequest) && !TextUtils.isEmpty(result.explains.toString())) {
            mHm.insertHistory(result);
        }
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

    public void setViewVisibleListener(FloatPanelVisibleListener listener) {
        mButtonVisibleListener = listener;
    }

    private void toggleVisible(boolean translating) {
        mMainShowHistory.setVisibility(translating ? View.GONE : View.VISIBLE);
        mMainShowResultPanel.setVisibility(translating ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTTS.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(mContext, "Data missing or unsupported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTTS(String result) {
        if (mTTS != null && !mTTS.isSpeaking()) {
            mTTS.setPitch(0.5f);
            if (TextUtils.isEmpty(result)) {
                result = "No result";
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mTTS.speak(result, TextToSpeech.QUEUE_FLUSH, null, null);
            } else {
                mTTS.speak(result, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "beforeTextChanged: ");

        if (mState == STATE_TRANS_SUCCESS) {
            mFm.pullDownFAB(FABManager.ACTION_INPUT_NULL);
        }
        mState = STATE_INPUT;
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        Log.d(TAG, "onTextChanged: ");
    }

    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TAG, "afterTextChanged: ");
        if (editable.length() == 0) {
            //show all history
            toggleVisible(false);

            //reset result
            mLastResult = null;
        } else {
            //show query in history, if no,

            //
        }
    }

    public void destroy() {
        mHistoryArray.clear();
        mTTS.stop();
        mTTS.shutdown();
        if (mIsShowing) {
            mWm.removeView(mMain);
        }
        mHm = null;
        mTm = null;
        mFm = null;
        mWm = null;
        mTTS = null;
        mButtonVisibleListener = null;
        mFabSpeak = null;
        mFabFavorite = null;
        mMainInput = null;
        mMain = null;
        mMainSearch = null;
        mMainShowResultPanel = null;
        mMainShowResultText = null;
        mMainShowHistory = null;
        mMainPanel = null;
        mButton = null;
        mLayoutParams = null;
        mHandler = null;
        mHistoryArray = null;
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            hideSearchPanel();
        }
        return false;
    }

    private class HistoryLoader extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }
}
