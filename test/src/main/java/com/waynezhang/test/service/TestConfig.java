package com.waynezhang.test.service;

import java.text.SimpleDateFormat;

/**
 * Created by don on 12/12/14.
 */
public class TestConfig {
    public static final String BASE_URL = "http://ymm123.sdo.com";
    public static final String APP_ID = "791000123";
    public static final String GCHAT_APP_ID = "991001023";
    public static final String GCHAT_AREA_ID = "0";
    public static final String GCHAT_APPKEY = "9728949f9d3c43ba84acb5f6a4da7dd8";
    public static final String SRC_CODE = "10";
    public static final String SMS_TEMPLATE = ".*?(?:游麦麦|G买卖)短信验证码:(.*?)，.*";
    public static final String Ex = "^[0-9_a-zA-Z]{6,30}$";
    public static final String SPLASH_KEY = "mhh_splash_prefrence";
    public static final String SPLASH_WIDTH = "mhh_splash_width";
    public static final String SPLASH_HEIGHT = "mhh_splash_height";
    public static final String SIGN_OPEN_DATE = "mhh_sign_open_date";
    public static final String RECOMMEND_CONFIG = "recommend_config";
    public static final String HONG_JIN_BAO_NEW_MSG_FLAG_KEY = "hong_jin_bao_new_msg_flag_key";
    public static final int SPLASH_TIME = 3 * 1000;
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static final String MESSAGE_CLOSETIME_TAG_KEY = "close_time";
    public static final String EXPLANE_CLOSE_TAG_KEY = "explane_close_time";
    public static String GRPORT_LOG_URL = "http://gmmreport.sdo.com/report/ge/batch";
    public static String GRPORT_CRASH_URL = "http://gmmreport.sdo.com/report/ge/crash/";
    public static final String GRPORT_ANR_URL = "http://reportsk.sdo.com/report/other/anr/";
    public static final String CRASH_REPORT_URL = "http://reportsk.sdo.com/report/other/crashfile/";
    public static String AliPay_URL = "http://cdndownload.alipay.com/mobilesp/android/5.1.0/20140730114811alipay-newmsp-5.1.0-pro-1-201403011456.apk";
}

