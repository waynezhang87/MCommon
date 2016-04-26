package com.waynezhang.mcommon.template.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.waynezhang.mcommon.R;
import com.waynezhang.mcommon.activity.RequestCode;
import com.waynezhang.mcommon.support.image.add.BigImageFragment;
import com.waynezhang.mcommon.template.interfaces.TemplateImgGoOtherCallback;
import com.waynezhang.mcommon.util.ImageSelecter;
import com.waynezhang.mcommon.util.L;

import java.util.ArrayList;

/**
 * Created by don on 1/25/16.
 */
public class BridgeActivity extends FragmentActivity {

    public static final int IMG_TAKE_PHOTO = 1;
    public static final int IMG_MULTI = 2;
    public static final int BIG_IMAGE = 3;


    static TemplateImgGoOtherCallback templateImgGoOtherCallback;
    static int mcurrentImageIndex;
    static ArrayList mimageList;
    static int EXTRA;
    static int mmaxCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bridge);
        switch (EXTRA) {
            case IMG_TAKE_PHOTO:
                imageFromPhoto();
                break;
            case BIG_IMAGE:
                BigImageFragment.go(BridgeActivity.this,mcurrentImageIndex,mimageList);
                break;
            case IMG_MULTI:
                ImageSelecter.multiImageFromLocal(BridgeActivity.this, mmaxCount);
                break;
        }
    }

    public static void goImageSelector(Activity from, TemplateImgGoOtherCallback callback){
        EXTRA = IMG_TAKE_PHOTO;
        templateImgGoOtherCallback = callback;
        from.startActivity(new Intent(from, BridgeActivity.class));
    }

    public static void goBigImageFragment(Activity from, TemplateImgGoOtherCallback callback, int currentImageIndex, ArrayList imageList){
        EXTRA = BIG_IMAGE;
        mcurrentImageIndex = currentImageIndex;
        mimageList = imageList;
        templateImgGoOtherCallback = callback;
        from.startActivity(new Intent(from, BridgeActivity.class));
    }

    public static void goMultiImageSelector(Activity from,int maxCount, TemplateImgGoOtherCallback callback){
        EXTRA = IMG_MULTI;
        mmaxCount = maxCount;
        templateImgGoOtherCallback = callback;
        from.startActivity(new Intent(from, BridgeActivity.class));
    }


    /**
     * 拍照
     *
     * @param
     */
    public void imageFromPhoto() {
        ImageSelecter.imageFromPhoto(BridgeActivity.this);
    }


    /**
     * 取本地图片
     *
     * @param
     */
    public void imageFromLocal() {
        ImageSelecter.imageFromLocal(BridgeActivity.this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        L.d("onActivityResult", "requestCode=" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }

        if (requestCode == ImageSelecter.TAKE_PHONE) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            templateImgGoOtherCallback.take_phone(requestCode,resultCode,data);
        } else if (requestCode == ImageSelecter.MULTI_PIC_FROM_LOCAL) {
            //把添加图片按钮移动到最后一个位置（先删除，后添加）
            templateImgGoOtherCallback.multi_pic_from_local(requestCode,resultCode,data);
        } else if(requestCode == RequestCode.BIG_IMAGE){
            templateImgGoOtherCallback.big_image(requestCode,resultCode,data);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        templateImgGoOtherCallback = null;
        mimageList = null;
        super.onDestroy();

    }
}
