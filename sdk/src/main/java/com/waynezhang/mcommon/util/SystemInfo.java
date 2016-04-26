package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.util.UUID;

public class SystemInfo {
    Context context;
    TelephonyManager telephonyManager;

    public SystemInfo(Context context) {
        this.context = context;

        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    @JavascriptInterface
    public String getAndroidId() {
        if (context == null) {
            return "";
        }

        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    @JavascriptInterface
    public String getImei() {
        if (telephonyManager == null) {
            return "";
        }

        return telephonyManager.getDeviceId();
    }

    @JavascriptInterface
    public String getDeviceId() {
        if (telephonyManager == null) {
            return "";
        }

        return "" + getImei() + "-" + getAndroidId();
    }

    @JavascriptInterface
    public String getSimSerialNumber() {
        if (telephonyManager == null) {
            return "";
        }

        String simSerialNumber = telephonyManager.getSimSerialNumber();
        if (null == simSerialNumber) {
            return "";
        }
        return simSerialNumber;
    }

    @JavascriptInterface
    public String getImsi() {
        if (telephonyManager == null) {
            return "";
        }
        String imsi = telephonyManager.getSubscriberId();
        if (null == imsi) {
            return "";
        }
        return imsi;
    }

    @JavascriptInterface
    public String getPhoneModel() {
        return android.os.Build.MODEL;
    }

    @JavascriptInterface
    public String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    @JavascriptInterface
    public String getCpuType() {
        return android.os.Build.CPU_ABI;
    }

    @JavascriptInterface
    public String getPhoneNum() {
        if (telephonyManager == null) {
            return "";
        }
        return telephonyManager.getLine1Number();
    }

    @JavascriptInterface
    public long getSDCardSize() {
        if (context == null) {
            return 0;
        }

        try {
            String sDcString = android.os.Environment.getExternalStorageState();
            if (sDcString.equals(android.os.Environment.MEDIA_MOUNTED)) {
                File pathFile = android.os.Environment.getExternalStorageDirectory();
                android.os.StatFs statfs = new android.os.StatFs(pathFile.getPath());
                @SuppressWarnings("deprecation")
                long nTotalBlocks = statfs.getBlockCount();
                @SuppressWarnings("deprecation")
                long nBlocSize = statfs.getBlockSize();
                // long nAvailaBlock = statfs.getAvailableBlocks();
                // long nFreeBlock = statfs.getFreeBlocks();
                long nSDTotalSize = nTotalBlocks * nBlocSize / 1024 / 1024;
                // long nSDFreeSize = nAvailaBlock * nBlocSize / 1024 / 1024;
                return nSDTotalSize;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @JavascriptInterface
    public long getRamSize() {
        if (context == null) {
            return 0;
        }

        String file_path = "/proc/meminfo";// 系统内存信息文件
        String ram_info;
        String[] arrayOfRam;
        long initial_memory = 0;
        try {
            FileReader fr = new FileReader(file_path);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            // 读取meminfo第一行，系统总内存大小
            ram_info = localBufferedReader.readLine();
            arrayOfRam = ram_info.split("\\s+");// 实现多个空格切割的效果
            initial_memory = Integer.valueOf(arrayOfRam[1]).intValue() / 1024;// 获得系统总内存，单位是KB，除以1024转换为B
            localBufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return initial_memory;
    }

    @JavascriptInterface
    public String getMetrics() {
        if (context == null) {
            return "";
        }

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels + "*" + metrics.heightPixels;
    }

    @JavascriptInterface
    public String getLogId() {
        if (context == null) {
            return "";
        }

        return Installation.id(context);
    }

    /**
     * 获取当前的网络状态
     *
     * @return -1 没有网络 1 WIFI网络 2 cmwap网络 3 cmnet网络.
     */
    @JavascriptInterface
    public int getNetStatus() {
        if (context == null) {
            return -1;
        }

        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().compareToIgnoreCase("cmnet") == 0) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * 检测网络状态.
     *
     * @return
     */
    @JavascriptInterface
    public boolean isNetworkAvailable() {
        if (context == null) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @JavascriptInterface
    public int getNetworkType() {
        if (telephonyManager == null) {
            return TelephonyManager.NETWORK_TYPE_UNKNOWN;
        }

        return telephonyManager.getNetworkType();
    }

    @JavascriptInterface
    public String getSubscriberName() {
        if (context == null) {
            return "";
        }

        String imsi = getImsi();

        if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
            return "移动";
        } else if (imsi.startsWith("46001")) {
            return "联通";
        } else if (imsi.startsWith("46003")) {
            return "电信";
        } else {
            return "未知";
        }
    }

    @JavascriptInterface
    public String getNetworkName() {
        if (context == null) {
            return "";
        }

        int type = getNetworkType();

        String typeName = "";
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                typeName = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                typeName = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                break;
        }
        return getSubscriberName() + " " + typeName;
    }

    private static String MAC_ADDR_FROM_WIFI = null;

    @JavascriptInterface
    public String getWifiMac() {
        if (context == null) {
            return "";
        }

        if (MAC_ADDR_FROM_WIFI != null)
            return MAC_ADDR_FROM_WIFI;

        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr == null)
            return getMacAddressFromCmd();

        WifiInfo info = wifiMgr.getConnectionInfo();
        if (info == null)
            return getMacAddressFromCmd();

        String macAddr = info.getMacAddress();
        if (macAddr == null || macAddr.length() != 17)
            return getMacAddressFromCmd();

        MAC_ADDR_FROM_WIFI = macAddr;
        return MAC_ADDR_FROM_WIFI;
    }

    private static String MAC_ADDR_FROM_CMD = null;

    private static String getMacAddressFromCmd() {
        if (MAC_ADDR_FROM_CMD != null)
            return MAC_ADDR_FROM_CMD;

        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String str = "";
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    MAC_ADDR_FROM_CMD = str.trim();
                    break;
                }
            }
        } catch (IOException ignored) {
        }
        return MAC_ADDR_FROM_CMD;
    }




    static class Installation {
        private static String sID = null;
        private static final String INSTALLATION = "YOUYUN_INSTALLATION";

        public synchronized static String id(Context context) {
            if (sID == null) {
                File installation = new File(context.getFilesDir(), INSTALLATION);
                try {
                    if (!installation.exists())
                        writeInstallationFile(installation);
                    sID = readInstallationFile(installation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return sID;
        }

        private static String readInstallationFile(File installation) throws IOException {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }

        private static void writeInstallationFile(File installation) throws IOException {
            FileOutputStream out = new FileOutputStream(installation);
            String id = UUID.randomUUID().toString();
            out.write(id.getBytes());
            out.close();
        }


    }

    public CharSequence getAppInfo(Activity activity) {
        try {
            String pkName = activity.getPackageName();
            String versionName = activity.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            int versionCode = activity.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
            return pkName + "   " + versionName + "  " + versionCode;
        } catch (Exception e) {
        }
        return null;
    }
}