package com.waynezhang.mcommon.util;

import java.util.List;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 游云短信监听.
 *
 * @author James Shen
 */
public class SmsObserver extends ContentObserver {

    private static final String TAG = SmsObserver.class.getSimpleName();

    private static final int MAX_COUNT = 1;
    private static int START_ID = 0;

    private ContentResolver mResolver;
    private Handler mHandler;

    public SmsObserver(Handler handler, ContentResolver resolver) {
        super(handler);
        this.mResolver = resolver;
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        try {
            Message message;
            List<SmsHelper.SmsInfo> all = SmsHelper.getAll(mResolver, MAX_COUNT, START_ID);
            for (SmsHelper.SmsInfo smsInfo : all) {
                setStartId(smsInfo.getId());
                message = new Message();
                message.obj = smsInfo;
                Log.d(TAG, "read sms: " + smsInfo.toString());
                mHandler.sendMessage(message);
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
    }

    private void setStartId(int id) {
        if (id > START_ID) {
            START_ID = id;
        }
    }
}
