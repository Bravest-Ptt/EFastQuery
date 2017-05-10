package bravest.ptt.androidlib.net;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;


import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bravest.ptt.androidlib.cache.CacheManager;
import bravest.ptt.androidlib.utils.BaseUtils;
import bravest.ptt.androidlib.utils.FrameConstants;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jieke on 2016/11/24.
 */

public class OkHttpRequest implements Runnable {

    private final static String cookiePath = "/data/data/bravest.ptt.efastquery/cache/cookie";
    // 区分get还是post的枚举
    public static final String REQUEST_GET = "get";
    public static final String REQUEST_POST = "post";
    private Activity activity;
    private URLData urlData = null;
    private RequestCallback requestCallback = null;
    private List<RequestParameter> parameter = null;
    private String url = null; // 原始url
    private String newUrl = null; // 拼接key-value后的url

    // 切换回UI线程
    protected Handler handler;

    protected boolean cacheRequestData = true;

    // 头信息
    HashMap<String, String> headers;

    private Request request = null;

    private Call call;

    //创建okHttpClient对象
    private OkHttpClient mOkHttpClient;
    private OkHttpClient.Builder okBuilder;

    public OkHttpRequest(Activity activity, final URLData data, final List<RequestParameter> params, final RequestCallback callBack) {
        this.activity = activity;
        this.urlData = data;
        url = urlData.getUrl();
        this.parameter = params;

        this.requestCallback = callBack;

        okBuilder = new OkHttpClient.Builder();

        handler = new Handler();

        headers = new HashMap<>();
    }

    
    public Call getCall() {
        return call;
    }


