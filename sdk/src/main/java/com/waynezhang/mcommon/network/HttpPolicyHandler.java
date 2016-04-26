package com.waynezhang.mcommon.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.waynezhang.mcommon.cache.Cache;
import com.waynezhang.mcommon.cache.CacheInterface;
import com.waynezhang.mcommon.security.MD5;
import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.util.ResourceUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by waynezhang on 9/18/15.
 */
public class HttpPolicyHandler implements HttpPolicyHandlerImp{
    public static final String TAG = HttpPolicyHandler.class.getSimpleName().toString();

    private static Handler handler = new Handler(Looper.getMainLooper());
    private TimeoutTask timeoutTask;
    private HttpPolicies policies;
    private ConcurrentHashMap<String, Integer> cacheHitMap;
    //此回调用来过滤哪些returnCode对应的response不需要缓存
//    private HandleErrorCodeCallback handleErrorCodeCallback;
    private CacheInterface cache;
    private boolean initFlag = false;
    private long connectionTimeout;
    private long socketTimeout;

    private HttpPolicyHandler(Builder builder) throws IOException {
        timeoutTask = new TimeoutTask(handler);
        cacheHitMap = new ConcurrentHashMap<>();
        this.connectionTimeout = builder.connectionTimeout;
        this.socketTimeout = builder.socketTimeout;
//        this.handleErrorCodeCallback = builder.callback;
        this.cache = builder.cache == null ? Cache.getInstance() : builder.cache;
        this.policies = builder.httpPolicies == null ? ResourceUtil.getPoliciesFromRaw(builder.context) : builder.httpPolicies;
        initFlag = policies == null ? false : true;
        if (initFlag) timeoutTask.run();
    }

    public static Builder getBuilder(Context context) {
        return new Builder(context);
//        if (instance != null) {
//            throw new IllegalArgumentException("Cache instance has been built.");
//        } else {
//        }
    }

    @Override
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public long getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public void cancelTimeoutTask(Object tag) {
        if(timeoutTask != null) {
            timeoutTask.removeFromTaskMap(tag);
        }
    }

    private HttpPolicies.Policy checkPolicyHit(String urlKey, String postParams) {
        if (!initFlag) {
            return null;
        } else {
            for (HttpPolicies.Policy policy : policies.policies) {
                if (policyCheck(urlKey, postParams, policy)) {
                    return policy;
                }
            }
            policyCheck(urlKey, postParams, policies.defaultPolicy);
            return policies.defaultPolicy;
        }
    }

    @Override
    public boolean checkCacheHit(String urlKey) {
        if (!initFlag) {
            return false;
        }
        Integer hitFlag = cacheHitMap.get(urlKey);
        if (null != hitFlag  && hitFlag.intValue() == HttpPolicies.CACHE_HIT_FLAG) {
            return true;
        }
        return false;
    }

    public boolean isInit() {
        return initFlag;
    }

    private String getCache(String key) {
        if (cache != null) {
            return cache.getCache(key);
        }
        return null;
    }

    private void putCache(String key, String responseString, int period) {
        if (cache != null) {
            cache.putCache(key, responseString, period);
        }
    }

