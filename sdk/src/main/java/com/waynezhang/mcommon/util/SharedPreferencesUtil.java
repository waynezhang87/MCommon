package com.waynezhang.mcommon.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesUtil {

	public static void setSharedPreferences(Context ctx, String key, String value) {
		if (ctx == null || key == null || value == null)
			return;
		SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getSharedPreferencesValue(Context ctx, String key, String defValue) {
		if (ctx == null || key == null || defValue == null)
			return "";
		SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key, defValue);
	}

	public static void setSharedPreferences(Context ctx, String key, boolean value) {
		if (ctx == null || key == null)
			return;
		SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getSharedPreferencesValue(Context ctx, String key, boolean defValue) {
		if (ctx == null || key == null)
			return false;
		SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getBoolean(key, defValue);
	}

    public static void setSharedPreferences(Context ctx, String key, long value) {
        if (ctx == null || key == null)
            return;
        SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

	public static void removeSharedPreferences(Context ctx, String key)
	{
		if (ctx == null || key == null)
			return;
		SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

    public static long getSharedPreferencesValue(Context ctx, String key, long defValue) {
        if (ctx == null || key == null)
            return 0;
        SharedPreferences sp = (SharedPreferences) PreferenceManager.getDefaultSharedPreferences(ctx);
        return sp.getLong(key, defValue);
    }
}