    @Override
    public void run() {

        try {
            if (urlData.getNetType().equals(REQUEST_GET)) {  //get请求时的处理

                newUrl = getNewUrl(url, parameter);
                Log.i("newUrl", newUrl);

                // 如果这个get的API有缓存时间（大于0）直接返回缓存的数据
                if (urlData.getExpires() > 0) {
                    final String content = CacheManager.getInstance().getFileCache(newUrl);
                    if (content != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestCallback.onSuccess(content + "  Expires");
                            }

                        });

                        return;
                    }
                }
                //建立一个请求
                request = new Request.Builder().url(newUrl).build();


            } else if (urlData.getNetType().equals(REQUEST_POST)) { //post 请求时的处理

                FormBody body = getFormBody(parameter);
                request = new Request.Builder().url(url).post(body).build();

            } else { //其他处理
                return;
            }

            okBuilder.connectTimeout(4000, TimeUnit.MILLISECONDS)
                    .readTimeout(4000, TimeUnit.MILLISECONDS)
                    .writeTimeout(4000, TimeUnit.MILLISECONDS);

            addCookie();// 添加Cookie到请求头中

            setHttpHeaders(okBuilder); //添加必要的头部信息

            mOkHttpClient = okBuilder.build();


            //发送请求
            call = mOkHttpClient.newCall(request);
            //结果回调
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    if (requestCallback != null) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                requestCallback.onFail(e.getMessage());
                            }
                        });
                    } else {
                        // TODO: 2016/11/24  处理接口为空的情况
                    }

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (requestCallback != null) {
                        int code = response.code();
                        if (code >= 200 && code < 300) {  //代表成功

//                            updateDeltaBetweenServerAndClientTime(response);  // 更新服务器时间和本地时间的差值

                            //okHttp3 如果在头部中 添加了 gzip字段 会自动进行 gzip 解压 这里不在处理
                            String string = response.body().string();
                            Log.i("DataResponse", string);

                            final DataResponse dataResponse = JSON.parseObject(string, DataResponse.class);

                            if (dataResponse.hasError()) {  // 包含错误

                                if (dataResponse.getErrorType() == 1) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestCallback.onCookieExpired();
                                        }
                                    });
                                } else {
                                    handleNetworkError(dataResponse.getErrorMessage());
                                }

                            } else {
                                //当是get请求 并且缓存时间>0时 保存到缓存
                                if (urlData.getNetType().equals(REQUEST_GET) && urlData.getExpires() > 0) {
                                    CacheManager.getInstance().putFileCache(newUrl, dataResponse.getResult(), urlData.getExpires());
                                }

                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        requestCallback.onSuccess(dataResponse.getResult() + "  success");
                                    }
                                });
                            }
                        } else {
                            handleNetworkError("网络异常2");
                        }

                    } else {
                        // TODO: 2016/11/24  处理接口为空的情况

                    }
                }
            });
        } catch (Exception e) {
            handleNetworkError("网络异常1");
        }

    }

    /**
     * FromBody 继承自 RequestBody  默认表单提交  默认编码格式为 utf—8
     *
     * @param parameter
     * @return
     */
    private FormBody getFormBody(List<RequestParameter> parameter) {

        FormBody.Builder builder = new FormBody.Builder();

        if (parameter != null & parameter.size() > 0) {
            for (RequestParameter requestParameter : parameter) {
                builder.add(requestParameter.getName(), requestParameter.getValue());
            }
        }
        return builder.build();
    }


    /**
     * 此种拼接方式为 1 没有参数时 xxx.api  2有参数时 xxx.api?k1=v1&k2=v2
     *
     * @param url
     * @param parameter
     * @return
     */
    private String getNewUrl(String url, List<RequestParameter> parameter) {

        // 添加参数
        final StringBuffer paramBuffer = new StringBuffer();

        if ((parameter != null) && (parameter.size() > 0)) {

            sortKeys();// 这里要对key进行排序

            for (final RequestParameter p : parameter) {
                if (paramBuffer.length() == 0) {
                    paramBuffer.append(p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
                } else {
                    paramBuffer.append("&" + p.getName() + "=" + BaseUtils.UrlEncodeUnicode(p.getValue()));
                }
            }

            return url + "?" + paramBuffer.toString();
        } else {
            return url;
        }
    }

    public void handleNetworkError(final String errorMsg) {
        if (requestCallback != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    requestCallback.onFail(errorMsg);
                }
            });
        }
    }


    /**
     * 对key进行排序
     * 我们可以将xxx.api?k1=v1&k2=v2 这样的url格式作为标记  放入 app 缓存
     * 需要注意的是 我们要对 k1 k2 这些key进行排序 这样这些标记才能唯一
     * xxxx.api?k1=v1&k2=v2  xxxx.api?k2=v2&k1=v1 其实是一个请求
     */
    public void sortKeys() {
        for (int i = 1; i < parameter.size(); i++) {
            for (int j = i; j > 0; j--) {
                RequestParameter p1 = parameter.get(j - 1);
                RequestParameter p2 = parameter.get(j);
                if (compare(p1.getName(), p2.getName())) {
                    // 交互p1和p2这两个对象，写的超级恶心
                    String name = p1.getName();
                    String value = p1.getValue();

                    p1.setName(p2.getName());
                    p1.setValue(p2.getValue());

                    p2.setName(name);
                    p2.setValue(value);
                }
            }
        }
    }

    // 返回true说明str1大，返回false说明str2大
    public boolean compare(String str1, String str2) {
        String uppStr1 = str1.toUpperCase();
        String uppStr2 = str2.toUpperCase();

        boolean str1IsLonger = true;
        int minLen = 0;

        if (str1.length() < str2.length()) {
            minLen = str1.length();
            str1IsLonger = false;
        } else {
            minLen = str2.length();
            str1IsLonger = true;
        }

        for (int index = 0; index < minLen; index++) {
            char ch1 = uppStr1.charAt(index);
            char ch2 = uppStr2.charAt(index);
            if (ch1 != ch2) {
                if (ch1 > ch2) {
                    return true; // str1大
                } else {
                    return false; // str2大
                }
            }
        }

        return str1IsLonger;
    }


    /**
     * 从本地获取cookie列表
     *
     * @return
     */
    public void addCookie() {
        okBuilder.cookieJar(new CookiesManager());
    }

    /**
     * 添加必要的头信息
     */
    public void setHttpHeaders(OkHttpClient.Builder okBuilder) {

        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header(FrameConstants.ACCEPT_CHARSET, "UTF-8,*")
                        .header(FrameConstants.USER_AGENT, "Young Heart Android App ")
                        .header(FrameConstants.ACCEPT_ENCODING, "gzip")
                        .method(originalRequest.method(), originalRequest.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

        okBuilder.addInterceptor(headerInterceptor);

    }

    /**
     * 自动管理Cookies
     */
    private class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(activity);

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }


}
