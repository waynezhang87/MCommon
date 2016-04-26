package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.waynezhang.mcommon.file.FileUtil;
import com.waynezhang.mcommon.network.FileDownloader;
import com.waynezhang.mcommon.support.Clipboard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.HashMap;


public class AppwallJsApi {
    private static final String TAG = AppwallJsApi.class.getSimpleName();

    public static final int DOWNLOAD_ERROR_CODE_SUCCESSED = 0;
    public static final int DOWNLOAD_ERROR_CODE_FAILED = -1001;
    public static final int DOWNLOAD_ERROR_CODE_FILE_NOT_FOUND = -1002;
    public static final int DOWNLOAD_ERROR_CODE_OPARATION_NOT_SUPPORT = -1003;
    public static final int DOWNLOAD_ERROR_CODE_TASK_NOT_FOUND = -1004;
    public static final int DOWNLOAD_ERROR_CODE_TASK_NOT_FINISHED = -1005;
    public static final int DOWNLOAD_ERROR_CODE_TASK_INPROGRESS = -1006;

    public static final int DOWNLOAD_EVENT_PROCESSING = 1001;

    public static final int DOWNLOAD_ACTION_STOP = 2001;

    public static final int REQUEST_CODE_APK_INSTALL = 3001;

    private Activity activity;
    private SystemInfo systemInfo;
    private WebView webView;
    private HashMap<String, FileDownloader> downloadMap = new HashMap<String, FileDownloader>();

    String jsBackKeyClickCallback = "";
    String jsInstallerClosedCallback = "";

    public AppwallJsApi(Activity activity, WebView webView) {
        // this.activity = activity.getApplicationContext();
        this.activity = activity;
        this.systemInfo = new SystemInfo(activity);
        this.webView = webView;
    }

    @JavascriptInterface
    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    @JavascriptInterface
    public void setValue(String key, String value) {
        Log.d(TAG, String.format("setValue(%s, %s)", key, value));

        SharedPreferences sharedPreferences = activity.getSharedPreferences("aw_cookie_db", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(key, value).commit();
    }

    @JavascriptInterface
    public String getValue(String key) {
        Log.d(TAG, String.format("getValue(%s)", key));

        SharedPreferences sharedPreferences = activity.getSharedPreferences("aw_cookie_db", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    @JavascriptInterface
    public void saveClipboard(String text) {
        Clipboard.saveClipboard(activity, text);
    }

    @JavascriptInterface
    public void listenBackKeyClick(final String callback) {
        Log.d(TAG, String.format("listenBackKeyClick(%s)", callback));

        jsBackKeyClickCallback = callback;
    }

    @JavascriptInterface
    public void listenInstallerClosed(final String callback) {
        Log.d(TAG, String.format("listenInstallerClosed(%s)", callback));

        jsInstallerClosedCallback = callback;
    }

    //
    @JavascriptInterface
    public String getAppStatus(String json) {
        Log.d(TAG, String.format("getAppStatus(%s)", json));

        try {
            JSONArray packageList = new JSONArray(json);
            JSONArray packageInfoList = new JSONArray();
            for (int i = 0; i < packageList.length(); i++) {
                String packageName = (String) packageList.get(i);
                PackageInfo packageInfo = PackageHelper.getPackageInfo(activity, packageName, PackageManager.GET_SIGNATURES);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("packageName", packageName);
                if (packageInfo != null) {
                    jsonObject.put("appLabel", PackageHelper.getAppLabel(activity, packageName));
                    jsonObject.put("versionCode", packageInfo.versionCode);
                    jsonObject.put("versionName", packageInfo.versionName);
                    jsonObject.put("firstInstallTime", packageInfo.firstInstallTime);
                    jsonObject.put("lastUpdateTime", packageInfo.lastUpdateTime);
                    jsonObject.put("signature", PackageHelper.getSignString(packageInfo.signatures));
                }
                packageInfoList.put(jsonObject);
            }
            return packageInfoList.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "{}";
    }

    @JavascriptInterface
    public void downloadFile(final String url, final String callback) {
        if (!FileUtil.createDirectory(getCacheFilePath())) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_FAILED, "下载失败", "");
            return;
        }

        FileDownloader downloader = downloadMap.get(url);
        if (downloader != null) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_TASK_INPROGRESS, "下载任务已经存在", "");
            return;
        }

        downloader = new FileDownloader();
        downloadMap.put(url, downloader);
        downloader.setFileUrl(url);
        downloader.setSavePath(getCacheFilePath() + "/" + UrlUtil.getFileName(url));
        downloader.setProgressOutput(new FileDownloader.IDownloadProgress() {

            @Override
            public void downloadProgress(int progress) {
                doJsCallback(callback, DOWNLOAD_EVENT_PROCESSING, "正在下载", String.format("{\"url\":\"%s\",\"progress\":%d}", url, progress));
            }

            @Override
            public void downloadSuccess() {
                doJsCallback(callback, DOWNLOAD_ERROR_CODE_SUCCESSED, "下载完成", String.format("{\"url\":\"%s\"}", url));
            }

            @Override
            public void downloadFailure() {
                doJsCallback(callback, DOWNLOAD_ERROR_CODE_FAILED, "下载失败", String.format("{\"url\":\"%s\"}", url));
            }

        });
        downloader.start();
    }

