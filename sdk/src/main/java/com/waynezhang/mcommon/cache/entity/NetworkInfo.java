package com.waynezhang.mcommon.cache.entity;

import android.database.Cursor;

public class NetworkInfo {
	public static String[] queryFields = new String[]{
		"key","value","timeStamp"};
	
	public String key;
	public String value;
	public String timeStamp;
	
	public NetworkInfo(Cursor cursor) {
		key = cursor.getString(0);
		value = cursor.getString(1);
		timeStamp = cursor.getString(2);
	}

	public NetworkInfo(String key, String value) {
		this.key = key;
		this.value = value;
		this.timeStamp = String.valueOf(System.currentTimeMillis());
	}
	
	public String toString() {
    	StringBuilder buff = new StringBuilder();
    	buff.append("key=").append(key).append(",");
    	buff.append("value=").append(value).append(",");
    	buff.append("timeStamp=").append(timeStamp).append(",");
    	return buff.toString();
    }
}
