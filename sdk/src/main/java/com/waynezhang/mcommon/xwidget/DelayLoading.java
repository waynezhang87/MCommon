package com.waynezhang.mcommon.xwidget;

import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;

import com.waynezhang.mcommon.support.Safeguard;
import com.waynezhang.mcommon.util.L;

/**
 * Created by liuyagang on 15-2-4.
 * <p/>
 * 延迟显示Loading，通常大部分接口会非常快速的返回，这种情况下如果显示Loading会一闪而过，体验比较差，
 * 因此使用该工具类来延迟显示Loading如果响应很快则不会显示Loading
 */
public class DelayLoading {
    private static final String TAG = DelayLoading.class.getSimpleName();

    private static int delay = 300; // ms
    private Dialog progressDialog;
    private Handler uiThreadHandler;
    private Runnable callback;
    private int refCount = 0;

    public static void setDelay(int ms) {
        delay = ms;
    }

    public DelayLoading(Dialog progressDialog) {
        this.progressDialog = progressDialog;
        uiThreadHandler = new Handler(progressDialog.getContext().getMainLooper());
    }

    public void show() {
        refCount += 1;
        L.d(TAG, "show loading wait, reference count " + refCount);
        // 已经显示Loading，直接返回
        if (refCount > 1) {
            return;
        }
        uiThreadHandler.postDelayed(callback = new Runnable() {

            @Override
            public void run() {
                L.d(TAG, "showLoading, reference count " + refCount);
                if (Safeguard.ignorable(progressDialog.getContext())) {
                    return;
                }
                progressDialog.show();
                progressDialog.getWindow().setGravity(Gravity.CENTER);
            }

        }, delay);
    }

    public void hide() {
        refCount--;
        if (refCount <= 0) {
            refCount = 0;

            uiThreadHandler.removeCallbacks(callback);
            if (progressDialog != null && progressDialog.isShowing()) {
                L.d(TAG, "hideLoading, reference count " + refCount);

                progressDialog.dismiss();
            }
        } else {
            L.d(TAG, "cancel loading, reference count " + refCount);
        }
    }
}
