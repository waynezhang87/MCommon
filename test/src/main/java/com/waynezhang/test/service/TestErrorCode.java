package com.waynezhang.test.service;

import android.util.SparseArray;

/**
 * Created by liuyagang on 14-12-29.
 * 错误码：
 * 客户端：		From - 10869700  to  - 10869799
 */
public class TestErrorCode {

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_NETWORK_TIMEOUT = -10869701;
    public static final int ERROR_INVALID_RESPONSE = -10869702;
    public static final int ERROR_UNKNOWN = -10869703;
    public static final int ERROR_NO_NETWORK = -10869704;
    public static final int ERROR_NETWORK_EXCEPTION = -10869705;
    public static final int ERROR_NO_LOGIN = -10869706;

    public static final SparseArray<String> MAP_ERROR_CODE = new SparseArray<String>(0);

    static {
        MAP_ERROR_CODE.put(ERROR_NO_ERROR, "操作成功。");
        MAP_ERROR_CODE.put(ERROR_NETWORK_TIMEOUT, "网络超时，请稍候再试。");
        MAP_ERROR_CODE.put(ERROR_INVALID_RESPONSE, "服务暂时不可用请稍后再试。");
        MAP_ERROR_CODE.put(ERROR_UNKNOWN, "网络不给力，请稍候再试！");
        MAP_ERROR_CODE.put(ERROR_NO_NETWORK, "无网络。");
        MAP_ERROR_CODE.put(ERROR_NETWORK_EXCEPTION, "网络异常。");
        MAP_ERROR_CODE.put(ERROR_NO_LOGIN, "未登录。");
    }

    public static String getErrorMsg(final int errorCode) {
        return MAP_ERROR_CODE.get(errorCode, "未知错误。");
    }
}
