package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.waynezhang.mcommon.support.image.selector.ImageSelectorFragment;

import java.io.File;
import java.io.FileInputStream;

/**
 * 图片选择器
 * <p/>
 * 使用方法：需要在onActivityResult中接收返回来的图片路径
 * protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * if (requestCode == ImageSelecter.GET_PIC_FROM_LOCAL) {
 * ImageSelecter.getFilePathFromContentUri(data.getData(), getContentResolver());   // 本地图片返回的图片路径
 * } else if(requestCode == ImageSelecter.TAKE_PHONE) {
 * ImageSelecter.getPhotoImagePath();   // 照相返回的图片路径
 * }
 * }
 */
public class ImageSelecter {
    public static final int GET_PIC_FROM_LOCAL = 1;
    public static final int TAKE_PHONE = 2;
    public static final int CROP_IMAGE = 3;
    public static final int MULTI_PIC_FROM_LOCAL = 4;
    private static final File tmpPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "imgselected_photo");
    private static final File cropPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "imgselected_photo_crop");

    /**
     * 取本地图片
     *
     * @param activity
     */
    public static void imageFromLocal(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //4.4强制控制到图库上
        intent.setType("image/*");
        activity.startActivityForResult(intent, GET_PIC_FROM_LOCAL);
    }

    public static void imageFromLocal(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);  //4.4强制控制到图库上
        intent.setType("image/*");
        fragment.startActivityForResult(intent, GET_PIC_FROM_LOCAL);
    }

    /**
     * 取本地图片 多选
     *
     * @param activity
     */
    public static void multiImageFromLocal(Activity activity, int maxCount) {
        ImageSelectorFragment.go(activity, MULTI_PIC_FROM_LOCAL, maxCount);
    }

    public static void multiImageFromLocal(Fragment fragment, int maxCount) {
        ImageSelectorFragment.go(fragment, MULTI_PIC_FROM_LOCAL, maxCount);
    }

    /**
     * 照相图片
     *
     * @param activity
     */
    public static void imageFromPhoto(Activity activity) {
        imageFromPhoto((Object) activity);
    }

    public static void imageFromPhoto(Fragment fragment) {
        imageFromPhoto((Object) fragment);
    }

    private static void imageFromPhoto(Object context) {
        try {
            Uri mPhotoUri = Uri.fromFile(tmpPhotoFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(cameraIntent, TAKE_PHONE);
            } else if (context instanceof Fragment) {
                ((Fragment) context).startActivityForResult(cameraIntent, TAKE_PHONE);
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 仅供从相机图片的路径
     */
    public static String getPhotoImagePath() {
        return tmpPhotoFile.getPath();
    }

    /**
     * 获取照相已处理压缩、转角的图片路径
     *
     * @return
     */
    public static String getPhotoImagePathWithRotate(Activity activity) {
        String path = tmpPhotoFile.getPath();
        BitmapUtil.resizeAndSolveRotate(path);
        return path;
    }

    /**
     * 仅供从本地获取图片使用
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 仅供从本地获取图片使用
     */
    public static String getFilePath4Local(Context context, Intent data) {
        String filePath;
        if (data.getType() == null) {
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        } else {
            return data.getData().getPath();
        }
    }

    public static void cropImage(Activity activity, String filePath) {
        cropImage(activity, filePath, true, 200, 200);
    }

    public static void cropImage(Fragment fragment, String filePath) {
        cropImage(fragment, filePath, true, 200, 200);
    }

    public static void cropImage(Fragment fragment, String filePath, boolean keepAspect) {
        cropImage(fragment, filePath, keepAspect, -1, -1);
    }

    public static void cropImage(Activity activity, String filePath, boolean keepAspect) {
        cropImage(activity, filePath, keepAspect, -1, -1);
    }

    public static void cropImage(Activity activity, String filePath, boolean keepAspect, int width, int height) {
        cropImage((Object) activity, filePath, keepAspect, width, height);
    }

    public static void cropImage(Fragment fragment, String filePath, boolean keepAspect, int width, int height) {
        cropImage((Object) fragment, filePath, keepAspect, width, height);
    }

    private static void cropImage(Object content, String filePath, boolean keepAspect, int width, int height) {
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        if (keepAspect) {
            intent.putExtra("aspectX", 1);// 裁剪框比例
            intent.putExtra("aspectY", 1);
        }
        if (width != -1) {
            intent.putExtra("outputX", width);// 输出图片大小
        }
        if (height != -1) {
            intent.putExtra("outputY", height);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cropPhotoFile));
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        if (content instanceof Fragment) {
            ((Fragment) content).startActivityForResult(intent, CROP_IMAGE);
        } else if (content instanceof Activity) {
            ((Activity) content).startActivityForResult(intent, CROP_IMAGE);
        }
    }

    public static String getCropImagePath(Intent data) {
        return cropPhotoFile.getPath();
    }

    /**
     * 加载本地图片
     *
     * @param path
     * @return
     */
    public static Bitmap getLocalBitmap(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
            fis.close();
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
