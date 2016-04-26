package com.waynezhang.mcommon.network;

import android.os.Handler;

import com.waynezhang.mcommon.util.L;
import com.waynezhang.mcommon.xwidget.ToastUtil;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by waynezhang on 4/10/15.
 */
public class TimeoutTask implements Runnable {
    private Handler handler;
    public static final long period = 500;
    private long startTime;

    private ConcurrentHashMap<Object, TimeoutTaskEntity> taskMap;

    private void addToTaskMap(Object tag, TimeoutTaskEntity entity) {
        taskMap.put(tag, entity);
    }

    public TimeoutTaskEntity getEntity(Object url) {
        return taskMap.get(url);
    }

    public TimeoutTaskEntity removeFromTaskMap(Object url) {
        if (taskMap == null | url == null) return null;
        return taskMap.remove(url);
    }

    public TimeoutTask(Handler handler) {
        this.taskMap = new ConcurrentHashMap<Object, TimeoutTaskEntity>();
        this.handler = handler;
    }

    public void addToTask(Object url, TimeoutTaskEntity entity) {
//        int typeFlag;
        //当缓存中没有取到response时，强制策略类型为FORCE_NETWORK_WITH_REFRESH_CALLBACK
//        if (cachedResponse == null) {
//            typeFlag = Policies.POLICY_FORCE_NETWORK;
//        } else {
//            typeFlag = policy.policyType;
//        }
//
//        if (typeFlag == Policies.POLICY_FORCE_CACHE_WITHOUT_NETWORK_CALLBACK ||
//                typeFlag == Policies.POLICY_FORCE_CACHE_WITH_NETWORK_CALLBACK || typeFlag == Policies.POLICY_FORCE_CACHE) {
//            L.i("Http", "This is a callback response from cache!!");
//            callback.onResponse(cachedResponse);
//        }
//
//        long cacheTimeout = policy.useCacheTimeout * 1000;
////        long connectionTimeout = Long.valueOf(policy.connectionTimeout) * 1000;
////        long socketTimeout = Long.valueOf(policy.socketTimeout) * 1000;
//
//        long cacheExpTime = cacheTimeout + System.currentTimeMillis();
//        TaskEntity entity = new TaskEntity(cacheExpTime, typeFlag, callback, cachedResponse);
        addToTaskMap(url, entity);

        //如果网络超时参数不为默认值0，则在请求头文件中添加socketTimeout与connectTimeout
//        if (connectionTimeout != 0 || socketTimeout != 0) {
//            OkHttpClient client1 = client.clone();
//            client1.setConnectTimeout(connectionTimeout, TimeUnit.MILLISECONDS);
//            client1.setReadTimeout(socketTimeout, TimeUnit.MILLISECONDS);
//            return client1;
//        }
//        return client;
    }

    @Override
    public void run() {
        Iterator it = taskMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            TimeoutTaskEntity entity = (TimeoutTaskEntity) entry.getValue();
            if (entity.cacheExpTime < System.currentTimeMillis()) {
                final Http.StringCallback callback = entity.callback;
                final String cachedResponse = entity.cachedResponse;
                if (cachedResponse != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(ResultCode.RequestTimeout.msg);
                            callback.onResponse(cachedResponse);
                            L.i("Http", "This is a callback response from cache!!(cachetimeout)");
                            L.v("Http", "cache response:" + cachedResponse);
                        }
                    });
                }
                it.remove();
            }
        }
        handler.postDelayed(this, period);
    }

}
