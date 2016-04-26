package com.waynezhang.mcommon.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtil {
    private static final String TAG = "BitmapUtil";
    private static final int JPEG_QUALITY_DEFAULT = 50;

    //TODO delete
    public static String save2file(Activity activity, Bitmap b) {
        File f = activity.getCacheDir();
        File dir = new File(f, "/mhh/pics");
        if (dir.isFile()) {
            dir.delete();
        }
        if (!dir.isDirectory()) {
            dir.mkdirs();
        } else {
            File[] fs = dir.listFiles();
            if (fs != null && fs.length > 0) {
                for (File del : fs) {
                    if (del.exists()) {
                        del.delete();
                    }
                }
            }
        }
        File save = new File(dir, "" + System.currentTimeMillis());
        OutputStream out = null;
        try {
            out = new FileOutputStream(save, false);
            b.compress(Bitmap.CompressFormat.JPEG, 75, out);
            L.v(TAG, "save pic at " + save.getAbsolutePath());
            return save.getAbsolutePath();
        } catch (IOException e) {
            L.e(TAG, "e", e);
            return null;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    L.e(TAG, "e", e);
                }
            }
        }
    }

    //TODO delete
    public static boolean save2file(Bitmap bmap, File saveFile) {
        // 图像保存到文件中
        FileOutputStream foutput = null;
        try {
            foutput = new FileOutputStream(saveFile);
            bmap.compress(Bitmap.CompressFormat.JPEG, 100, foutput);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != foutput) {
                try {
                    foutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 等比例缩放图片，保证横图不超过1920*1200，竖图不超过1200*1920
     */
    public static boolean getSmallImage(String srcPath, String destPath) {
        Bitmap bitmap = getSmallImage(srcPath);
        if (bitmap == null) {
            return false;
        }
        return saveJpg(bitmap, destPath);
    }

    /**
     * 等比例缩放图片，保证横图不超过1920*1200，竖图不超过1200*1920
     */
    public static Bitmap getSmallImage(String srcPath) {
        File file = new File(srcPath);
        if (!file.exists()) {
            L.e(TAG, "File[" + srcPath + "] does not exist!");
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);

        int reqWidth = 0;
        int reqHeight = 0;
        if (options.outHeight >= options.outWidth) {
            reqWidth = 1200;
            reqHeight = 1920;
        } else {
            reqWidth = 1920;
            reqHeight = 1200;
        }
        return resizeImage(srcPath, reqWidth, reqHeight);
    }

    /**
     * 如果图片角度不对，自动旋转到正确的角度
     * 如果图片大小太大，自动按比例缩小
     */
    public static boolean resizeAndSolveRotate(String path) {
        return resizeAndSolveRotate(path, path);
    }

    /**
     * 如果图片角度不对，自动旋转到正确的角度
     * 如果图片大小太大，自动按比例缩小
     */
    public static boolean resizeAndSolveRotate(String path, String destPath) {
        int degree = BitmapUtil.getImageRotateDegree(path);
        Bitmap bitmap = BitmapUtil.getSmallImage(path);
        if (bitmap == null) {
            return false;
        }
        if (degree != 0) {
            bitmap = BitmapUtil.rotateImage(bitmap, degree);
            if (bitmap == null) {
                return false;
            }
        }
        BitmapUtil.saveJpg(bitmap, destPath);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return true;
    }

    /**
     * 计算图片缩放之后的尺寸（图片保持长宽比例缩放，宽度不超过maxWidth，高度不超过maxHeight）
     */
    public static Point calcScaledSize(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float scale = calcScale(options.outWidth, options.outHeight, maxWidth, maxHeight);
        return new Point((int) (options.outWidth * scale), (int) (options.outHeight * scale));
    }

    /**
     * 计算图片缩放比例
     */
    private static float calcScale(int srcWidht, int srcHeight, int destWidht, int destHeight) {
        if (srcWidht > destWidht || srcHeight > destHeight) {
            // 计算出实际宽度和目标宽度的比率
            float widthRatio = (float) destWidht / srcWidht;
            float heightRatio = (float) destHeight / srcHeight;
            return Math.min(widthRatio, heightRatio);
        }
        return 1.0f;
    }

    /**
     * 调整图片尺寸，如果图片尺寸超过maxWidth或者maxHeight则等比例缩小图片，使图片尺寸不超过maxWidth和maxHeight
     */
    public static Bitmap resizeImage(String srcPath, int maxWidth, int maxHeight) {
        File file = new File(srcPath);
        if (!file.exists()) {
            L.e(TAG, "File[" + srcPath + "] does not exist!");
            return null;
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(srcPath, options);
            float scale = calcScale(options.outWidth, options.outHeight, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inScaled = true;
            options.inDensity = 240;
            options.inTargetDensity = (int) (240 * scale);
            return BitmapFactory.decodeFile(srcPath, options);
        } catch (OutOfMemoryError e) {
            L.e(TAG, "outofmemory");
            return null;
        }
    }

    /**
     * 根据图片Exif信息对图片进行旋转（三星手机拍照时角度信息记录在Exif中，图片并没有实际旋转，这里根据Exif信息旋转到实际角度）
     *
     * @return
     */
    public static boolean solveDegree(String path) {
        int degree = getImageRotateDegree(path);
        if (degree == 0) {
            return true;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) {
            return false;
        }
        bitmap = rotateImage(bitmap, degree);
        if (bitmap == null) {
            return false;
        } else {
            saveJpg(bitmap, path);
            bitmap.recycle();
            return true;
        }
    }

    /**
     * 根据给定角度，对图片进行转动
     *
     * @return
     */
    public static Bitmap rotateImage(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix mtx = new Matrix();
        mtx.postRotate(degree);
        Bitmap rotatedBMP = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        if (!rotatedBMP.equals(bitmap)) {
            bitmap.recycle();
        }
        return rotatedBMP;
    }

    /**
     * 获取转角
     * <p/>
     * ExifInterface类主要描述多媒体文件比如JPG格式图片的一些附加信息，比如拍照的设备厂商，当时的日期时间，曝光时间，快门速度等
     *
     * @param filepath 图片路径
     * @return
     */
    public static int getImageRotateDegree(String filepath) {
        int orientation = -1;
        try {
            ExifInterface exif = new ExifInterface(filepath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        } catch (IOException e) {
        }
        int degree = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    public static boolean saveJpg(Bitmap bitmap, String fileName) {
        return saveJpg(bitmap, fileName, JPEG_QUALITY_DEFAULT);
    }

    public static boolean saveJpg(Bitmap bitmap, String fileName, int quality) {
        return saveImage(bitmap, fileName, Bitmap.CompressFormat.JPEG, quality);
    }

    public static boolean savePng(Bitmap bitmap, String fileName) {
        return saveImage(bitmap, fileName, Bitmap.CompressFormat.PNG, 100);
    }

    public static boolean saveImage(Bitmap bitmap, String fileName, Bitmap.CompressFormat format, int quality) {
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(new File(fileName));
            bitmap.compress(format, quality, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static byte[] getJpegBytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY_DEFAULT, baos);
        return baos.toByteArray();
    }

    public static Bitmap DrawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }
}
