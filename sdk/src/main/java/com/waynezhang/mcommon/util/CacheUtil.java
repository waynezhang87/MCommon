package com.waynezhang.mcommon.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class CacheUtil {
	public static final Charset US_ASCII = Charset.forName("US-ASCII");
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
	private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB
	private static final String SNDA_CACHE = "/snda-cache";
	public static final int IO_BUFFER_SIZE = 8 * 1024;
	public static final char KEY_SEPARATOR = '\n';

	public static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * Deletes the contents of {@code dir}. Throws an IOException if any file
	 * could not be deleted, or if {@code dir} is not a readable directory.
	 */
	public static void deleteContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException("not a readable directory: " + dir);
		}
		for (File file : files) {
			if (file.isDirectory()) {
				deleteContents(file);
			}
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	public static void closeQuietly(/* Auto */Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	public static int calculateMemoryCacheSize(Context context) {
		ActivityManager am = getService(context, Context.ACTIVITY_SERVICE);
		boolean largeHeap = (context.getApplicationInfo().flags & ApplicationInfo.FLAG_LARGE_HEAP) != 0;
		int memoryClass = am.getMemoryClass();
		if (largeHeap && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
		}
		// Target ~15% of the available heap.
		return 1024 * 1024 * memoryClass / 7;
	}

	public static File getDiskCacheDir(Context context, String uniqueName) {

		// Check if media is mounted or storage is built-in, if so, try and use external cache dir
		// otherwise use internal cache dir
		final String cachePath =
				Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
						!isExternalStorageRemovable() ?
						getExternalCacheDir(context).getPath() :
						getInternalCacheDir(context).getPath();
		return new File(cachePath + (uniqueName == null ? "" : (File.separator + uniqueName)));
	}

	public static File getExternalCacheDir(Context context) {
		if (hasExternalCacheDir(context)) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + SNDA_CACHE;
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	public static boolean hasExternalCacheDir(Context context) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && context.getExternalCacheDir() != null;
	}

	public static File getInternalCacheDir(Context context) {
		if (hasExternalCacheDir(context)) {
			return context.getCacheDir();
		}

		final String cacheDir = "/data/data/" + context.getPackageName() + SNDA_CACHE;
		return new File(cacheDir);
	}

	public static boolean hasInternalCacheDir(Context context) {
		return context.getCacheDir() != null;
	}

	public static long calculateDiskCacheSize(File dir) {
		long size = MIN_DISK_CACHE_SIZE;

		try {
			StatFs statFs = new StatFs(dir.getAbsolutePath());
			long available = ((long) statFs.getBlockCount())
					* statFs.getBlockSize();
			// Target 2% of the total space.
			size = available / 50;
		} catch (IllegalArgumentException ignored) {
		}

		// Bound inside min/max size for disk cache.
		return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE),
				MIN_DISK_CACHE_SIZE);
	}

	public static boolean isExternalStorageRemovable() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public static <T> T getService(Context context, String service) {
		return (T) context.getSystemService(service);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static class ActivityManagerHoneycomb {
		static int getLargeMemoryClass(ActivityManager activityManager) {
			return activityManager.getLargeMemoryClass();
		}
	}

	public static int getBitmapBytes(Bitmap bitmap) {
		int result;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			result = BitmapHoneycombMR1.getByteCount(bitmap);
		} else {
			result = bitmap.getRowBytes() * bitmap.getHeight();
		}
		if (result < 0) {
			throw new IllegalStateException("Negative size: " + bitmap);
		}
		return result;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	private static class BitmapHoneycombMR1 {
		static int getByteCount(Bitmap bitmap) {
			return bitmap.getByteCount();
		}
	}

//	public static Type getDataType(Policy foo) {
//		Type mySuperClass = foo.getClass().getGenericSuperclass();
//		Type type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
//		return type;
//
//	}

	public static boolean regexCheck(String url, String regexUrl) {
		return url.matches(regexUrl);
	}

}
