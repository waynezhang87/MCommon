package com.waynezhang.test;

import android.app.Application;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.waynezhang.test.service.TestErrorCode;
import com.waynezhang.test.service.TestResponse;
import com.waynezhang.mcommon.cache.Cache;
import com.waynezhang.mcommon.network.Http;
import com.waynezhang.mcommon.network.HttpPolicyHandler;
import com.waynezhang.mcommon.util.L;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by waynezhang on 1/31/16.
 */
public class TestApp extends Application {

    public Gson gson = new Gson();

    @Override
    public void onCreate() {
        super.onCreate();

        L.setLevel(L.VERBOSE);

        try {
            Cache cache = Cache.getBuilder(getApplicationContext()).build();
            Http.initCachePolicyHandler(this, HttpPolicyHandler.getBuilder(this).connectionTimeout(10000).socketTimeout(10000).setPolicies(null).setCache(cache).build(), new Http.Init() {
                @Override
                public String getMethodName(String url) {
                    return url.substring(url.lastIndexOf("/") + 1);
                }

                @Override
                public int getResponseCode(String result) {
                    if (result == null || TextUtils.isEmpty(result.trim())) {
                        return TestErrorCode.ERROR_INVALID_RESPONSE;
                    }
                    try {
                        Type type = new TypeToken<TestResponse<Object>>() {}.getType();
                        TestResponse<Object> testResponse = gson.fromJson(result, type);
                        if (testResponse == null) {
                            throw new JsonSyntaxException("Error format.");
                        }
                        return (int) testResponse.return_code;
                    } catch (final Exception e) {
                        L.e("Http", e.getMessage(), e);
                        return TestErrorCode.ERROR_INVALID_RESPONSE;
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
