package com.waynezhang.mcommon.util;

import android.content.Context;
import android.os.Handler;

public class ThreadUtil {
	public static void runOnUiThread(Context context, Runnable runnable) {
		if (Thread.currentThread() != context.getMainLooper().getThread()) {
			Handler handler = new Handler(context.getMainLooper());
			handler.post(runnable);
		} else {
			runnable.run();
		}
	}
}
