package com.waynezhang.mcommon.xwidget;

import android.content.Context;
import android.widget.Toast;

import com.waynezhang.mcommon.BuildConfig;
import com.waynezhang.mcommon.support.ContextReference;

/**
 * Created by liuyagang on 15-4-9.
 */
public class ToastUtil {
    public static void showToast(CharSequence msg) {
        if (ContextReference.getContext() != null) {
            Toast.makeText(ContextReference.getContext(), msg, Toast.LENGTH_SHORT).show();
        } else if (BuildConfig.DEBUG) {
            throw new IllegalArgumentException("Application context was invalid, please check ContextReference.setContext has called.");
        }
    }

    public static void showToastLong(CharSequence msg) {
        if (ContextReference.getContext() != null) {
            Toast.makeText(ContextReference.getContext(), msg, Toast.LENGTH_LONG).show();
        } else if (BuildConfig.DEBUG) {
            throw new IllegalArgumentException("Application context was invalid, please check ContextReference.setContext has called.");
        }
    }

    public static void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToastLong(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
