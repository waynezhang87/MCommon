package com.waynezhang.mcommon.notification.download;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/11/6.
 */
public class Download {
    public static void start(final Context context, String title, String url){
        start(context, title, url, null, 0);
    }

    public static void start(final Context context, String title, String url, String saveFileName, int logoResId){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.KEY_TITLE, title);
        intent.putExtra(DownloadService.KEY_URL, url);
        intent.putExtra(DownloadService.KEY_SAVE_NAME, saveFileName);
        if(logoResId != 0){
            intent.putExtra(DownloadService.KEY_LOGO, logoResId);
        }
        context.startService(intent);
    }

    public static void pause(final Context context, String url){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.KEY_URL, url);
        intent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.PAUSE);
        context.startService(intent);
    }

    public static void resume(final Context context, String url){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.KEY_URL, url);
        intent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.CONTINUE);
        context.startService(intent);
    }

    public static void cancel(final Context context, String url){
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.KEY_URL, url);
        intent.putExtra(DownloadService.KEY_DOWNLOAD_ACTION, DownloadAction.CANCEL);
        context.startService(intent);
    }

    public static List<DownloadStatus> getDownloadList(){
        List<DownloadStatus> list = new ArrayList<>(DownloadService.taskMap.size());
        for(Map.Entry<String, DownloadThread> entry : DownloadService.taskMap.entrySet()){
            String url = entry.getKey();
            int status = entry.getValue().status;
            DownloadBean bean = entry.getValue().getBean();
            File file = new File(bean.savePath);
            if(!file.exists() && status == DownloadStatus.SUCCESS){
                 continue;
            }
            DownloadStatus downloadStatus = new DownloadStatus(status, "", url, 0);
            downloadStatus.setDownloadSpeed(bean.downloadSpeed);
            downloadStatus.setDownloadedSize(bean.loadedSize);
            downloadStatus.setSize(bean.size);
            list.add(downloadStatus);
        }
        return  list;
    }
}
