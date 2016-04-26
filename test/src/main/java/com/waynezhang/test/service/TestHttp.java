package com.waynezhang.test.service;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.$Gson$Types;
import com.waynezhang.mcommon.network.Http;
import com.waynezhang.mcommon.network.ResultCode;
import com.waynezhang.mcommon.util.L;
import com.squareup.okhttp.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by don on 12/15/14.
 */
public class TestHttp {
    private static Gson gson = new GsonBuilder().registerTypeAdapterFactory(new TestTypeAdapter()).create();

    public static <T> String get(String url, final TestReqCallback<T> cb) {
        return Http.get(url, new Http.StringCallback() {
            @Override
            public void onFailure(Request request, ResultCode code, Exception e) {
                cb.onFailure(new TestServiceException(code));
            }

            @Override
            public void onResponse(String result) {
                if (result == null || TextUtils.isEmpty(result.trim())) {
                    cb.onFailure(new TestServiceException(TestErrorCode.ERROR_INVALID_RESPONSE));
                    return;
                }
                try {
                    Type retType = cb.getGenericType();
                    //for nested Generic Type
                    if (retType instanceof ParameterizedType) {
                        ParameterizedType callbackType = (ParameterizedType) cb.getGenericType();
                        retType = $Gson$Types.newParameterizedTypeWithOwner(
                                callbackType.getOwnerType(), callbackType.getRawType(),
                                callbackType.getActualTypeArguments());
                    }
                    Type responseType = $Gson$Types.newParameterizedTypeWithOwner(null, TestResponse.class, retType);
                    TestResponse<T> testResponse = gson.fromJson(result, responseType);
                    if (testResponse == null) {
                        throw new JsonSyntaxException("Error format.");
                    }
                    if (testResponse.return_code != 0) {
                        cb.onFailure(new TestServiceException(testResponse.return_code, testResponse.return_message));
                    } else {
                        cb.onResponse(testResponse.data);
                    }
                } catch (final JsonSyntaxException e) {
                    L.e("TestHttp", e.getMessage(), e);

                    cb.onFailure(new TestServiceException(ResultCode.DefaultException));
                }
            }
        });
    }

    public static <T> String post(String url, Bitmap attachment, final TestReqCallback<T> cb) {
        return Http.post(url, attachment, new Http.StringCallback() {
            @Override
            public void onFailure(Request request, ResultCode code, Exception e) {
                cb.onFailure(new TestServiceException(code));
            }

            @Override
            public void onResponse(String result) {
                if (result == null || TextUtils.isEmpty(result.trim())) {
                    cb.onFailure(new TestServiceException(TestErrorCode.ERROR_INVALID_RESPONSE));
                    return;
                }
                try {
                    Type responseType = $Gson$Types.newParameterizedTypeWithOwner(null, TestResponse.class, cb.getGenericType());
                    TestResponse<T> testResponse = gson.fromJson(result, responseType);
                    if (testResponse == null) {
                        throw new JsonSyntaxException("Error format.");
                    }
                    if (testResponse.return_code != 0) {
                        cb.onFailure(new TestServiceException(testResponse.return_code, testResponse.return_message));
                    } else {
                        cb.onResponse(testResponse.data);
                    }
                } catch (final JsonSyntaxException e) {
                    L.e("TestHttp", e.getMessage(), e);
                    cb.onFailure(new TestServiceException(ResultCode.DefaultException));
                }
            }
        });
    }

    public static void cancel(String url) {
        Http.cancel(url);
    }

    public static void cancel(List<String> urls) {
        if (urls == null)
            return;

        for (String url : urls) {
            cancel(url);
        }
    }
}
