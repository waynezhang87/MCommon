package com.waynezhang.mcommon.cache.entity.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.waynezhang.mcommon.cache.entity.NetworkInfo;

public class CacheDao extends BaseDao {
	
	private static CacheDao instance = new CacheDao();
	public static CacheDao getInstance() {
		return instance;
	}

	@Override
	public String getTable() {
		return DbHelper.NETWORK;
	}
	
	public boolean insertNetworkInfo(Context ctx, String key, String value, String timeStamp) {
		ContentValues values = new ContentValues();
		values.put("key", key);
		values.put("value", value);
		values.put("timeStamp", timeStamp);
		
		insertWithOnConflict(ctx, values);
		return true;
		
	}
	
	public boolean checkExist(Context ctx,String key){
        String where = " key = ? ";
        int cnt = queryInt(ctx,new String[]{"count(1)"},where, new String[]{key},"" );
        return cnt>0;
    }

	public NetworkInfo queryNetworkInfo(Context ctx, String key) {
		NetworkInfo info = null;
		Cursor cursor = null;
		String where = " key = ? ";
		try {
			cursor = query(ctx,NetworkInfo.queryFields, where, new String[]{key}, null);
			if(cursor!=null){
				if (cursor.moveToFirst()) {
					info = new NetworkInfo(cursor);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor!=null)
				cursor.close();
		}
		return info;
	}
	
	public boolean deleteNetworkInfo(Context ctx, String key){
		String where = " key = ? ";
    	delete(ctx,where,new String[]{key});
        return true;
    }
	
	@SuppressLint("NewApi")
	public void removeDatabase(Context ctx) {
		String name = DbHelper.getInstance(ctx)._username;
		ctx.deleteDatabase(name);
		DbHelper.destroyInstance();
    }
	
	@SuppressLint("NewApi")
	public long getDatabaseSize(Context ctx) {
    	DbHelper dbHelper = DbHelper.getInstance(ctx);
    	return ctx.getDatabasePath(dbHelper.getDatabaseName()).length();
    }
	
}
