package com.waynezhang.test.service;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by waynezhang on 1/31/16.
 */
public class TestServiceApi {
    public static final String URI_CONFIG = "/api/accountapi/config";
    public static final String URI_CONFIG2 = "/api/tradeapi/config";

    private static String formatedUrl(String uri, String methodName, TestApiParams params) {
        StringBuilder sb = new StringBuilder();
        sb.append(TestConfig.BASE_URL);
        sb.append(uri);
        sb.append("?src_code=").append(TestConfig.SRC_CODE);
//        sb.append("&_=").append(UUID.randomUUID().toString());
        if (!TextUtils.isEmpty(methodName)) {
            sb.append("&method=").append(methodName);
        }
        if (params != null)
            sb.append("&params=").append(params.toUrlString());
        return sb.toString();
    }

    public static String getGameOperatorAreaGroupListOnshelf(final TestReqCallback<List<Object>> cb) {
        String method = "GetGameOperatorAreaGroupList";
        TestApiParams params = new TestApiParams("type", "group");
        params.add("area_id", -1);
        params.add("game_id", 89);
        return TestHttp.get(formatedUrl(URI_CONFIG, method, params), cb);
    }

    public static String getGameOperatorAreaGroupListOnshelf2(final TestReqCallback<List<Object>> cb) {
        String method = "GetGameOperatorAreaGroupList";
        TestApiParams params = new TestApiParams("type", "group");
        params.add("area_id", -1);
        params.add("game_id", 89);
        return TestHttp.get(formatedUrl(URI_CONFIG2, method, params), cb);
    }
}
