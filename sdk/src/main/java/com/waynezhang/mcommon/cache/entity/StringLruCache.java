package com.waynezhang.mcommon.cache.entity;


import android.content.Context;

import com.waynezhang.mcommon.util.CacheUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** A memory cache which uses a least-recently used eviction policy. */
public class StringLruCache implements Iterable, LruCacheInterface {
	public static final String TAG = StringLruCache.class.getSimpleName();

	final LinkedHashMap<String, NetworkInfo> map;
	private final int maxSize;

	private int size;
	private int putCount;
	private int evictionCount;
	private int hitCount;
	private int missCount;
	private int itCount;
	private Iterator itCache;

	/**
	 * Create a cache using an appropriate portion of the available RAM as the
	 * maximum size.
	 */
	public StringLruCache(Context context) {
		this(CacheUtil.calculateMemoryCacheSize(context));
	}

	/** Create a cache with a given maximum size in bytes. */
	public StringLruCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("Max size must be positive.");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<String, NetworkInfo>(0, 0.75f, true);
	}

	@Override
	public NetworkInfo get(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		NetworkInfo value;
		synchronized (this) {
			value = map.get(key);
			if (value != null) {
				hitCount++;
				return value;
			}
			missCount++;
		}

		return null;
	}

	@Override
	public void set(String key, NetworkInfo value) {
		if (key == null || value == null) {
			throw new NullPointerException("key == null || bitmap == null");
		}

		NetworkInfo previous;
		synchronized (this) {
			putCount++;
			 size += value.value.getBytes().length;
			previous = map.put(key, value);
			if (previous != null) {
				 size -= previous.value.getBytes().length;
			}
		}
		trimToSize(maxSize);
	}

	private void trimToSize(long maxSize) {
		while (true) {
			String key;
			NetworkInfo value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					throw new IllegalStateException(getClass().getName()
							+ ".sizeOf() is reporting inconsistent results!");
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				Map.Entry<String, NetworkInfo> toEvict = map.entrySet().iterator().next();
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				 size -= value.value.getBytes().length;
				evictionCount++;
			}
		}
	}

	public void delete(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}
		NetworkInfo value;
		synchronized (this) {
			if (size < 0 || (map.isEmpty() && size != 0)) {
				throw new IllegalStateException(getClass().getName()
						+ ".sizeOf() is reporting inconsistent results!");
			}
			value = map.remove(key);
			if (value != null) {
				 size -= value.value.getBytes().length;
				evictionCount++;
			}
		}
		
	}

	/** Clear the cache. */
	public final void evictAll() {
		trimToSize(-1); // -1 will evict 0-sized elements
	}

	/** Returns the sum of the sizes of the entries in this cache. */
	public final synchronized int size() {
		return size;
	}


	/** Returns the maximum sum of the sizes of the entries in this cache. */
	public final synchronized int maxSize() {
		return maxSize;
	}


	public final synchronized void clear() {
		evictAll();
	}

	/** Returns the number of times {@link #get} returned a value. */
	public final synchronized int hitCount() {
		return hitCount;
	}

	/** Returns the number of times {@link #get} returned {@code null}. */
	public final synchronized int missCount() {
		return missCount;
	}

	/** Returns the number of times {@link #set(String, NetworkInfo)} was called. */
	public final synchronized int putCount() {
		return putCount;
	}

	/** Returns the number of values that have been evicted. */
	public final synchronized int evictionCount() {
		return evictionCount;
	}

	@Override
	public Iterator<String> iterator() {
		final Iterator it = map.values().iterator();
		itCache =  new Iterator<String>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public String next() {
				return (String) it.next();
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
		return itCache;
	}

}
