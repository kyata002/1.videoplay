//package com.mtg.videoplay.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//    public static final String DATABASE_NAME = "Resume.db";
//    public static final String TABLE_NAME = "resume";
//    // --Commented out by Inspection (9/6/2022 9:00 AM):public static final String COL_1 = "ID";
//    public static final String COL_2 = "URL";
//// --Commented out by Inspection START (9/6/2022 9:00 AM):
////    public static final String COL_3 = "LENGTH";
////
////    public DatabaseHelper(Context context) {
////        super(context, DATABASE_NAME, null, 1);
//// --Commented out by Inspection STOP (9/6/2022 9:00 AM)
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,URL TEXT,LENGTH LONG)");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    public boolean insertData(String url, long length) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_2, url);
//        contentValues.put(COL_3, length);
//        long result = db.insert(TABLE_NAME, null, contentValues);
//        return result != -1;
//    }
//
//    public Cursor getAllData(String url) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.rawQuery("select * from resume where URL='" + url + "'", null);
//    }
//
//
//    public Integer deleteData(String id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
//    }
//}
