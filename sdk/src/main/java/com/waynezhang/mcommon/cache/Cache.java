package com.waynezhang.mcommon.cache;

import android.content.Context;
import android.graphics.Bitmap;

import com.waynezhang.mcommon.cache.entity.BitmapLruCache;
import com.waynezhang.mcommon.cache.entity.DbCache;
import com.waynezhang.mcommon.cache.entity.DiskLruImageCache;
import com.waynezhang.mcommon.cache.entity.NetworkInfo;
import com.waynezhang.mcommon.cache.entity.StringLruCache;
import com.waynezhang.mcommon.util.CacheUtil;
import com.waynezhang.mcommon.util.L;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The global default {@link Cache} instance.
 * <p>
 * This instance is automatically initialized with defaults that are suitable to most
 * implementations.
 * <ul>
 * <li>LRU memory cache of 15% the available application RAM</li>
 * <li>Disk cache of 2% storage space up to 50MB but no less than 5MB. (Note: this is only
 * available on API 14+ <em>or</em> if you are using a standalone library that provides a disk
 * cache on all API levels like OkHttp)</li>
 * <li>Three download threads for disk and network access.</li>
 * </ul>
 * <p>
 * If these settings do not meet the requirements of your application you can construct your own
 * with full control over the configuration by using {@link Cache.Builder} to create a
 * {@link Cache} instance. You can either use this directly or by setting it as the global
 */
public class Cache implements CacheInterface {
    public static final String TAG = Cache.class.getSimpleName();

    private static final int VERSION = 201504;
    private static final int ENTRY_COUNT = 2;
    private static final int VALUE_IDX = 0;
    private static final int METADATA_IDX = 1;

    private BitmapLruCache mBitmapCache;
    private StringLruCache mStringCache;
    private com.squareup.okhttp.Cache mOkhttpDiskCache;
    private DiskLruImageCache mCustomDiskCache;
    private DbCache mDbCache;
    private ConcurrentHashMap<String, Integer> expTimeMap;
    private int maxBitmapCacheSize;
    private int maxStringCacheSize;
    private boolean isOkhttp;
    public static Cache instance;

    private Cache(Builder builder) {
        this.mOkhttpDiskCache = builder.okhttpDiskCache;
        this.mCustomDiskCache = builder.customDiskCache;
        this.mBitmapCache = builder.bitmapCache;
        this.mDbCache = builder.dbCache;
        this.mStringCache = builder.stringCache;
        this.maxBitmapCacheSize = builder.maxBitmapCacheSize;
        this.maxStringCacheSize = builder.maxStringCacheSize;
        this.expTimeMap = new ConcurrentHashMap<>();
        this.isOkhttp = builder.isOkhttp;
        instance = this;
    }

    // Added by waynezhang on 11/23/15: Cache类hold着一个globle instance, 只能使用Builder创建一次
    public static Builder getBuilder(Context context) {
        if (instance != null) {
            throw new IllegalArgumentException("Cache instance has been built.");
        } else {
            return new Builder(context);
        }
    }

    // Added by waynezhang on 11/23/15: 获取当前globle instance, 如果返回空, 表示Cache未被全局创建过
    public static Cache getInstance() {
        if (instance == null) {
            throw new IllegalArgumentException("Cache instance has not been build yet.");
        }
        return instance;
    }

    // Added by waynezhang on 11/23/15: 使用默认配置初始化Cache
    public static Cache newInstance(Context context) throws Exception {
        if (instance == null) {
            return getBuilder(context).build();
        }
        return instance;
    }

    @Override
    public void putBitmapCache(final String key, final Bitmap bitmap) {
        if (!isOkhttp) {
            mCustomDiskCache.put(PicassoTools.getOkHttpKey(key), bitmap);
        }
    }

    @Override
    public Bitmap getBitmapCache(String key) {
        Bitmap bitmap = mBitmapCache.get(PicassoTools.getKey(key));
        if (bitmap == null) {
            bitmap = mCustomDiskCache.getBitmap(PicassoTools.getOkHttpKey(key));
            if (bitmap == null) {
                L.i(TAG, "MemCache not hit, DiskCache not hit");
            } else {
                L.i(TAG, "MemCache not hit, DiskCache hit");
                mBitmapCache.set(PicassoTools.getKey(key), bitmap);
            }
        } else {
            L.i(TAG, "MemCache hit");
        }
        return bitmap;
    }

