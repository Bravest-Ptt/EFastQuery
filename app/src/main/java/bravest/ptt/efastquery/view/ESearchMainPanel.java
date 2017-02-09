package bravest.ptt.efastquery.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import java.util.ArrayList;
import java.util.Locale;

import bravest.ptt.efastquery.MainService;
import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.data.Result;
import bravest.ptt.efastquery.data.TranslateListener;
import bravest.ptt.efastquery.data.TranslateManager;
import bravest.ptt.efastquery.model.HistoryModule;
import bravest.ptt.efastquery.provider.HistoryManager;
import bravest.ptt.efastquery.utils.Utils;
import bravest.ptt.efastquery.view.ESearchFloatButton.*;
import bravest.ptt.efastquery.view.Loader.Loader;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by root on 1/4/17.
 */

class ESearchMainPanel implements View.OnClickListener, TranslateListener, FloatPanelVisibleListener,
        TextToSpeech.OnInitListener, TextWatcher, View.OnKeyListener, View.OnFocusChangeListener {

    private static final String TAG = "ptt";

    private static final int WHAT_SEARCHING = 0x011;
    private static final int WHAT_DONE = 0x012;
    private static final int STATE_INPUT = 0x033;
    private static final int STATE_TRANS_SUCCESS = 0x044;
    private static final int STATE_TRANS_FAILED = 0x055;
    private static final int STATE_TRANSLATING = 0x066;

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
    private ImageButton mMainClean;
    private EditText mMainInput;
    private View mMainShowResultPanel;
    private EditText mMainShowResultText;
    private RecyclerView mMainShowHistory;
    private ProgressBar mProgressBar;

    private FloatingActionButton mFabSpeak;
    private FloatingActionButton mFabFavorite;

    private WindowManager.LayoutParams mLayoutParams;

    private Result mLastResult;
    private ArrayList<HistoryModule> mHistoryArray;
    private HistoryAdapter mHistoryAdapter;

    private String mRequest;
    private boolean mIsShowing = false;

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

        initViews();
        initLayoutParams();
        initHistoryData();
    }

    private void initHistoryData() {
        //Loader.init(mHm, mHistoryArray).progress(mProgressBar).execute();
        mHistoryArray = mHm.getAllHistory();
        mHistoryAdapter = new HistoryAdapter(mContext, mHistoryArray);

        mMainShowHistory.setLayoutManager(new LinearLayoutManager(mContext));
        mMainShowHistory.setAdapter(mHistoryAdapter);
    }

    private void initViews() throws InflaterNotReadyException {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (inflater == null) {
            throw new InflaterNotReadyException();
        }
        mMain = inflater.inflate(R.layout.layout_search_main_panel, null);
        mMainPanel = mMain.findViewById(R.id.main_panel);
        mMainSearch = (ImageButton) mMain.findViewById(R.id.main_panel_search_button);
        mMainClean = (ImageButton) mMain.findViewById(R.id.main_panel_search_clean);
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

        //Clean button
        mMainClean.setOnClickListener(this);

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
        mMainInput.setOnFocusChangeListener(this);
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
        mProgressBar = (ProgressBar) mMain.findViewById(R.id.google_progressbar);

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
            case R.id.main_panel_search_clean:
                mMainInput.setText("");
                break;
        }
    }

    private void doSearch() {
        String input = mMainInput.getText().toString();
        if (mTm != null && !TextUtils.isEmpty(input)) {
            if (TextUtils.equals(mRequest, input)) {
                return;
            }
            mRequest = mMainInput.getText().toString();

            //Translate
            mTm.translate(mRequest);

            //Hide soft input keyboard
            Utils.hideSoftInput(mContext, mMainInput);

            //Clear show text view
            mMainShowResultText.setText("");

            //Dismiss history, show result & progressbar
            toggleVisible(true);
        }
    }

    @Override
    public void onTranslateStart() {
        mState = STATE_TRANSLATING;
        mHandler.sendEmptyMessage(WHAT_SEARCHING);
    }

    @Override
    public void onTranslateSuccess(Result result) {
        Log.d("ptt", "onTranslateSuccess: ");
        mState = STATE_TRANS_SUCCESS;
        if (result == null) {
            return;
        }
        mLastResult = result;
        mProgressBar.setVisibility(View.GONE);

        mFm.popUpFAB(FABManager.ACTION_TRANS_SUCCESS);
        mMainShowResultText.setText(result.getResultWithQuery());

        if (!mHm.isRequestExist(mRequest) && result.explains != null) {
            //Add history in database.
            long insertTime = mHm.insertHistory(result);

            //Add history in memory
            HistoryModule module = new HistoryModule();
            module.request = result.query;
            module.date = insertTime + "";
            module.result = result.getResult();
            mHistoryArray.add(0, module);
            mHistoryAdapter.notifyItemInserted(0);
        }
    }

    @Override
    public void onTranslateFailed(String error) {
        Log.d("ptt", "onTranslateFailed: " + error);
        mState = STATE_TRANS_FAILED;
        mLastResult = null;
        mProgressBar.setVisibility(View.GONE);

        mMainShowResultText.setText(error);
    }

    public void showSearchPanel() {
        if (!mIsShowing) {
            mIsShowing = true;
            mWm.addView(mMain, mLayoutParams);
            if (mButtonVisibleListener != null) {
                mButtonVisibleListener.onShow();
            }
            if (mState != STATE_TRANSLATING) {
                Utils.popSoftInput(mContext,mMainInput);
            }
        }
    }

    public void hideSearchPanel() {
        if (mIsShowing) {
            mIsShowing = false;
            mWm.removeView(mMain);
            if (mButtonVisibleListener != null) {
                mButtonVisibleListener.onHide();
            }
            Utils.hideSoftInput(mContext, mMainInput);
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
        if (translating) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }


    //Voice init
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

    //Voice speak
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

    //Input TextWatcher
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (mState == STATE_TRANS_SUCCESS) {
            mFm.pullDownFAB(FABManager.ACTION_INPUT_NULL);
        }
        mState = STATE_INPUT;
    }

    //Input TextWatcher
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    //Input TextWatcher
    @Override
    public void afterTextChanged(Editable editable) {
        Log.d(TAG, "afterTextChanged: ");
        if (editable.length() == 0) {
            //show all history
            toggleVisible(false);

            //hide clean button
            mMainClean.setVisibility(View.GONE);

            //reset result
            mLastResult = null;
        } else {
            //show query in history, if no,

            //show clean button
            mMainClean.setVisibility(View.VISIBLE);
        }
    }

    //Destroy all object
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
                && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)) {
            hideSearchPanel();
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (view.getId() == R.id.main_panel_search_edit) {
            if (!b) {
                Utils.hideSoftInput(mContext, mMainInput);
            }
        }
    }
}
