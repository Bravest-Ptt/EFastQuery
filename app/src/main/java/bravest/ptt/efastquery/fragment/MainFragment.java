package bravest.ptt.efastquery.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bravest.ptt.efastquery.R;
import bravest.ptt.efastquery.msciat.settings.IatSettings;
import bravest.ptt.efastquery.msciat.utils.JsonParser;
import bravest.ptt.efastquery.utils.PLog;
import bravest.ptt.efastquery.utils.Utils;


/**
 * Created by root on 2/13/17.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener{
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
//    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
//    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    //用ArrayList存储听写结果
    private ArrayList<String> mIatResultsArray = new ArrayList<>();

    //UI
    private TextView mModeView;
    private EditText mResultText;
    private Button mSpeechButton;

    private View mMain;

    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.iat_recognize:
                mResultText.setText("");
                mIatResultsArray.clear();
                break;
            default:
                break;
        }
        return true;
    }

    float rbx;
    float rby;

    float lastX;
    float lastY;

    private boolean mHasInitialized = false;
    private float mDownX;
    private float mDownY;
    private long mDownTimeMillis;
    private static final float DISTANCE = 15f;
    private static final int ANIMATION_DURATION = 1000;
    private ObjectAnimator mTouchXAnimator = null;
    private ObjectAnimator mTouchYAnimator = null;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int[] loc = new int[2];
        mSpeechButton.getLocationOnScreen(loc);
        if (!mHasInitialized) {
            rbx = loc[0];
            rby = loc[1];
            mHasInitialized = true;
        }
        PLog.log("loc0 = " + loc[0] + ",loc1 = " + loc[1]);
        PLog.log("rbx = " + rbx + ",rby = " + rby);

        if (view.getId() != R.id.iat_recognize) {
            return false;
        }
        if ((mTouchXAnimator != null && mTouchXAnimator.isRunning())
                || (mTouchYAnimator != null && mTouchYAnimator.isRunning())) {
            return true;
        }
        float x = motionEvent.getRawX();
        float y = motionEvent.getRawY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = mDownX = x;
                lastY = mDownY = y;
                mDownTimeMillis = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                if (x > mDownX - DISTANCE && x <= mDownX + DISTANCE
                        && y > mDownY - DISTANCE && y <= mDownY + DISTANCE) {
                    if (System.currentTimeMillis() - mDownTimeMillis > 1200) {
                        //long click
                        PLog.log("touch long click");
                    } else {
                        //click
                        PLog.log("touch click");
                    }
                } else {
                    PLog.log(" x = "  + x + ", y = " + y);
                    mTouchXAnimator = ObjectAnimator.ofFloat(mSpeechButton, "translationX", rbx - loc[0]);
                    mTouchXAnimator.setDuration(ANIMATION_DURATION);
                    mTouchXAnimator.setInterpolator(new OvershootInterpolator());

                    mTouchYAnimator = ObjectAnimator.ofFloat(mSpeechButton, "translationY", rby - loc[1]);
                    mTouchYAnimator.setDuration(ANIMATION_DURATION);
                    mTouchYAnimator.setInterpolator(new OvershootInterpolator());

                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(mTouchXAnimator, mTouchYAnimator);
//                    set.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            mSpeechButton.layout((int)rbx, (int)rby, (int)rbx + mSpeechButton.getMeasuredWidth(), (int)rby + mSpeechButton.getMeasuredHeight());
//                        }
//                    });
                    set.start();
                    PLog.log("set started");
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!(x >= mDownX - DISTANCE && x <= mDownX + DISTANCE
                        && y >= mDownY - DISTANCE && y <= mDownY + DISTANCE)) {
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                            ViewGroup.LayoutParams.WRAP_CONTENT,
//                            ViewGroup.LayoutParams.WRAP_CONTENT
//                    );
//                    params.leftMargin = (int) (x - mSpeechButton.getMeasuredWidth() / 2 );
//                    params.topMargin = (int) (y - Utils.getStatusBarHeight(getContext()) - (mSpeechButton.getMeasuredHeight() / 2));
//                    mSpeechButton.setLayoutParams(params);
                    View v = mSpeechButton;
                    float dx = x - lastX;
                    float dy = y - lastY;
//
                    int left = (int) (v.getLeft() + dx);
                    int top = (int) (v.getTop() + dy);
                    int right = (int) (v.getRight() + dx);
                    int bottom = (int) (v.getBottom() + dy);

                    PLog.log("dx = " + dx
                            + " dy = " + dy
                            + "left = " + left
                            + "top = " + top
                            + " right = " + right
                            + " bottom = " + bottom);

                    // 设置不能出界
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    int screenWidth = Utils.getScreenWidth(getContext());
                    int screenHeight = Utils.getScreenHeight(getContext());
                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    PLog.log("left = " + left
                     + "top = " + top
                     + " right = " + right
                     + " bottom = " + bottom);
                    v.layout(left, top, right, bottom);

//                    mSpeechButton.setX(x - mSpeechButton.getMeasuredWidth() / 2);
//                    mSpeechButton.setY(y - Utils.getStatusBarHeight(getContext()) - (mSpeechButton.getMeasuredHeight() / 2));
                    lastX = x;
                    lastY = y;
                    return true;
                }
                break;
        }
        return false;
    }

    private enum STATE {
      STARTED,STOPPED
    }

    private STATE mState = STATE.STOPPED;

    private static String TAG = "MainFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化识别无UI识别对象
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        mIat = SpeechRecognizer.createRecognizer(getContext(), mInitListener);

        // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
        // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
//        mIatDialog = new RecognizerDialog(getContext(), mInitListener);

        mSharedPreferences = getActivity().getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        mToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(inflater == null) {
            return null;
        }
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initLayout(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateModeText(mSharedPreferences.getString("iat_language_preference", "en_us"));
    }

    private void initLayout(View parent) {
        mMain = parent.findViewById(R.id.iat_main);
        mResultText = ((EditText) parent.findViewById(R.id.iat_text));

        mSpeechButton = (Button) parent.findViewById(R.id.iat_recognize);
        mSpeechButton.setOnClickListener(this);
        mSpeechButton.setOnLongClickListener(this);
        mSpeechButton.setOnTouchListener(this);

        mModeView = (TextView) parent.findViewById(R.id.iat_mode_show);
        mModeView.setOnClickListener(this);
        updateModeText(mSharedPreferences.getString("iat_language_preference", "en_us"));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( null != mIat ){
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    int ret = 0; // 函数调用返回值

    @Override
    public void onClick(View view) {
        if( null == mIat ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            return;
        }
        switch (view.getId()) {
            // 进入参数设置页面
            case R.id.iat_mode_show:
                Intent intents = new Intent(getActivity(), IatSettings.class);
                startActivity(intents);
                break;
            // 开始听写 / 停止听写
            // 如何判断一次听写结束：OnResult isLast=true 或者 onError
            case R.id.iat_recognize:
                if (mState == STATE.STOPPED) {
                    updateRecognizeStateAndUI(STATE.STOPPED);
                    //开始听写
//                    mResultText.setText(null);// 清空显示内容
//                    mIatResults.clear();
                    // 设置参数
                    setParam();
                    // 不显示听写对话框
                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("听写失败,错误码：" + ret);
                    } else {
                        showTip(getString(R.string.text_begin));
                    }
                } else if (mState == STATE.STARTED) {
                    //停止听写
                    updateRecognizeStateAndUI(STATE.STARTED);
                    mIat.stopListening();
                    showTip("停止听写");
                }
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
            updateRecognizeStateAndUI(STATE.STOPPED);
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
            updateRecognizeStateAndUI(STATE.STARTED);
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
            updateRecognizeStateAndUI(STATE.STARTED);
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            mSpeechButton.setScaleX(1f + ((float) volume / 150f));
            mSpeechButton.setScaleY(1f + ((float) volume / 150f));
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };


    /**
     * 参数设置
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "en_us");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        mIatResults.put(sn, text);
        mIatResultsArray.add(text);

        StringBuffer resultBuffer = new StringBuffer();
//        for (String key : mIatResults.keySet()) {
//            resultBuffer.append(mIatResults.get(key));
//        }

        int size = mIatResultsArray.size();
        for (int i = 0; i < size; i++) {
            resultBuffer.append(mIatResultsArray.get(i));
        }

        mResultText.setText(resultBuffer.toString());
        mResultText.setSelection(mResultText.length());
    }

    private void updateModeText(String mode) {
        if (mModeView == null) {
            showTip("Mode模块尚未初始化好！");
            return;
        }
        switch (mode) {
            case "mandarin":
                mModeView.setText("听写语言：普通话");
                break;
            case "cantonese":
                mModeView.setText("听写语言：粤语");
                break;
            case "en_us":
                mModeView.setText("听写语言：英语");
                break;
        }
    }

    private void updateRecognizeStateAndUI(STATE state) {
        mState = state;
        switch (state) {
            case STOPPED:
                mState = STATE.STARTED;
                mSpeechButton.setText("停");
                break;
            case STARTED:
                mSpeechButton.setText("听");
                mState = STATE.STOPPED;
                break;
            default:
                break;
        }
        animateRecognizeButton();
    }

    private ObjectAnimator mSpeechButtonAnimator = null;

    private void animateRecognizeButton() {
        if (mSpeechButton == null) {
            Log.d(TAG, "animateRecognizeButton: mSpeechButton not initialized");
            return;
        }
        if (mSpeechButtonAnimator != null) {
            if (mSpeechButtonAnimator.isRunning()) {
                mSpeechButtonAnimator.cancel();
            }
            mSpeechButtonAnimator = null;
        }
        if (mState == STATE.STARTED) {
            mSpeechButton.setAlpha(0.3f);
            mSpeechButtonAnimator = ObjectAnimator.ofFloat(mSpeechButton, "alpha", 0.3f, 0.1f, 0.3f);
            mSpeechButtonAnimator.setDuration(1000);
            mSpeechButtonAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mSpeechButtonAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mSpeechButtonAnimator.start();
        } else {
            mSpeechButton.setAlpha(1f);
            mSpeechButton.setScaleX(1f);
            mSpeechButton.setScaleY(1f);
        }
    }
}
