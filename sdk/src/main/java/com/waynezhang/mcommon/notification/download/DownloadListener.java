package com.waynezhang.mcommon.notification.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 如果不需要过滤url，请使用默认构造函数，此时会收到下载组件所有url的下载状态通知；
 * 如果使用带url的构造函数，只会收到该url的下载状态通知
 * Created by liuxiaofeng on 2015/11/6.
 */
public abstract class DownloadListener {
    private static final String TAG = DownloadListener.class.getSimpleName();
    private String url;
    private BroadcastReceiver receiver;

    public DownloadListener(){}

    public DownloadListener(String url){
        this.url = url;
    }

    public void register(Context context){
        if(context == null){
            throw new IllegalArgumentException(TAG + " register params 'context' can not be null!");
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadStatus status = intent.getParcelableExtra(DownloadThread.DOWNLOAD_STATUS);
                if(null != status){
                    if(null != url && !url.equals(status.getUrl())){
                        return;
                    }

                    switch (status.getCode()){
                        case DownloadStatus.FAIL:
                            failure(status);
                            break;
                        case DownloadStatus.SUCCESS:
                            success(status);
                            break;
                        case DownloadStatus.DOWNLOADING:
                            progress(status);
                            break;
                        case DownloadStatus.PAUSE:
                            pause(status);
                            break;
                        case DownloadStatus.CONTINUE:
                            resume(status);
                            break;
                        case DownloadStatus.CANCEL:
                            cancel(status);
                            break;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadThread.RECEIVER_ACTION);
        context.registerReceiver(receiver, filter);
    }

    public void unregister(Context context){
        if(context == null){
            throw new IllegalArgumentException(TAG + " unregister params 'context' can not be null!");
        }

        context.unregisterReceiver(receiver);
    }

    public abstract void progress(DownloadStatus status);

    public abstract void failure(DownloadStatus status);

    public abstract void success(DownloadStatus status);

    public void pause(DownloadStatus status){}

    public void resume(DownloadStatus status){}

    public void cancel(DownloadStatus status){}
}