    @JavascriptInterface
    public void controlDownload(String url, int action, String callback) {
        Log.d(TAG, String.format("controlDownload(%s, %d, %s)", url, action, callback));

        switch (action) {
            case DOWNLOAD_ACTION_STOP:
                FileDownloader downloader = downloadMap.get(url);
                if (downloader == null || downloader.isFinished()) {
                    doJsCallback(callback, DOWNLOAD_ERROR_CODE_TASK_NOT_FOUND, "下载任务不存在", String.format("{\"url\":\"%s\"}", url));
                    return;
                }
                downloader.stop();
                doJsCallback(callback, DOWNLOAD_ERROR_CODE_SUCCESSED, "操作成功", String.format("{\"url\":\"%s\"}", url));
                break;
            default:
                doJsCallback(callback, DOWNLOAD_ERROR_CODE_OPARATION_NOT_SUPPORT, "不支持该操作", String.format("{\"url\":\"%s\"}", url));
                break;
        }
    }

    @JavascriptInterface
    public void installApk(String url, String callback) {
        Log.d(TAG, String.format("installApk(%s)", url));

        FileDownloader downloader = downloadMap.get(url);
        if (downloader == null) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_TASK_NOT_FOUND, "下载任务不存在", String.format("{\"url\":\"%s\"}", url));
            return;
        }
        if (!downloader.isFinished() || downloader.hasError()) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_TASK_NOT_FINISHED, "下载任务没有完成", String.format("{\"url\":\"%s\"}", url));
            return;
        }
        try {
            PackageHelper.installApkForResult(activity, REQUEST_CODE_APK_INSTALL, downloader.getSavePath());
        } catch (FileNotFoundException e) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_FAILED, "安装文件不存在", String.format("{\"url\":\"%s\"}", url));
            e.printStackTrace();
        }
        doJsCallback(callback, DOWNLOAD_ERROR_CODE_SUCCESSED, "操作成功", String.format("{\"url\":\"%s\"}", url));
    }

    @JavascriptInterface
    public void removeTask(String url, String callback) {
        Log.d(TAG, String.format("installApk(%s)", url));

        FileDownloader downloader = downloadMap.get(url);
        if (downloader == null) {
            doJsCallback(callback, DOWNLOAD_ERROR_CODE_TASK_NOT_FOUND, "下载任务不存在", String.format("{\"url\":\"%s\"}", url));
            return;
        }
        if (!downloader.isFinished()) {
            downloader.stop();
        }
        downloadMap.remove(url);
        doJsCallback(callback, DOWNLOAD_ERROR_CODE_SUCCESSED, "操作成功", String.format("{\"url\":\"%s\"}", url));
    }

    @JavascriptInterface
    public void openApp(String packageName) {
        Log.d(TAG, String.format("openApp(%s)", packageName));

        PackageHelper.openApp(activity, packageName);
    }

    public boolean doBackKeyClick() {
        if (StringUtil.isNotEmpty(jsBackKeyClickCallback)) {
            executeJs(String.format("javascript:%s()", jsBackKeyClickCallback));
            return true;
        }
        return false;
    }

    public void doInstallerClosed() {
        if (StringUtil.isNotEmpty(jsInstallerClosedCallback)) {
            executeJs(String.format("javascript:%s()", jsInstallerClosedCallback));
        }
    }

    private String getCacheFilePath() {
        return Environment.getExternalStorageDirectory() + "/" + activity.getApplicationInfo().packageName + "/appwall";
    }

    private void doJsCallback(String callback, int code, String msg, String data) {
        executeJs(String.format("javascript:%s(%d, \"%s\", \"%s\")", callback, code, StringUtil.jsStringEncode(msg),
                StringUtil.jsStringEncode(data)));
    }

    private void executeJs(final String js) {
        Log.d(TAG, js);

        ThreadUtil.runOnUiThread(activity, new Runnable() {

            @Override
            public void run() {
                webView.loadUrl(js);
            }

        });
    }
}
