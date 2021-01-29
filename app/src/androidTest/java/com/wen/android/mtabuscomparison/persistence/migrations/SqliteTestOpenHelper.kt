package com.wen.android.mtabuscomparison.persistence.migrations

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.wen.android.mtabuscomparison.utilities.BusContract

class SqliteTestOpenHelper @JvmOverloads constructor(
        val context: Context,
        val name: String?,
        val cursorFactory: SQLiteDatabase.CursorFactory?,
        val version: Int = 1) : SQLiteOpenHelper(context, name, cursorFactory, version) {
    override fun onCreate(db: SQLiteDatabase?) {

        val SQL_CREATE_BUSTLIST_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME + " (" +
                BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE2 + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE3 + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_LINE + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_GROUP + " TEXT, " +
                BusContract.BusEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); "

        val SQL_CREATE_ALL_BUS_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME_ALL_BUS + " (" +
                BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_ROUTES + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_LAT + " INTEGER NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_LNG + " INTEGER NOT NULL " +
                "); "

        db?.execSQL(SQL_CREATE_BUSTLIST_TABLE)
        db?.execSQL(SQL_CREATE_ALL_BUS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}