package com.waynezhang.mcommon.util;

import android.net.Uri;

import java.util.UUID;

/**
 * Created by liuyagang on 15-6-23.
 */
public class UrlUtil {
    public static boolean isHttpUrl(String url) {
        url = url.toLowerCase();
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static boolean isFtpUrl(String url) {
        url = url.toLowerCase();
        return url.startsWith("ftp://");
    }

    public static String getDomain(String url) {
        Uri uri = Uri.parse(url);
        return uri.getHost();
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String url) {
        String filename = url.substring(url.lastIndexOf('/') + 1);
        if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
            filename = UUID.randomUUID() + ".tmp";// 默认取一个文件名
        }
        return filename;
    }
}
