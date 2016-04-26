package com.waynezhang.mcommon.cache.entity;

import android.content.Context;

import com.waynezhang.mcommon.cache.entity.db.CacheDao;
import com.waynezhang.mcommon.cache.entity.db.DbHelper;

public class DbCache {
	public static final String TAG = DbCache.class.getSimpleName();
	private static final String DEFAULT_USERNAME = "default";
	private boolean isInit = false;
	private Context mAppContext;
	private String mUsername;
	
	public DbCache(Context ctx) {
		this(ctx, DEFAULT_USERNAME);
	}
	
	public DbCache(Context ctx, String username) {
		this.mAppContext = ctx.getApplicationContext();
		this.mUsername = username;
		init(mUsername);
	}
	
	public boolean isInit() {
		return isInit;
	}
	
	public void init(String username) {
		if (DbHelper.createInstance(mAppContext, username) != null) {
			isInit = true;
		}
	}

	public void close() {
		DbHelper.destroyInstance();
		isInit = false;
	}
	
	public void put(String key, String value) {
		if(!isInit) {
			init(DEFAULT_USERNAME);
		}
		CacheDao.getInstance().insertNetworkInfo(mAppContext, key, value, String.valueOf(System.currentTimeMillis()));
	}
	
	public NetworkInfo get(String key) {
		if(!isInit) {
			init(DEFAULT_USERNAME);
		}
		return CacheDao.getInstance().queryNetworkInfo(mAppContext, key);
	}
	
	
	public void delete(String key) {
		CacheDao.getInstance().deleteNetworkInfo(mAppContext, key);
	}
	
	public long size() {
		return CacheDao.getInstance().getDatabaseSize(mAppContext);
	}
	
	public long maxSize() {
		return CacheDao.getInstance().getDatabaseSize(mAppContext);
	}
	
	public void clear() {
		CacheDao.getInstance().removeDatabase(mAppContext);
		isInit = false;
	}
	
}