    public File getBitmapFile(String key) {
        try {
            Field field = com.squareup.okhttp.Cache.class.getDeclaredField("ENTRY_BODY");
            field.setAccessible(true);
            String suffix = field.get(null).toString();
            String fileName = mCustomDiskCache.getCacheFolder().getPath() + "/" +PicassoTools.getOkHttpKey(key) + "." + suffix;
            return new File(fileName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteBitmapCache(String key) {
        mBitmapCache.delete(PicassoTools.getKey(key));
        mCustomDiskCache.deleteIfExist(key);
    }

    @Override
    public void putCache(String key, String response) {
        mDbCache.put(key, response);
        NetworkInfo info = new NetworkInfo(key, response);
        mStringCache.set(key, info);
    }

    @Override
    public void putCache(String key, String response, int period) {
        mDbCache.put(key, response);
        NetworkInfo info = new NetworkInfo(key, response);
        mStringCache.set(key, info);
        expTimeMap.put(key, period);
    }

    @Override
    public String getCache(String key) {
        long expTime = Long.MAX_VALUE;
        Iterator it = expTimeMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry) it.next();
            if (key.equals(entry.getKey())) {
                expTime = ((long) entry.getValue()) * 1000;
            }
        }

        NetworkInfo response = mStringCache.get(key);
        if (response == null) {
            response = mDbCache.get(key);
            if (response != null) {
                if (isExpire(response.timeStamp, expTime)) {
                    L.i(TAG, "CacheData expire");
                    mDbCache.delete(key);
                    return null;
                } else {
                    L.i(TAG, "StringCache not hit, DbCache hit");
                    mStringCache.set(key, response);
                    return response.value;
                }
            } else {
                L.i(TAG, "StringCache not hit, DbCache not hit");
                return null;
            }
        } else {
            L.i(TAG, "StringCache hit");
            if (isExpire(response.timeStamp, expTime)) {
                L.i(TAG, "CacheData expire");
                mStringCache.delete(key);
                mDbCache.delete(key);
                return null;
            } else return response.value;
        }
    }

    private static boolean isExpire(String prevTime, long expTime) {
        if (expTime < Long.MAX_VALUE && System.currentTimeMillis() > (expTime + Long.valueOf(prevTime))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteCache(String key) {
        mStringCache.delete(key);
    }

    @Override
    public int memSize() {
        return mBitmapCache.size();
    }

    @Override
    public int memMaxSize() {
        return mBitmapCache.maxSize();
    }

    @Override
    public void clear() throws IOException {
        mBitmapCache.clear();
        mDbCache.clear();
        mStringCache.clear();
        mCustomDiskCache.clearCache();
    }

    @Override
    public void close() throws IOException {
        mBitmapCache.clear();
        mDbCache.close();
    }

    @Override
    public void clearInvalid() {

    }

    @Override
    public void resizeMem(long maxSize) {

    }

    public static class Builder {
        private final Context context;
        private BitmapLruCache bitmapCache;
        private StringLruCache stringCache;
        private com.squareup.okhttp.Cache okhttpDiskCache;
        private DiskLruImageCache customDiskCache;
        private DbCache dbCache;
        private OkHttpClient client;
        private File diskDirectory;
        private int maxBitmapCacheSize;
        private int maxStringCacheSize;
        private boolean isOkhttp = false;
        private boolean isPicasso = false;
        private Map<String, String> expireMap;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder diskCache(OkHttpClient client, File diskDir) throws IOException {
            if (client == null) {
                throw new IllegalArgumentException("OkHttpClient must not be null.");
            }
            if (diskDir == null) {
                this.diskDirectory = CacheUtil.getExternalCacheDir(context);
            } else {
                if (!diskDir.exists()) {
                    diskDir.mkdirs();
                }
                this.diskDirectory = diskDir;
            }
            this.client = client;
            isOkhttp = true;
            return this;
        }

        public Builder diskCache(File diskDir) {
            if (!isOkhttp) {
                if (diskDir == null) {
                    this.diskDirectory = CacheUtil.getExternalCacheDir(context);
                }
                if (!diskDir.exists()) {
                    diskDir.mkdirs();
                }
                this.diskDirectory = diskDir;
                return this;
            }
            return this;
        }

        public Builder stringCache(int maxSize) {
            if (maxSize <= 0) {
                throw new IllegalArgumentException("String cache max size <= 0.");
            }
            this.maxStringCacheSize = maxSize;
            stringCache = new StringLruCache(maxSize);
            return this;
        }

        public Builder picassoCache(boolean isPicasso) {
            this.isPicasso = isPicasso;
            return this;
        }

        public Cache build() throws IOException {
            if (bitmapCache == null) {
                maxBitmapCacheSize = CacheUtil.calculateMemoryCacheSize(context);
                bitmapCache = new BitmapLruCache(maxBitmapCacheSize);
            }

            if (isOkhttp) {
                okhttpDiskCache = new com.squareup.okhttp.Cache(diskDirectory, CacheUtil.calculateDiskCacheSize(diskDirectory));
                client.setCache(okhttpDiskCache);

                if (isPicasso) {
                    Picasso.setSingletonInstance(new Picasso.Builder(context).memoryCache(bitmapCache).downloader(new OkHttpDownloader(client)).build());
                }
            } else {
                if (diskDirectory == null) {
                    diskDirectory = CacheUtil.getDiskCacheDir(context, null);
                }
                customDiskCache = new DiskLruImageCache(diskDirectory, CacheUtil.calculateDiskCacheSize(diskDirectory));
                if (isPicasso) {
                    Picasso.setSingletonInstance(new Picasso.Builder(context).memoryCache(bitmapCache).build());
                }
            }

            if (stringCache == null) {
                maxStringCacheSize = 5 * 1024 * 1024;
                stringCache = new StringLruCache(maxStringCacheSize);
            }

            if (dbCache == null) {
                dbCache = new DbCache(context);
            }

            return new Cache(this);
        }

    }

}

