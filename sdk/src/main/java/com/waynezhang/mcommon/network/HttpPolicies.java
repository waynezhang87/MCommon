package com.waynezhang.mcommon.network;

import java.util.List;

/**
 * Created by waynezhang on 4/10/15.
 */
public class HttpPolicies {
    public static final int POLICY_FORCE_NETWORK = 0;   //强制使用网络，跳过缓存
    public static final int POLICY_CACHE_NETWORK_UPDATE_WITHOUT_CALLBACK = 1;    //先使用缓存，网络刷新缓存，但只有一次回调
    public static final int POLICY_CACHE_NETWORK_UPDATE_CALLBACK = 2;   //先使用缓存，网络刷新缓存，有一次或两次回调
    public static final int POLICY_NORMAL = 3; //网络超时，则使用缓存
    public static final int POLICY_FORCE_CACHE = 4; //有缓存则不访问网络，无缓存则访问网络

    public static final int CACHE_NOT_HIT_FLAG = 0;
    public static final int CACHE_HIT_FLAG = 1;

    public Policy defaultPolicy;
    public List<Policy> policies;

    public class Policy {
        public String key;
        public Params params;
        public int policyType;
        public int useCacheTimeout;
        public int cacheExpireTime;

        private String cacheKey;


        public String getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
        }
    }

    public class Params {
        public List<String> include;
        public List<String> exclude;
    }

}