    @Override
    public boolean preReqPolicyHandle(final Object tag, final String url, final String params, final Http.StringCallback callback) {
        final HttpPolicies.Policy policy = checkPolicyHit(url, params);
        if (policy == null) {
            return true;
        }
        final String responseString;
        switch (policy.policyType) {
            case HttpPolicies.POLICY_FORCE_NETWORK:
                return true;
            case HttpPolicies.POLICY_CACHE_NETWORK_UPDATE_WITHOUT_CALLBACK:
                responseString = getCache(policy.getCacheKey());
                if (responseString != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i(TAG, "Pre http request policy check hit cache!!");
                            L.i(TAG, "This is a callback response from cache!!(prehttprequest)");
                            L.v(TAG, "[" + url + "] cache response:" + responseString);
                            callback.onResponse(responseString);
                        }
                    });
                    cacheHitMap.put(url, HttpPolicies.CACHE_HIT_FLAG);
                } else {
                    L.i(TAG, "Pre http request policy check did not hit cache, no cache response!!");
                    cacheHitMap.put(url, HttpPolicies.CACHE_NOT_HIT_FLAG);
                }
                return true;
            case HttpPolicies.POLICY_CACHE_NETWORK_UPDATE_CALLBACK:
                responseString = getCache(policy.getCacheKey());
                if (responseString != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i(TAG, "Pre http request policy check hit cache!!");
                            L.i(TAG, "This is a callback response from cache!!(prehttprequest)");
                            L.v(TAG, "[" + url + "] cache response:" + responseString);
                            callback.onResponse(responseString);
                        }
                    });
                    cacheHitMap.put(url, HttpPolicies.CACHE_HIT_FLAG);
                } else {
                    L.i(TAG, "Pre http request policy check did not hit cache, no cache response!!");
                    cacheHitMap.put(url, HttpPolicies.CACHE_NOT_HIT_FLAG);
                }
                return true;
            case HttpPolicies.POLICY_NORMAL:
                responseString = getCache(policy.getCacheKey());
                if (responseString != null) {
                    L.i(TAG, "Pre http request policy check hit cache!!");
                    L.v(TAG, "[" + url + "] cache response:" + responseString);
                    cacheHitMap.put(url, HttpPolicies.CACHE_HIT_FLAG);

                    long cacheTimeout = policy.useCacheTimeout * 1000;
                    long cacheExpTime = cacheTimeout + System.currentTimeMillis();
                    TimeoutTaskEntity entity = new TimeoutTaskEntity(cacheExpTime, callback, responseString);
                    timeoutTask.addToTask(tag, entity);
                } else {
                    L.i(TAG, "Pre http request policy check did not hit cache, no cache response!!");
                    cacheHitMap.put(url, HttpPolicies.CACHE_NOT_HIT_FLAG);
                }
                return true;
            case HttpPolicies.POLICY_FORCE_CACHE:
                responseString = getCache(policy.getCacheKey());
                if (responseString != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i(TAG, "Pre http request policy check hit cache!!");
                            L.i(TAG, "This is a callback response from cache!!(prehttprequest)");
                            L.v(TAG, "[" + url + "] cache response:" + responseString);
                            callback.onResponse(responseString);
                        }
                    });
                    cacheHitMap.put(url, HttpPolicies.CACHE_HIT_FLAG);
                    return false;
                } else {
                    L.i(TAG, "Pre http request policy check did not hit cache, no cache response!!");
                    cacheHitMap.put(url, HttpPolicies.CACHE_NOT_HIT_FLAG);
                    return true;
                }
        }
        return false;
    }

    @Override
    public void postReqPolicyHandle(final Object tag, final String url, final String params, final String responseString, final Http.StringCallback callback, boolean shouldCache) {
        final HttpPolicies.Policy policy = checkPolicyHit(url, params);
        if (policy == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(responseString);
                }
            });
            return;
        }
        switch (policy.policyType) {
            case HttpPolicies.POLICY_FORCE_NETWORK:
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        L.i("Http", "This is a callback response from http!!");
                        callback.onResponse(responseString);
                    }
                });
                break;
            case HttpPolicies.POLICY_CACHE_NETWORK_UPDATE_WITHOUT_CALLBACK:
                if (shouldCache) {
                    putCache(policy.getCacheKey(), responseString, policy.cacheExpireTime == 0 ? Integer.MAX_VALUE : policy.cacheExpireTime);
                }
                if (!checkCacheHit(url)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i("Http", "This is a callback response from http!!");
                            callback.onResponse(responseString);
                        }
                    });
                }
                break;
            case HttpPolicies.POLICY_CACHE_NETWORK_UPDATE_CALLBACK:
                if (shouldCache) {
                    putCache(policy.getCacheKey(), responseString, policy.cacheExpireTime == 0 ? Integer.MAX_VALUE : policy.cacheExpireTime);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        L.i("Http", "This is a callback response from http!!");
                        callback.onResponse(responseString);
                    }
                });
                break;
            case HttpPolicies.POLICY_NORMAL:
                if (shouldCache) {
                    putCache(policy.getCacheKey(), responseString, policy.cacheExpireTime == 0 ? Integer.MAX_VALUE : policy.cacheExpireTime);
                }
                if (timeoutTask.removeFromTaskMap(tag) != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i("Http", "This is a callback response from http!!");
                            callback.onResponse(responseString);
                        }
                    });
                } else if (!checkCacheHit(url)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i("Http", "This is a callback response from http!!");
                            callback.onResponse(responseString);
                        }
                    });
                }
                break;
            case HttpPolicies.POLICY_FORCE_CACHE:
                if (shouldCache) {
                    putCache(policy.getCacheKey(), responseString, policy.cacheExpireTime == 0 ? Integer.MAX_VALUE : policy.cacheExpireTime);
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        L.i("Http", "This is a callback response from http!!");
                        callback.onResponse(responseString);
                    }
                });
                break;
        }

    }

    // Added by waynezhang on 1/30/16: 添加策略include/exclude支持
    public static boolean policyCheck(String url, String postParam, HttpPolicies.Policy policy) {
        boolean isHit = false;

        if (policy.key == null) policy.key = "";

        if (policy.params == null) {
            isHit = url.matches(".*" + policy.key + ".*");
            policy.setCacheKey(MD5.digest(url + postParam));
            return isHit;
        }

        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuffer regexUrl = new StringBuffer(".*").append(policy.key).append(".*&params=").append("|.*").append(policy.key);

        List<String> includeList = policy.params.include;
        List<String> excludeList = policy.params.exclude;

        StringBuffer urlKey = new StringBuffer();
        StringBuffer postParamKey = new StringBuffer();

        if (includeList != null && !includeList.isEmpty()) {
            Pattern patternUrl = Pattern.compile(regexUrl.toString());
            Matcher matcherUrl = patternUrl.matcher(url);
            if (matcherUrl.find()) {
                urlKey.append(matcherUrl.group());
            } else {
                urlKey.append(url);
            }

            for (String include : includeList) {
                String regexInclude = "\"" + include + "\":[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?[,]?|\"" + include + "\":\"\\w+\"[,]?|\"" + include + "\":[true false]+[,]?";
                Pattern pattern = Pattern.compile(regexInclude);
                Matcher matcher = pattern.matcher(url);
                while (matcher.find()) {
                    isHit = true;
                    urlKey.append(matcher.group());
                }

                matcher = pattern.matcher(postParam);
                while (matcher.find()) {
                    isHit = true;
                    postParamKey.append(matcher.group());
                }
            }
        } else {
            if (excludeList != null && !excludeList.isEmpty()) {
                for (String exclude : excludeList) {
                    String regexExclude = "\"" + exclude + "\":[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?[,]?|\"" + exclude + "\":\"\\w+\"[,]?|\"" + exclude + "\":[true false]+[,]?";
                    Pattern pattern = Pattern.compile(regexExclude);
                    Matcher matcher = pattern.matcher(url);
                    while (matcher.find()) {
                        isHit = true;
                        url = url.replace(matcher.group(), "");
                    }
                    urlKey.append(url);

                    matcher = pattern.matcher(postParam);
                    while (matcher.find()) {
                        isHit = true;
                        postParam = postParam.replace(exclude, "");
                    }
                    postParamKey.append(postParam);

                }
            } else {
                isHit = url.matches(regexUrl.toString());
                urlKey.append(url);
                postParamKey.append(postParam);
            }
        }

        policy.setCacheKey(MD5.digest(urlKey.toString() + postParamKey.toString()));

        return isHit;

    }

