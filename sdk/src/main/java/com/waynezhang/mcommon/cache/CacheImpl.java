package com.waynezhang.mcommon.cache;

public interface CacheImpl<T> {
	
	public void clearInvalid();
	public void resizeMem(long maxSize);
	public void resizeDisk(long maxSize);
	
}
