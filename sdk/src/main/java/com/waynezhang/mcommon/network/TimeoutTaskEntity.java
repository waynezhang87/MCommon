package com.waynezhang.mcommon.network;

/**
 * Created by waynezhang on 5/7/15.
 */
class TimeoutTaskEntity {
    protected long cacheExpTime;
    protected Http.StringCallback callback;
    protected String cachedResponse;

    public TimeoutTaskEntity(long cacheExpTime, Http.StringCallback callback, String cachedResponse) {
        this.cacheExpTime = cacheExpTime;
        this.callback = callback;
        this.cachedResponse = cachedResponse;
    }

}
