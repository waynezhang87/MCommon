package com.waynezhang.mcommon.cache.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.waynezhang.mcommon.security.MD5;
import com.waynezhang.mcommon.util.CacheUtil;
import com.waynezhang.mcommon.util.L;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by waynezhang on 5/7/15.
 */
public class DiskLruImageCache {
    public static final String TAG = DiskLruImageCache.class.getSimpleName();

    private DiskLruCache mDiskCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int mCompressQuality = 70;
    private static final int APP_VERSION = 201504;
    private static final int VALUE_COUNT = 1;

    public DiskLruImageCache(File diskCacheDir, long diskCacheSize) {
        try {
            mDiskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
//            mCompressFormat = compressFormat;
//            mCompressQuality = quality;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor)
            throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), CacheUtil.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, mCompressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public void put(String keyMD5, Bitmap data) {
        DiskLruCache.Editor editor = null;
        try {
            editor = mDiskCache.edit(keyMD5);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data, editor)) {
                mDiskCache.flush();
                editor.commit();
                L.d(TAG, "image put on disk cache " + keyMD5);
            } else {
                editor.abort();
                L.d(TAG, "ERROR on: image put on disk cache " + keyMD5);
            }
        } catch (IOException e) {
            L.d(TAG, "ERROR on: image put on disk cache " + keyMD5);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }

    }

    public Bitmap getBitmap(String key) {
        String keyMD5 = MD5.digest(key);
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get(keyMD5);
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, CacheUtil.IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        L.d(TAG, bitmap == null ? "" : "image read from disk " + keyMD5);

        return bitmap;

    }

    public boolean containsKey(String key) {

        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = mDiskCache.get(key);
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return contained;

    }

    public void clearCache() {
        L.d(TAG, "disk cache CLEARED");
        try {
            mDiskCache.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteIfExist(String key) {
        String keyMD5 = MD5.digest(key);
        File path = new File(getCacheFolder() + File.separator + keyMD5);
        try {
            mDiskCache.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getCacheFolder() {
        return mDiskCache.getDirectory();
    }

}
