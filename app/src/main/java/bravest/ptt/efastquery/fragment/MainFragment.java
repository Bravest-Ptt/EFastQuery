package bravest.ptt.efastquery.fragment;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * Created by root on 2/13/17.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener{
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
    private View mClearEditTextButton;

    private Toast mToast;
    private SharedPreferences mSharedPreferences;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

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
    public void onResume() {
        super.onResume();
        updateModeText(mSharedPreferences.getString("iat_language_preference", "en_us"));
    }

    private void initLayout(View parent) {
        mResultText = ((EditText) parent.findViewById(R.id.iat_text));

        mSpeechButton = (Button) parent.findViewById(R.id.iat_recognize);
        mSpeechButton.setOnClickListener(this);

        mClearEditTextButton = parent.findViewById(R.id.iat_clear);
        mClearEditTextButton.setOnClickListener(this);

        mModeView = (TextView) parent.findViewById(R.id.iat_mode_show);
        updateModeText(mSharedPreferences.getString("iat_language_preference", "en_us"));

        parent.findViewById(R.id.iat_set).setOnClickListener(this);
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
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
            return;
        }
        switch (view.getId()) {
            // 进入参数设置页面
            case R.id.iat_set:
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
            case R.id.iat_clear:
                if (mState == STATE.STOPPED) {
                    mResultText.setText(null);// 清空显示内容
                    mIatResultsArray.clear();
                } else {
                    showTip("请先停止录音");
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
                mSpeechButton.setText("结束 听写");
                break;
            case STARTED:
                mSpeechButton.setText("开始 听写");
                mState = STATE.STOPPED;
                break;
            default:
                break;
        }
        animateRecognizeButton();
        animateCleanButton();
    }

    private ObjectAnimator mSpeechButtonAnimator = null;
    private ObjectAnimator mClearButtonAnimator = null;

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
            mSpeechButton.setAlpha(1f);
        }
        if (mState == STATE.STARTED) {
            mSpeechButtonAnimator = ObjectAnimator.ofFloat(mSpeechButton, "alpha", 1.2f, 0.1f, 1.3f);
            mSpeechButtonAnimator.setDuration(3000);
            mSpeechButtonAnimator.setInterpolator(new LinearInterpolator());
            mSpeechButtonAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mSpeechButtonAnimator.start();
        }
    }

    private void animateCleanButton() {
        if (mClearEditTextButton == null) {
            Log.d(TAG, "changeCleanButtonStatus: mClearEditTextButton not initialized");
            return;
        }
        if (mClearButtonAnimator != null) {
            if (mClearButtonAnimator.isRunning()) {
                mClearButtonAnimator.cancel();
            }
            mClearButtonAnimator = null;
            mClearEditTextButton.setScaleX(1f);
            mClearEditTextButton.setScaleY(1f);
        }
        if (mState == STATE.STARTED) {
            mClearButtonAnimator = ObjectAnimator.ofFloat(mClearEditTextButton, "scaleX", 1f, 0f);
            mClearButtonAnimator.setDuration(500);
            mClearButtonAnimator.setInterpolator(new AccelerateInterpolator());
            mClearButtonAnimator.start();
        }
    }
}
