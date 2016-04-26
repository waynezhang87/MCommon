package com.waynezhang.mcommon.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.waynezhang.mcommon.network.Http;
import com.waynezhang.mcommon.network.ResultCode;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.lang.reflect.Field;

//import com.shandagames.greport.GReport;

/**
 * Created by liuxiaofeng02 on 2015/4/13.
 */
public class PicassoUtil {
    private static int PLACEHOLDER_RES_ID = 0;
    private static int ERROR_RES_ID = 0;

    public static void setPlaceholderResId(int resId){
        PLACEHOLDER_RES_ID = resId;
    }

    public static void setErrorResId(int resId){
        ERROR_RES_ID = resId;
    }

    public static void showWithSize(ImageView iv, Context context, final String url, int width, int height, int placeholder_id){
        if(TextUtils.isEmpty(url)){
            iv.setImageResource(placeholder_id);
            return;
        }

        final long startTime = System.currentTimeMillis();
        Picasso picasso = Picasso.with(context);
        picasso.setIndicatorsEnabled(false);
        picasso.load(url).tag(context).placeholder(placeholder_id).resize(width, height).centerCrop().into(iv, new Callback() {
            @Override
            public void onSuccess() {
                reportTimeCost(url, 0, startTime);
            }

            @Override
            public void onError() {
                reportTimeCost(url, ResultCode.NetworkException.nativeInt, startTime);
            }
        });
    }

    public static void show(ImageView iv, Context context, File file, int placeholder_id){
        Picasso picasso = Picasso.with(context);
        picasso.load(file).placeholder(placeholder_id).into(iv);
    }

    public static void show(ImageView iv, Context context, File file){
        Picasso picasso = Picasso.with(context);
        RequestCreator requestCreator = picasso.load(file);
        if(PLACEHOLDER_RES_ID != 0){
            requestCreator = requestCreator.placeholder(PLACEHOLDER_RES_ID);
        }
        if(ERROR_RES_ID != 0){
            requestCreator = requestCreator.error(ERROR_RES_ID);
        }
        requestCreator.into(iv);
    }

    public static void show(ImageView iv, Context context, int resId){
        Picasso picasso = Picasso.with(context);
        RequestCreator requestCreator = picasso.load(resId);
        if(PLACEHOLDER_RES_ID != 0){
            requestCreator = requestCreator.placeholder(PLACEHOLDER_RES_ID);
        }
        if(ERROR_RES_ID != 0){
            requestCreator = requestCreator.error(ERROR_RES_ID);
        }
        requestCreator.into(iv);
    }

    public static void show(ImageView iv, Context context, final String url){
        if(TextUtils.isEmpty(url)){
            return;
        }

        final long startTime = System.currentTimeMillis();
        Picasso picasso = Picasso.with(context);
        RequestCreator requestCreator = picasso.load(url).tag(context);
        if(PLACEHOLDER_RES_ID != 0){
            requestCreator = requestCreator.placeholder(PLACEHOLDER_RES_ID);
        }
        if(ERROR_RES_ID != 0){
            requestCreator = requestCreator.error(ERROR_RES_ID);
        }
        requestCreator.into(iv, new Callback() {
            @Override
            public void onSuccess() {
                reportTimeCost(url, 0, startTime);
            }

            @Override
            public void onError() {
                reportTimeCost(url, ResultCode.NetworkException.nativeInt, startTime);
            }
        });
    }

    public static void show(ImageView iv, Context context, final String url, int placeholder_id) {
        if(TextUtils.isEmpty(url)){
            return;
        }

        final long startTime = System.currentTimeMillis();
        Picasso picasso = Picasso.with(context);
        picasso.load(url).placeholder(placeholder_id).tag(context).into(iv, new Callback() {
            @Override
            public void onSuccess() {
                reportTimeCost(url, 0, startTime);
            }

            @Override
            public void onError() {
                reportTimeCost(url, ResultCode.NetworkException.nativeInt, startTime);
            }
        });
    }

    public static void show(ImageView iv, Context context, final String url, int placeholder_id, int error_res_id) {
        if(TextUtils.isEmpty(url)){
            return;
        }

        final long startTime = System.currentTimeMillis();
        Picasso picasso = Picasso.with(context);
        picasso.load(url).tag(context).placeholder(placeholder_id).error(error_res_id).into(iv, new Callback() {
            @Override
            public void onSuccess() {
                reportTimeCost(url, 0, startTime);
            }

            @Override
            public void onError() {
                reportTimeCost(url, ResultCode.NetworkException.nativeInt, startTime);
            }
        });
    }

    public static void show(ImageView iv, Context context, final String url, final Callback imgCallback){
        Picasso picasso = Picasso.with(context);
        if(TextUtils.isEmpty(url)){
            return ;
        }

        final long startTime = System.currentTimeMillis();
        picasso.load(url).tag(context).into(iv, new Callback() {
            @Override
            public void onSuccess() {
                imgCallback.onSuccess();
                reportTimeCost(url, 0, startTime);
            }

            @Override
            public void onError() {
                imgCallback.onError();
                reportTimeCost(url, ResultCode.NetworkException.nativeInt, startTime);
            }
        });
    }

    private static void reportTimeCost(String url, int exceptionCode, long startTime){
        Http.reportTimeCost(url, 0, startTime, 0, exceptionCode);
    }

    public static void cancelTag(Context context){
        Picasso picasso = Picasso.with(context);
        picasso.cancelTag(context);
    }

    public static void cancelTag(Context context, Object tag){
        Picasso picasso = Picasso.with(context);
        picasso.cancelTag(tag);
    }

    public static String getCachedImageFilePath(Context context, String key) {
        try {
            Class.forName("com.squareup.okhttp.OkHttpClient");
            Field field = com.squareup.okhttp.Cache.class.getDeclaredField("ENTRY_BODY");
            field.setAccessible(true);
            String suffix = field.get(null).toString();
            File cacheDir = new File(context.getApplicationContext().getCacheDir(), "picasso-cache");
            String fileName = cacheDir.getPath() + File.separator + PicassoTools.getOkHttpKey(key) + "." + suffix;
            return fileName;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
