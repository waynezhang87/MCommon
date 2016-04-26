package com.waynezhang.mcommon.cache.entity;


/**
 * A memory cache for storing the most recently used images.
 *
 */

public interface LruCacheInterface {

	public void set(String key, NetworkInfo value);
	public NetworkInfo get(String key);
	public void delete(String key);
	public int size();
	public int maxSize();
	public void clear();
	
}
