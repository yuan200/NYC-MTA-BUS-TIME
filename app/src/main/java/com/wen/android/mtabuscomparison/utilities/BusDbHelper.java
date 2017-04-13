package com.wen.android.mtabuscomparison.utilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuan on 4/11/2017.
 */

public class BusDbHelper extends SQLiteOpenHelper {
    //The database name
    private static final String DATABASE_NAME = "buslist.db";
    //if you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    public BusDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create a table to hold buslist data
        final String SQL_CREATE_BUSTLIST_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME + " (" +
                BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE2 + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE3 + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_GROUP + " TEXT, " +
                BusContract.BusEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        final String SQL_CREATE_SINGLE_BUSTLIST_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME_SINGLE_BUS + " (" +
                BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT,NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_LINE + " TEXT,NOT NULL, " +
                BusContract.BusEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_BUSTLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SINGLE_BUSTLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //drop the table and create a new one, if you change the database version
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusContract.BusEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusContract.BusEntry.TABLE_NAME_SINGLE_BUS);
        onCreate(sqLiteDatabase);
    }
}