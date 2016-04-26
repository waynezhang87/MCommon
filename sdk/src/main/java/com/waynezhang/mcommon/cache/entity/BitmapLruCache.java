package com.waynezhang.mcommon.cache.entity;


import android.content.Context;
import android.graphics.Bitmap;

import com.waynezhang.mcommon.util.CacheUtil;
import com.squareup.picasso.Cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/** A memory cache which uses a least-recently used eviction policy. */
public class BitmapLruCache implements Iterable, Cache {
	public static final String TAG = BitmapLruCache.class.getSimpleName();
	
	final LinkedHashMap<String, Bitmap> map;
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
	public BitmapLruCache(Context context) {
		this(CacheUtil.calculateMemoryCacheSize(context));
	}

	/** Create a cache with a given maximum size in bytes. */
	public BitmapLruCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("Max size must be positive.");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<String, Bitmap>(0, 0.75f, true);
	}

	@Override
	public Bitmap get(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		Bitmap value;
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
	public void set(String key, Bitmap value) {
		if (key == null || value == null) {
			throw new NullPointerException("key == null || bitmap == null");
		}

		Bitmap previous;
		synchronized (this) {
			putCount++;
			 size += CacheUtil.getBitmapBytes(value);
			previous = map.put(key, value);
			if (previous != null) {
				 size -= CacheUtil.getBitmapBytes(previous);
			}
		}
		trimToSize(maxSize);
	}

	private void trimToSize(long maxSize) {
		while (true) {
			String key;
			Bitmap value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					throw new IllegalStateException(getClass().getName()
							+ ".sizeOf() is reporting inconsistent results!");
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				Map.Entry<String, Bitmap> toEvict = map.entrySet().iterator().next();
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				 size -= CacheUtil.getBitmapBytes(value);
				evictionCount++;
			}
		}
	}

	public void delete(String key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}
		Bitmap value;
		synchronized (this) {
			if (size < 0 || (map.isEmpty() && size != 0)) {
				throw new IllegalStateException(getClass().getName()
						+ ".sizeOf() is reporting inconsistent results!");
			}
			value = map.remove(key);
			if (value != null) {
				 size -= CacheUtil.getBitmapBytes(value);
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

	@Override
	public final synchronized void clearKeyUri(String uri) {
		boolean sizeChanged = false;
		int uriLength = uri.length();
		for (Iterator<Map.Entry<String, Bitmap>> i = map.entrySet().iterator(); i.hasNext();) {
			Map.Entry<String, Bitmap> entry = i.next();
			String key = entry.getKey();
			Bitmap value = entry.getValue();
			int newlineIndex = key.indexOf(CacheUtil.KEY_SEPARATOR);
			if (newlineIndex == uriLength && key.substring(0, newlineIndex).equals(uri)) {
				i.remove();
				size -= CacheUtil.getBitmapBytes(value);
				sizeChanged = true;
			}
		}
		if (sizeChanged) {
			trimToSize(maxSize);
		}
	}

	/** Returns the number of times {@link #get} returned a value. */
	public final synchronized int hitCount() {
		return hitCount;
	}

	/** Returns the number of times {@link #get} returned {@code null}. */
	public final synchronized int missCount() {
		return missCount;
	}

	/** Returns the number of times {@link #set(String, Bitmap)} was called. */
	public final synchronized int putCount() {
		return putCount;
	}

	/** Returns the number of values that have been evicted. */
	public final synchronized int evictionCount() {
		return evictionCount;
	}

	@Override
	public Iterator<Bitmap> iterator() {
		final Iterator it = map.values().iterator();
		itCache =  new Iterator<Bitmap>() {

			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public Bitmap next() {
				return (Bitmap) it.next();
			}

			@Override
			public void remove() {
				it.remove();
			}
		};
		return itCache;
	}

}