//    /**
//     * 此回调用来过滤哪些returnCode对应的response不需要缓存
//     */
//    public interface HandleErrorCodeCallback {
//        boolean isResponseShouldCache(String response);
//    }

    public static class Builder {
        private Context context;
        private long connectionTimeout;
        private long socketTimeout;
//        private HandleErrorCodeCallback callback;
        private HttpPolicies httpPolicies;
        private CacheInterface cache;

        public Builder(Context ctx) {
            this.context = ctx.getApplicationContext();
        }

        public Builder socketTimeout(long socketTimeout) {
            if (socketTimeout > 0) {
                this.socketTimeout = socketTimeout;
            }
            return this;
        }

        public Builder connectionTimeout(long connectionTimeout) {
            if (connectionTimeout > 0) {
                this.connectionTimeout = connectionTimeout;
            }
            return this;
        }

//        public Builder setHandleErrorCodeCallback(HandleErrorCodeCallback callback) {
//            if (callback != null) {
//                this.callback = callback;
//            }
//            return this;
//        }

        public Builder setPolicies(HttpPolicies httpPolicies) {
            this.httpPolicies = httpPolicies;
            return this;
        }

        public Builder setCache(CacheInterface cache) {
            this.cache = cache;
            return this;
        }

        public HttpPolicyHandler build() throws IOException {
            if (this.socketTimeout == 0)
                this.socketTimeout = Integer.MAX_VALUE;
            if (this.connectionTimeout == 0)
                this.connectionTimeout = Integer.MAX_VALUE;
            return new HttpPolicyHandler(this);
        }
    }

}
