/*
 * Copyright (c) 2013 Shanda Corporation. All rights reserved.
 *
 * Created on 2013-10-28.
 */

package com.waynezhang.mcommon.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsHelper {

    private static final String TAG = "SmsHelper";
    /**
     * 所有的短信.
     */
    public static final Uri SMS_URI_ALL = Uri.parse("content://sms/");

    private static final String[] PROJECTION = new String[]{"_id", "address", "person", "body", "date", "type"};
    private static final String SELECTION = "_id > ?";
    private static final String SORT_ORDER = "date desc";

    private SmsCallback callback;
    private SmsObserver observer;
    private static SmsHelper smsHelper;
    private static String template;
    // 短信处理
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            SmsInfo smsInfo = (SmsInfo) msg.obj;
            Pattern pattern = Pattern.compile(template);
            Matcher matcher = pattern.matcher(smsInfo.getSmsbody());
            if (matcher.find()) {
                callback.verifyCode(matcher.group(1));
            }
        }
    };

    private SmsHelper(SmsCallback callback) {
        this.callback = callback;
    }

    public static SmsHelper getInstance(String template, SmsCallback callback) {
        if (null == smsHelper) {
            smsHelper = new SmsHelper(callback);
        }
        SmsHelper.template = template;
        return smsHelper;
    }

    /**
     * 获取所有的短信.
     *
     * @param resolver
     * @param count
     * @param startId
     * @return
     */
    public static List<SmsInfo> getAll(ContentResolver resolver, int count, int startId) {
        return getSmsInfo(resolver, SMS_URI_ALL, count, startId);
    }

    /**
     * 获取短信.
     *
     * @param resolver
     * @param uri
     * @param count
     * @param startId
     * @return
     */
    public static List<SmsInfo> getSmsInfo(ContentResolver resolver, Uri uri, int count, int startId) {
        List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
        try {
            Cursor cursor = resolver.query(uri, PROJECTION, SELECTION, new String[]{String.valueOf(startId)}, SORT_ORDER);
            int id = cursor.getColumnIndex("_id");
            int phoneNumberColumn = cursor.getColumnIndex("address");
            int nameColumn = cursor.getColumnIndex("person");
            int smsbodyColumn = cursor.getColumnIndex("body");
            int dateColumn = cursor.getColumnIndex("date");
            int typeColumn = cursor.getColumnIndex("type");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    SmsInfo smsInfo = new SmsInfo();
                    smsInfo.setId(cursor.getInt(id));
                    smsInfo.setPhoneNumber(cursor.getString(phoneNumberColumn));
                    smsInfo.setName(cursor.getString(nameColumn));
                    smsInfo.setSmsbody(cursor.getString(smsbodyColumn));
                    smsInfo.setDate(cursor.getString(dateColumn));
                    smsInfo.setType(cursor.getString(typeColumn));
                    smsInfos.add(smsInfo);

                    //dumpSmsInfo(cursor);

                    if (smsInfos.size() >= count) {
                        break;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return smsInfos;
    }

    private static void dumpSmsInfo(Cursor cursor) {
        Log.v("_id", "" + cursor.getLong(0));
        Log.v("thread_id", "" + cursor.getLong(1));
        Log.v("address", "" + cursor.getString(2));
        Log.v("person", "" + cursor.getString(3));
        Log.v("date", "" + cursor.getString(4));
        Log.v("protocol", "" + cursor.getString(5));
        Log.v("read", "" + cursor.getString(6));
        Log.v("status", "" + cursor.getString(7));
        Log.v("type", "" + cursor.getInt(8));
        Log.v("reply_path_present", "" + cursor.getString(9));
        Log.v("subject", "" + cursor.getString(10));
        Log.v("body", "" + cursor.getString(11));
        Log.v("service_center", "" + cursor.getString(12));
        Log.v("locked", "" + cursor.getString(13));
        Log.v("error_code", "" + cursor.getString(14));
        Log.v("seen", "" + cursor.getString(15));
    }

    /**
     * 打开监听
     *
     * @param ctx
     */
    public void openSmsObserver(Context ctx) {
        L.v(TAG, "openSmsObserver");
        if (!PackageHelper.checkPermission(ctx, "android.permission.READ_SMS")) {
            L.e(TAG, "no read_sms permission registered in manifest file.");
            return;
        }
        ContentResolver resolver = ctx.getContentResolver();
        observer = new SmsObserver(mHandler, resolver);
        resolver.registerContentObserver(SMS_URI_ALL, true, observer);
    }

    /**
     * 关闭监听
     *
     * @param ctx
     */
    public void closeSmsObserver(Context ctx) {
        L.v(TAG, "closeSmsObserver");
        if (!PackageHelper.checkPermission(ctx, "android.permission.READ_SMS")) {
            return;
        }
        if (observer != null) {
            ctx.getContentResolver().unregisterContentObserver(observer);
            observer = null;
            smsHelper = null;
        }
    }

    public static class SmsInfo implements Serializable {

        private int id;

        /**
         * 发送短信的电话号码.
         */
        private String phoneNumber;

        /**
         * 发送短信人的姓名.
         */
        private String name;

        /**
         * 短信内容.
         */
        private String smsbody;

        /**
         * 发送短信的日期和时间.
         */
        private String date;

        /**
         * 短信类型: 1 是接收到的 2 是已发出.
         */
        private String type;

        @Override
        public String toString() {
            return "id[" + id + "] phoneNumber[" + phoneNumber + "] name[" + name + "] smsbody[" + smsbody + "] date[" + date + "] type[" + type + "]";
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSmsbody() {
            return smsbody;
        }

        public void setSmsbody(String smsbody) {
            this.smsbody = smsbody;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
