package com.waynezhang.mcommon.network;

/**
 * Created by waynezhang on 10/9/15.
 */
public interface HttpPolicyHandlerImp {
    boolean preReqPolicyHandle(Object tag, String url, String params, Http.StringCallback callback);
    void postReqPolicyHandle(Object tag, String url, String params, String responseString, Http.StringCallback callback, boolean shouldCache);
    boolean checkCacheHit(String urlKey);
    long getConnectionTimeout();
    long getSocketTimeout();

    // Added by waynezhang on 2/22/16: 添加手动删除超时Task接口中对应请求的map键值, 防止OkHttp的请求被cancel后PolicyHandler依然根据相应map键值返回callback
    void cancelTimeoutTask(Object tag);
}
