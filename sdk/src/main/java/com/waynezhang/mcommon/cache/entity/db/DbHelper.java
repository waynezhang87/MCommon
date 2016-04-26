package com.waynezhang.mcommon.cache.entity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbCache";
    
    public static final String PREFIX = "networkCache";

    public static final String NETWORK = "network";
    
    public static Object lock  = new Object();

    public static final int VERSION = 1;

    public static DbHelper _db;
    public String _username;
    
    public static DbHelper createInstance(Context ctx,String username){
        if(_db!=null){
            if(_db._username.equals(username)){
                Log.i(TAG,"DbHelper user "+username+" user db in cache");
                return _db;
            }else{
                synchronized (lock){
                    try{
                       _db.close();
                       _db=null;
                       Log.i(TAG,"DbHelper user "+username+" user db different from cache .close cache");
                    }catch (Exception e){
                       Log.e(TAG, "DbHelper error",e);
                    }
                }
            }
        }

        synchronized (lock){
            if(_db==null ){
                Log.i(TAG,"DbHelper user "+username+" user db created");
                _db = new DbHelper(ctx,username);
                return _db;
            }
        }


        return _db;
    }

    public static void destroyInstance(){
        if(_db!=null){
            try{
                _db.close();
            }catch (Exception e){
            	Log.e(TAG, "DbHelper error",e);
            }
            _db = null;
        }
    }

    public static DbHelper getInstance(Context ctx){
        return _db;
    }

    private DbHelper(Context context,String username) {
        super(context, concatDb(PREFIX,username), null, VERSION);
        String dbstr = concatDb(PREFIX,username);
        Log.i(TAG,"DbHelper make db name "+dbstr);
        this._username = dbstr;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	Log.i(TAG,"DbHelper onCreate called");
    	
        String sql = "CREATE TABLE IF NOT EXISTS "+concatTable(NETWORK)+" " +
                "(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, key varchar UNIQUE,value varchar,timeStamp varchar" +
                ")";
        db.execSQL(sql);
   
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	
    	Log.i(TAG,"DbHelper onUpgrade called, oldVersion="+oldVersion+",newVersion="+newVersion);

    	if( oldVersion == 1 ) {
    		String tsql = "ALTER TABLE "+concatTable(NETWORK)+" ADD extraInfo varchar";
            db.execSQL(tsql);
            return;
    	}

        db.execSQL("DROP TABLE IF EXISTS "+concatTable(NETWORK));
        
        onCreate(db);
        return;
    }

    private String concatTable(String table){
        return table;
    }

    private static String concatDb(String prefix,String username){
        return prefix+"_"+ (username.hashCode() & 0x1ffffffffL);
    }

}
