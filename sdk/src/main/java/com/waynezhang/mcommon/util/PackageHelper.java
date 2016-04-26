/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-9-13.
 */

package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.waynezhang.mcommon.security.MD5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 游云安装包工具类.
 *
 * @author Liu yagang
 */
public class PackageHelper {
    private static final String TAG = PackageHelper.class.getSimpleName();

    /**
     * 遍历程序列表，判断是否安装过应用.
     *
     * @param packageName 应用包名
     * @return
     */
    public static boolean isPackageExist(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equalsIgnoreCase(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 遍历程序列表，获取应用版本.
     *
     * @param packageName 应用包名
     * @return
     */
    public static String getPackageVersion(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName, 0);
        if (packageInfo == null) {
            return "";
        }

        return packageInfo.versionName;
    }

    public static String getPackageVersion(Context context) {
        return getPackageVersion(context, context.getPackageName());
    }

    public static PackageInfo getPackageInfo(Context context, String packageName) {
        return getPackageInfo(context, packageName, 0);
    }

    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, context.getPackageName());
    }

    public static PackageInfo getPackageInfo(Context context, String packageName, int flags) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getPackageInfo(packageName, flags);
        } catch (NameNotFoundException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public static String getAppLabel(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName, 0);
        if (packageInfo == null) {
            return "";
        }

        PackageManager packageManager = context.getPackageManager();
        return packageManager.getApplicationLabel(packageInfo.applicationInfo).toString();
    }

    public static String getAppLabel(Context context) {
        return getAppLabel(context, context.getPackageName());
    }

    public static int getAppIconId(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName);
        if (packageInfo == null) {
            return -1;
        }

        return packageInfo.applicationInfo.icon;
    }

    public static int getAppIconId(Context context) {
        return getAppIconId(context, context.getPackageName());
    }

    public static String getSignString(Signature[] signatures) {
        if (signatures == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Signature sign : signatures) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(MD5.digest(sign.toByteArray()));
        }
        return sb.toString();
    }

    public static String getPackageSign(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName, PackageManager.GET_SIGNATURES);
        if (packageInfo == null) {
            return "";
        }
        return getSignString(packageInfo.signatures);
    }

    /**
     * 获取apk信息.
     *
     * @param apkPath apk路径
     */
    public static PackageInfo getApkInfo(Context context, String apkPath) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo apkInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA);
        return apkInfo;
    }

    /**
     * 获取apk版本.
     *
     * @param apkPath apk路径
     * @return
     */
    public static String getApkVersion(Context context, String apkPath) {
        if (apkPath != null) {
            PackageInfo apkInfo = getApkInfo(context, apkPath);
            if (apkInfo != null) {
                return apkInfo.versionName;
            }
        }
        return null;
    }

    /**
     * 检查权限.
     *
     * @param permission 权限
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pi = packageManager.getPackageInfo(context.getPackageName(), 0);
            // 得到自己的包名
            String pkgName = pi.packageName;
            PackageInfo pkgInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS); // 通过包名，返回包信息
            String[] requestedPermissions = pkgInfo.requestedPermissions;
            for (String requestedPermission : requestedPermissions) {
                if (requestedPermission.equalsIgnoreCase(permission)) {
                    return true;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString(), e);
        }
        return false;
    }

    /**
     * 获取当前程序的meta-data
     *
     * @return
     */
    public static String getMetaData(Context context, String key) {
        return getMetaData(context, context.getPackageName(), key);
    }

    /**
     * 获取指定程序的meta-data
     *
     * @return
     */
    public static String getMetaData(Context context, String packageName, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            //
        }
        return null;
    }

    /**
     * 系统缓冲中创建临时apk文件.
     *
     * @return
     */
    private static File createTempFile(Context context) {
        try {
            return File.createTempFile("youyun-service-", ".apk", context.getCacheDir());
        } catch (IOException e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

    /**
     * 从assets文件夹内释放游云服务apk文件到系统缓冲.
     *
     * @param fileName assets文件夹内apk文件名
     * @return
     */
    public static String retrieveApkFromAssets(Context context, String fileName) {
        String retrievedApkPath = null;
        if (fileName != null) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getAssets().open(fileName);
                File apk = createTempFile(context);
                if (apk != null) {
                    fos = new FileOutputStream(apk);
                    byte[] temp = new byte[1024];
                    int i = 0;
                    while ((i = is.read(temp)) > 0) {
                        fos.write(temp, 0, i);
                    }
                    retrievedApkPath = apk.getAbsolutePath();
                }
            } catch (IOException e) {
                Log.e(TAG, e.toString(), e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.toString(), e);
                    } finally {
                        fos = null;
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.toString(), e);
                    } finally {
                        is = null;
                    }
                }
            }
        }
        return retrievedApkPath;
    }

    public static void installApk(Context context, File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        installApk(context, Uri.fromFile(file));
    }

    public static void installApk(Context context, String uri) throws FileNotFoundException {
        File file = new File(uri);
        installApk(context, file);
    }

    public static void installApk(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行的数据类型
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);

    }

    public static void installApkForResult(Activity activity, int requestCode, File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }
        installApkForResult(activity, requestCode, Uri.fromFile(file));
    }

    public static void installApkForResult(Activity activity, int requestCode, String uri) throws FileNotFoundException {
        File file = new File(uri);
        installApkForResult(activity, requestCode, file);
    }

    public static void installApkForResult(Activity activity, int requestCode, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // 执行的数据类型
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        activity.startActivityForResult(intent, requestCode);
    }

    public static void openApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return;
        }
        context.startActivity(intent);
    }

    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAppRunning(Context context) {
        return isAppRunning(context, context.getPackageName());
    }

    public static boolean isAppOnForeground(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    public static boolean isAppOnForeground(Context context) {
        return isAppOnForeground(context, context.getPackageName());
    }

    //清除缓存和用户数据
    public static void cleanUserInfo(Context context) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    private static void cleanInternalCache(Context context) {
        deleteFilesByDirectory(context.getCacheDir());
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    private static void cleanDatabases(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/databases"));
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    private static void cleanSharedPreference(Context context) {
        deleteFilesByDirectory(new File("/data/data/"
                + context.getPackageName() + "/shared_prefs"));
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    private static void cleanFiles(Context context) {
        deleteFilesByDirectory(context.getFilesDir());
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    private static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            deleteFilesByDirectory(context.getExternalCacheDir());
        }
    }
}
