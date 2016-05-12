/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 jc0mm0n
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.waynezhang.mcommon.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonSyntaxException;
//import com.shandagames.greport.GReport;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.InternalCache;
import com.waynezhang.mcommon.util.BitmapUtil;
import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.util.ThreadUtil;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by jc0mm0n on 10/22/14.
 */
public class Http {
    private Http() {
    }

    private static final String TAG = "Http";
    private static OkHttpClient client = new OkHttpClient();
    private static HttpPolicyHandlerImp httpCachePolicyHandler;
    private static InternalCache okHttpCache;
    private static CookieManager cookieManager;
    private static Context mContext;
    private static Init mInit;

    private static Handler handler = new Handler(Looper.getMainLooper());

    static {
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        client.setCookieHandler(cookieManager);
    }

    public static void removeAllCookies() {
        cookieManager.getCookieStore().removeAll();
    }

    //初始化自定义策略
    public static void initCachePolicyHandler(HttpPolicyHandlerImp httpPolicyHandler) throws IOException {
        initCachePolicyHandler(null, httpPolicyHandler, null);
    }

    public static void initCachePolicyHandler(Context context, HttpPolicyHandlerImp httpPolicyHandler, Init init) throws IOException {
        mContext = context;
        mInit = init;
        if (httpPolicyHandler != null) {
            httpCachePolicyHandler = httpPolicyHandler;
            client.setReadTimeout(httpCachePolicyHandler.getSocketTimeout(), TimeUnit.MILLISECONDS);
            client.setConnectTimeout(httpCachePolicyHandler.getConnectionTimeout(), TimeUnit.MILLISECONDS);
        }
    }

    public static void cancel(String url) {
        client.cancel(url);
        httpCachePolicyHandler.cancelTimeoutTask(url);
    }

    public static void cancel(Object tag) {
        client.cancel(tag.hashCode());
        httpCachePolicyHandler.cancelTimeoutTask(tag.hashCode());
    }

    public static String get(final String url, final StringCallback callback) {
        //cancel tag with same url
//        cancel(url);  // Deleted by waynezhang on 5/9/16: 删除取消相同url的request的代码, 转由业务层控制
        UUID tag = UUID.randomUUID();
        L.d(TAG, "Do GET --> " + url);
        final Request request = new Request.Builder().url(url).tag(url).build();

        //检查请求url策略匹配,添加至策略任务
        final String urlKey = url;
        final long startTime = System.currentTimeMillis();
        if (httpCachePolicyHandler == null ? true : httpCachePolicyHandler.preReqPolicyHandle(url, urlKey, "", callback)) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    handleErrorWithCache(urlKey, handler, request, e, callback, startTime);
                }

