package com.waynezhang.mcommon.util;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import java.io.File;

public class ApkDownLoad {
    public static final String APK_DOWNLOAD_ID = "mhh_apk_download_id";

    private Context context;
    private String url;
    private String exceptionUrl;
    private String notificationTitle;
    private String notificationDescription;
    private String saveFileName;
    private String apkFilePath;
    private DownloadManager downloadManager;
    private CompleteReceiver completeReceiver;

    /**
     * @param context
     * @param url                     下载apk的url
     * @param notificationTitle       通知栏标题
     * @param notificationDescription 通知栏描述
     */
    public ApkDownLoad(Context context, String url, String notificationTitle,
                       String notificationDescription, String exceptionUrl) {
        super();
        this.context = context;
        this.url = url;
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.exceptionUrl = exceptionUrl;

        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        completeReceiver = new CompleteReceiver();

        this.saveFileName = context.getPackageName() + ".apk";
        /** register download success broadcast **/
        context.registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * Check if the primary "external" storage device is available.
     *
     * @return
     */
    public static boolean hasSDCardMounted() {
        String state = Environment.getExternalStorageState();
        if (state != null && state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * install app
     *
     * @param context
     * @param filePath
     * @return whether apk exist
     */
    public static boolean install(Context context, String filePath) {
        L.d("ApkDownLoad", "filePath=" + filePath);
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if (file != null && file.length() > 0 && file.exists() && file.isFile()) {
            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

    public void execute() {
        //清除已下载的内容重新下载
        long downloadId = SharedPreferencesUtil.getSharedPreferencesValue(context, APK_DOWNLOAD_ID, -1);
        if (downloadId != -1) {
            downloadManager.remove(downloadId);
            SharedPreferencesUtil.setSharedPreferences(context, APK_DOWNLOAD_ID, -1);
        }

        try{
            Request request = new Request(Uri.parse(url));
            //设置Notification中显示的文字
            request.setTitle(notificationTitle);
            request.setDescription(notificationDescription);
            //设置可用的网络类型
            //request.setAllowedNetworkTypes(Request.NETWORK_MOBILE  | Request.NETWORK_WIFI);
            //设置状态栏中显示Notification
            setNotificationVisibility(request);
            //不显示下载界面
            request.setVisibleInDownloadsUi(false);

            //设置下载后文件存放的位置
            File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }

            File file = new File(folder, saveFileName);
            if (file.exists()) {
                L.d("ApkDownLoad", "delete " + file.getAbsolutePath());
                file.delete();
            }
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, saveFileName);

            //设置文件类型
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            request.setMimeType(mimeString);

            downloadId = downloadManager.enqueue(request);
        }catch (Exception e){
            Uri uri = Uri.parse(exceptionUrl);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);

            e.printStackTrace();
            return;
        }
        //保存返回唯一的downloadId
        SharedPreferencesUtil.setSharedPreferences(context, APK_DOWNLOAD_ID, downloadId);
    }

    @TargetApi(11)
    private void setNotificationVisibility(final Request request) {
        if (Build.VERSION.SDK_INT >= 11) {
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
    }

    ;

    /**
     * 查询下载状态
     */
    public int queryDownloadStatus(DownloadManager downloadManager, long downloadId) {
        int result = -1;
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                apkFilePath = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            long downloadId = SharedPreferencesUtil.getSharedPreferencesValue(context, APK_DOWNLOAD_ID, -1);

            if (completeDownloadId == downloadId) {

                // if download successful
                if (queryDownloadStatus(downloadManager, downloadId) == DownloadManager.STATUS_SUCCESSFUL) {

                    //clear downloadId
                    SharedPreferencesUtil.setSharedPreferences(context, APK_DOWNLOAD_ID, -1);

                    //unregisterReceiver
                    context.unregisterReceiver(completeReceiver);

                    install(context, apkFilePath);
                }
            }
        }
    }

}
