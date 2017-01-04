package bravest.ptt.efastquery.data;


import android.content.Context;
import android.os.Handler;
import android.os.Message;

import bravest.ptt.efastquery.R;

/**
 * Created by root on 12/27/16.
 */

public class TranslateManager {

    private Context mContext;
    private TranslateEngine mEngine;

    private Request mRequest;
    private long mKey;
    private TranslateListener mListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Result.RESULT_SUCCESS:
                    if (mKey == mRequest.getKey()) {
                        mRequest.setHasResponse(true);
                        if (mListener != null) {
                            mListener.onTranslateSuccess((Result) msg.obj);
                        }
                    }
                    break;
            }
        }
    };

    private Handler mErrorHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Result.LOCALE_IO_EXCEPTION:
                case Result.LOCALE_JSON_EXCEPTION:
                case Result.LOCALE_MALFORMED_URL:
                case Result.LOCALE_UNSUPPORTED_ENCODER:
                case Result.RESULT_INVALID:
                case Result.RESULT_KEY_ERROR:
                case Result.RESULT_LONG:
                case Result.RESULT_NO_RESULT:
                case Result.RESULT_UNSUPPORTED:
                    if (mKey == mRequest.getKey()) {
                        mRequest.setHasResponse(true);
                        if (mListener != null) {
                            mListener.onTranslateFailed(mContext.getString(R.string.translate_error));
                        }
                    }
                    break;
            }
        }
    };

    public TranslateManager(Context context) {
        this.mContext = context;
        mEngine = new TranslateEngine();
    }

    public void translate(String request) {
        try {
            if (mRequest != null && (mRequest.getKey() == mKey) && !mRequest.hasResponse()) {
                return;
            }
            if (mListener != null) {
                mListener.onTranslateStart();
            }
            mRequest = new Request(request, mHandler, mErrorHandler);
            mKey = mRequest.getKey();
            mEngine.setRequest(mRequest).start();
        } catch (TranslateEngine.NotSetRequestException e) {
            e.printStackTrace();
        }
    }

    public void setTranslateListener(TranslateListener listener) {
        mListener = listener;
    }
}