                @Override
                public void onResponse(Response response) {
                    try {
                        final String responseString = response.body().string();
                        L.v(TAG, "[" + url + "] response:" + responseString);
                        if (null != mInit && httpCachePolicyHandler != null) {
                            int responseCode = mInit.getResponseCode(responseString);
                            httpCachePolicyHandler.postReqPolicyHandle(url, urlKey, "", responseString, callback, responseCode == 0);
                            reportTimeCost(url, responseCode, startTime, response.code(), 0);
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResponse(responseString);
                                }
                            });
                        }
                    } catch (final Throwable e) {
                        handleErrorWithCache(urlKey, handler, request, e, callback, startTime, response.code());
                    }
                }
            });
        }

        return tag.toString();
    }

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // 上传Json字符串使用
    public static void post(final Object httpTag, final String url, final String json, final StringCallback callback) {
        L.d(TAG, "Do Post --> " + url + " --> " + json);
        RequestBody requestBody = RequestBody.create(JSON, json);
        final Request request = new Request.Builder().url(url).tag(httpTag.hashCode()).post(requestBody).build();

        //检查请求url策略匹配,添加至策略任务
        final String urlKey = url;
        final String params = json;
        final long startTime = System.currentTimeMillis();
        if (httpCachePolicyHandler == null ? true : httpCachePolicyHandler.preReqPolicyHandle(httpTag.hashCode(), urlKey, params, callback)) {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    handleErrorWithCache(urlKey, handler, request, e, callback, startTime);
                }

                @Override
                public void onResponse(Response response) {
                    try {
                        final String responseString = response.body().string();
                        L.v(TAG, "[" + url + "] response:" + responseString);
                        if (null != mInit && httpCachePolicyHandler != null) {
                            int responseCode = mInit.getResponseCode(responseString);
                            httpCachePolicyHandler.postReqPolicyHandle(httpTag.hashCode(), urlKey, params, responseString, callback, responseCode == 0);
                            reportTimeCost(url, responseCode, startTime, response.code(), 0);
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onResponse(responseString);
                                }
                            });
                        }
                    } catch (final Throwable e) {
                        handleErrorWithCache(urlKey, handler, request, e, callback, startTime, response.code());
                    }
                }
            });
        }

    }

    private static final MediaType MEDIA_TYPE = MediaType.parse("image/jpeg");

    // 上传图片使用
    public static String post(final String url, final Bitmap attachment, final StringCallback callback) {
        return post(url, null, attachment, callback);
    }

    // 上传图片使用
    public static String post(final String url, final Map<String, String> params, final Bitmap attachment, final StringCallback callback) {
        return post(url, params, BitmapUtil.getJpegBytes(attachment), callback);
    }

    // 上传图片使用
    public static String post(final String url, final Map<String, String> params, final byte[] bytes, final StringCallback callback) {
        L.d(TAG, "Do Post --> " + url);
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        if (params != null) {
            Set<Map.Entry<String, String>> entrySet = params.entrySet();
            for (Iterator<Map.Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> it = iterator.next();
                builder.addFormDataPart(it.getKey(), it.getValue());
            }
        }
        builder = builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"a.jpg\""),
                RequestBody.create(MEDIA_TYPE, bytes));
        RequestBody requestBody = builder.build();
        final Request request = new Request.Builder().url(url).tag(url).post(requestBody).build();

        final long startTime = System.currentTimeMillis();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                handleError(url, handler, request, e, callback, startTime);
            }

            @Override
            public void onResponse(Response response) {
                try {
                    final String responseString = response.body().string();
                    L.v(TAG, "[" + url + "] response:" + responseString);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(responseString);
                        }
                    });
                    reportTimeCost(url, mInit != null ? mInit.getResponseCode(responseString) : 0, startTime, response.code(), 0);
                } catch (final Throwable e) {
                    handleError(url, handler, request, e, callback, startTime, response.code());
                }
            }
        });

        return url;
    }

    private static void handleErrorWithCache(final String url, final Handler handler, final Request request, final Throwable th, final OnErrorCallback callback, final long startTime) {
        handleErrorWithCache(url, handler, request, th, callback, startTime, 0);
    }

    private static void handleErrorWithCache(final String url, final Handler handler, final Request request, final Throwable th, final OnErrorCallback callback, final long startTime, int httpResponseCode) {
        final ResultCode resultCode = getErrorCode(th);
        reportTimeCost(url, 0, startTime, httpResponseCode, resultCode.nativeInt);
        //检查是否缓存命中过, 如果没有命中过, 则返回onFailure; 如果命中过, 则由缓存策略处理返回
        if (httpCachePolicyHandler != null && !httpCachePolicyHandler.checkCacheHit(url)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(request, resultCode, (Exception) th);
                }
            });
        }
    }

    private static void handleError(final String url, Handler handler, final Request request, final Throwable th, final OnErrorCallback cb, final long startTime) {
        handleError(url, handler, request, th, cb, startTime, 0);
    }

    private static void handleError(final String url, Handler handler, final Request request, final Throwable th, final OnErrorCallback cb, final long startTime, int httpResponseCode) {
        final ResultCode resultCode = getErrorCode(th);
        reportTimeCost(url, 0, startTime, httpResponseCode, resultCode.nativeInt);
        handler.post(new Runnable() {
            @Override
            public void run() {
                cb.onFailure(request, resultCode, (Exception) th);
            }
        });
    }

    private static ResultCode getErrorCode(final Throwable th) {
        assert (th != null);

        if (th instanceof ConnectTimeoutException) {
            return ResultCode.RequestTimeout;
        }

        if (th instanceof SocketTimeoutException) {
            return ResultCode.ServerResponseTimeout;
        }

        if (th instanceof IOException) {
            return ResultCode.NetworkException;
        }

        if (th instanceof JsonSyntaxException) {
            return ResultCode.ServerException;
        }

        return ResultCode.DefaultException;
    }

    public static void reportTimeCost(String url, int responseCode, long startTime, int httpResponseCode, int exceptionCode) {
        if (mInit != null) {
            String methodName = mInit.getMethodName(url);
            final HashMap<String, String> map = new HashMap();
            map.put("methodName", methodName);
            map.put("bizCode", "" + responseCode);
            map.put("timeCost", "" + (System.currentTimeMillis() - startTime));
            map.put("httpCode", "" + httpResponseCode);
            map.put("exceptionCode", "" + exceptionCode);
            map.put("url", url);
            ThreadUtil.runOnUiThread(mContext, new Runnable() {
                @Override
                public void run() {
//                    GReport.onEvent(mContext, "ApiStatics", map);
                }
            });
        }
    }

    public static interface OnErrorCallback {
        public void onFailure(Request request, ResultCode code, Exception e);
    }

    public static interface StringCallback extends OnErrorCallback {
        public void onResponse(String result);
    }

    public static interface JsonArrayCallback<T> extends OnErrorCallback {
        public Class<T> getGenericType();

        public void onResponse(List<T> resultList);
    }

    public abstract interface JsonObjectCallback<T> extends OnErrorCallback {
        public Class<T> getGenericType();

        public void onResponse(T result);
    }

    public static interface Init {
        public String getMethodName(String url);

        public int getResponseCode(String result);
    }
}
