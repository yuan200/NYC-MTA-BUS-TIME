package com.wen.android.mtabuscomparison.handler;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.BusDbHelper;

/**
 * Created by yuan on 4/28/2017.
 */

public class DatabaseHandler {
    private BusDbHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DatabaseHandler(Context context){
        mDbHelper = new BusDbHelper(context);
        mDb = mDbHelper.getWritableDatabase();
    }

    public long insertToDatabase(ContentValues cv){
        return mDb.insert(BusContract.BusEntry.TABLE_NAME,null,cv);
    }

    public void deleteFromDatabase(long rowID){
            mDb.delete(BusContract.BusEntry.TABLE_NAME, BusContract.BusEntry._ID + " = " + rowID,null );
    }

}
