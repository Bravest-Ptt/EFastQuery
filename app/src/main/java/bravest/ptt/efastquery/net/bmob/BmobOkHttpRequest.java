package bravest.ptt.efastquery.net.bmob;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import bravest.ptt.androidlib.net.AbstractOkHttpRequest;
import bravest.ptt.androidlib.net.RequestCallback;
import bravest.ptt.androidlib.net.RequestParam;
import bravest.ptt.androidlib.net.URLData;
import bravest.ptt.androidlib.utils.bmob.BmobConstants;
import bravest.ptt.androidlib.utils.JNIUtils;
import bravest.ptt.androidlib.utils.plog.PLog;
import bravest.ptt.efastquery.entity.User;
import bravest.ptt.efastquery.utils.API;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by root on 5/11/17.
 */

public class BmobOkHttpRequest extends AbstractOkHttpRequest {

    private static final String TAG = "BmobOkHttpRequest";

    private Context mContext;

    public BmobOkHttpRequest(Context context, URLData data, RequestParam param, RequestCallback callBack) {
        super(context, data, param, callBack);
        this.mContext = context;
    }

    /**
     * TODO refer bmob api document
     * 此种拼接方式为 1 没有参数时 xxx.api  2有参数时 xxx.api?k1=v1&k2=v2
     *
     * @param url
     * @param param
     * @return
     */
    protected String getNewUrl(String url, URLData data, RequestParam param) {
        try {
            if (param == null) {
                return url;
            }

            // 添加参数
            final StringBuffer paramBuffer = new StringBuffer();
            paramBuffer.append(url);

            String method = data.getNetType();

            if (param.hasId()) {
                paramBuffer.append("/"+param.getObjectId());
            }

            if (param.hasBody()) {
                switch (method) {
                    case REQUEST_GET:
                        //for login
                        if (data.getKey().equals(API.LOGIN)) {
//                            paramBuffer.append("?"
//                                    + URLEncoder.encode(param.getBody(), "utf-8"));
                            paramBuffer.append("?" + param.getBody());
                        } else {
                            //for query
                            paramBuffer.append("?where="
                                    + URLEncoder.encode(param.getBody(), "utf-8"));
                        }
                        break;
                    case REQUEST_POST:
                        break;
                    case REQUEST_PUT:
                        break;
                    case REQUEST_DELETE:
                        break;
                    default:
                        break;
                }
            }

            url = paramBuffer.toString();
            PLog.log("new url = " + url);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
        return url;
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

                User user = User.getInstance(mContext);
                if (user != null) {
                    String token = user.getSessionToken();
                    if (!TextUtils.isEmpty(token)) {
                        Log.d(TAG, "intercept: token = " + token);
                        requestBuilder.header(BmobConstants.X_BMOB_SESSION_TOKEN, token);
                    }
                }

                Log.d(TAG, "intercept: method = " + originalRequest.method() + ", body = " + originalRequest.body());
                requestBuilder.header(BmobConstants.X_BMOB_APP_ID, JNIUtils.getApplicationId())
                        .header(BmobConstants.X_BMOB_REST_API_KEY, JNIUtils.getRestAPIKey())
                        .method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                Log.d(TAG, "intercept: JNIUtils.getApplicationId()c = " + JNIUtils.getApplicationId() + ", X_BMOB_REST_API_KEY = " + JNIUtils.getRestAPIKey());
                return chain.proceed(request);
            }
        };
        okBuilder.addInterceptor(headerInterceptor);
    }
}
