package com.waynezhang.mcommon.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 * Created by don on 1/8/15.
 */
public final class ScreenUtil {

    public static int convertDipToPixel(Context context, float dip) {
        return dip2px(context, dip);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    private static int statusBarHeight = -1;

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight != -1)
            return statusBarHeight;

        try {
            Class<?> c_R_dimen = Class.forName("com.android.internal.R$dimen");
            Object o_R_dimen = c_R_dimen.newInstance();
            Field f_status_bar_height = c_R_dimen.getField("status_bar_height");
            int dp = Integer.parseInt(f_status_bar_height.get(o_R_dimen).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(dp);
            return statusBarHeight;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager manager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return manager.inKeyguardRestrictedInputMode();
    }
}
