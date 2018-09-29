package com.wind.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by w010003593 on 2018/5/18.
 */

public class SQLiteOpenHelperUtil extends SQLiteOpenHelper {

    private static final String DB_NAME = "distance.db";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE_TABLE = "tb_distance";

    public SQLiteOpenHelperUtil(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + DB_CREATE_TABLE +
                "(_id integer primary key autoincrement, type text, mileage double)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists distance");
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void insertDB(SQLiteDatabase database,String name,double mileage){
        ContentValues values = new ContentValues();
        values.put("type", name);
        values.put("mileage", mileage);
        database.insert(DB_CREATE_TABLE,null,values);
    }

    public double QueryDB(SQLiteDatabase database,int mId){
        double distance = 0.0;
        Cursor cursor = database.query(DB_CREATE_TABLE, null, "_id=" + mId,
                null, null, null, null);
        Log.i("wangtongming","cursor: " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String type = cursor.getString(cursor.getColumnIndex("type"));
                double mileage = cursor.getDouble(cursor.getColumnIndex("mileage"));
                Log.i("wangtongming","type: " + type + ",mileage: " + mileage);
                distance = mileage;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return distance;
    }

    public void UpdateDB(SQLiteDatabase database,String name,double mileage,int mId){
        ContentValues values = new ContentValues();
        values.put("type", name);
        values.put("mileage", mileage);
        database.update(DB_CREATE_TABLE, values, "_id=" + mId, null);
    }

}
