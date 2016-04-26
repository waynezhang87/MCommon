package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.FragmentActivity;

/**
 * Created by waynezhang on 12/2/15.
 * 获取context对应的activity
 */
public class ContextUtil {
    public static FragmentActivity scanForFragmentActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof FragmentActivity)
            return (FragmentActivity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForFragmentActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}
