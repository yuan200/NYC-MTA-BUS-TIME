package com.wen.android.mtabuscomparison.persistence.migrations

import com.wen.android.mtabuscomparison.utilities.BusContract

class SqliteTestHelper {
    companion object {
        fun createTable(helper: SqliteTestOpenHelper) {
            helper.writableDatabase.apply {
                val SQL_CREATE_BUSTLIST_TABLE = "CREATE TABLE IF NOT EXISTS " + BusContract.BusEntry.TABLE_NAME + " (" +
                        BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                        BusContract.BusEntry.COLUMN_BUS_STOP_CODE2 + " TEXT, " +
                        BusContract.BusEntry.COLUMN_BUS_STOP_CODE3 + " TEXT, " +
                        BusContract.BusEntry.COLUMN_BUS_LINE + " TEXT, " +
                        BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT, " +
                        BusContract.BusEntry.COLUMN_BUS_STOP_GROUP + " TEXT, " +
                        BusContract.BusEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        "); "

                val SQL_CREATE_ALL_BUS_TABLE = "CREATE TABLE IF NOT EXISTS " + BusContract.BusEntry.TABLE_NAME_ALL_BUS + " (" +
                        BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                        BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT NOT NULL, " +
                        BusContract.BusEntry.COLUMN_BUS_STOP_LAT + " TEXT NOT NULL, " +
                        BusContract.BusEntry.COLUMN_BUS_STOP_LNG + " TEXT NOT NULL " +
                        "); "
                execSQL(SQL_CREATE_ALL_BUS_TABLE)
                execSQL(SQL_CREATE_BUSTLIST_TABLE)
                close()
            }
        }
    }
}