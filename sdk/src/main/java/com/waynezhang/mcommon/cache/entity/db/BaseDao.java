package com.waynezhang.mcommon.cache.entity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public abstract class BaseDao {

    public abstract String getTable();

    void insert(Context ctx, ContentValues contentValues) {

        DbHelper dbHelper = DbHelper.getInstance(ctx);
        if(dbHelper!=null){
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert(getTable(),"",contentValues);
        }
    }

    void insertWithOnConflict(Context ctx, ContentValues contentValues) {
    	
    	DbHelper dbHelper = DbHelper.getInstance(ctx);
    	if(dbHelper!=null){
    		SQLiteDatabase db = dbHelper.getWritableDatabase();
    		db.insertWithOnConflict(getTable(),"",contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    	}
    }

    int delete(Context ctx, String s, String[] strings) {

        DbHelper dbHelper = DbHelper.getInstance(ctx);
        if(dbHelper!=null){
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
            return db.delete(getTable(),s,strings);
        }
        
        return 0;
    }

    int update(Context ctx, ContentValues contentValues, String s, String[] strings) {

        DbHelper dbHelper = DbHelper.getInstance(ctx);
        if(dbHelper!=null){
        	SQLiteDatabase db = dbHelper.getWritableDatabase();
            return db.update(getTable(),contentValues,s,strings);
        }

        return 0;
    }

    Cursor query(Context ctx, String[] queryfields, String where, String[] wherevalues, String orderBy) {
        SQLiteDatabase db ;

        DbHelper dbHelper = DbHelper.getInstance(ctx);
        if(dbHelper!=null){
            db = dbHelper.getReadableDatabase();
            Cursor queryCur = db.query(getTable(),queryfields,where,wherevalues,null,null,orderBy);
            return queryCur;
        }

        return null;
    }

    int queryInt(Context ctx, String[] queryfields, String where, String[] wherevalues, String orderBy) {
        Cursor cursor = null;
        int cnt = 0;
        try {
            cursor = query(ctx,queryfields,where,wherevalues,orderBy);
            if( cursor == null ) return 0;

            if(cursor.moveToFirst()){
                cnt = cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null)
                cursor.close();
        }
        return cnt;
    }
    
    void execSql(Context ctx, String sql) {
        SQLiteDatabase db ;

        DbHelper dbHelper = DbHelper.getInstance(ctx);
        if(dbHelper!=null){
            db = dbHelper.getWritableDatabase();
            db.execSQL(sql);
        }
    }
    
}
