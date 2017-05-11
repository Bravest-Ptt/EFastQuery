package bravest.ptt.androidlib.net.bmob;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import bravest.ptt.androidlib.net.OkHttpRequest;
import bravest.ptt.androidlib.net.RequestCallback;
import bravest.ptt.androidlib.net.RequestParameter;
import bravest.ptt.androidlib.net.URLData;
import bravest.ptt.androidlib.utils.bmob.BmobConstants;
import bravest.ptt.androidlib.utils.JNIUtils;
import bravest.ptt.androidlib.utils.PreferencesUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 5/11/17.
 */

public class BmobHttpRequest extends OkHttpRequest {

    private static final String TAG = "BmobHttpRequest";

    private Context mContext;

    public BmobHttpRequest(Context context, URLData data, List<RequestParameter> params, RequestCallback callBack) {
        super(context, data, params, callBack);
        this.mContext = context;
    }

    @Override
    protected String getNewUrl(String url, List<RequestParameter> parameter) {
        return null;
    }

    @Override
    protected void setHttpHeaders(OkHttpClient.Builder okBuilder, final URLData data) {
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder();

                if (data.getContentType() != null) {
                    Log.d(TAG, "intercept: type = " + data.getContentType());
                    requestBuilder.header(BmobConstants.X_BMOB_CONTENT_TYPE, data.getContentType());
                }

                String token = PreferencesUtils.getString(mContext, BmobConstants.BMOB_SESSION_KEY);
                if (!TextUtils.isEmpty(token)) {
                    Log.d(TAG, "intercept: token = " + token);
                    requestBuilder.header(BmobConstants.X_BMOB_SESSION_TOKEN, token);
                }

                requestBuilder.header(BmobConstants.X_BMOB_APP_ID, JNIUtils.getApplicationId())
                        .header(BmobConstants.X_BMOB_REST_API_KEY, JNIUtils.getRestAPIKey())
                        .method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
        okBuilder.addInterceptor(headerInterceptor);
    }
}
