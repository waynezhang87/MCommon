package com.waynezhang.mcommon.cache;

import android.graphics.Bitmap;

import java.io.IOException;

public interface CacheInterface {

	public void putBitmapCache(String key, Bitmap bitmap);
	public Bitmap getBitmapCache(String key);
	public void deleteBitmapCache(String key);

	public void putCache(String key, String response);
	public void putCache(String key, String response, int period);
	public String getCache(String key);
	public void deleteCache(String key);


	public int memSize();
	public int memMaxSize();
	
	public void clear() throws IOException;
	public void close() throws IOException;
	public void clearInvalid();
	public void resizeMem(long maxSize);
	
}
