package com.wen.android.mtabuscomparison.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by yuan on 4/11/2017.
 */

public class BusDbHelper extends SQLiteOpenHelper {
    //The database name
    private static final String DATABASE_NAME = "buslist.db";
    //if you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    //a handle to the application's resources
    private String output;

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
                BusContract.BusEntry.COLUMN_BUS_LINE + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_GROUP + " TEXT, " +
                BusContract.BusEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        final String SQL_CREATE_ALL_BUS_TABLE = "CREATE TABLE " + BusContract.BusEntry.TABLE_NAME_ALL_BUS + " (" +
                BusContract.BusEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_NAME + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_LAT + " TEXT NOT NULL, " +
                BusContract.BusEntry.COLUMN_BUS_STOP_LNG + " TEXT NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_BUSTLIST_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ALL_BUS_TABLE);
        //insert sample data
        sqLiteDatabase.execSQL("INSERT INTO "+ BusContract.BusEntry.TABLE_NAME + " (" +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE + ", " +
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE2 + ", " +
                BusContract.BusEntry.COLUMN_BUS_STOP_GROUP + ") " +
                "VALUES (" +
                "\'550552\', " +
                "\'551575\', " +
                "\'sample\'); ");

        //get the application's resources
        String file = "res/raw/stops_mtabc.txt";
        importDataFromTxtFile(file,sqLiteDatabase);
        file = "res/raw/stops_queens.txt";
        importDataFromTxtFile(file,sqLiteDatabase);
        file = "res/raw/stops_manhattan.txt";
        importDataFromTxtFile(file,sqLiteDatabase);
        file = "res/raw/stops_brooklyn.txt";
        importDataFromTxtFile(file,sqLiteDatabase);
        file = "res/raw/stops_bronx.txt";
        importDataFromTxtFile(file,sqLiteDatabase);
        file = "res/raw/stops_staten_island.txt";
        importDataFromTxtFile(file,sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //drop the table and create a new one, if you change the database version
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusContract.BusEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusContract.BusEntry.TABLE_NAME_ALL_BUS);
        onCreate(sqLiteDatabase);
    }

    /**
     * This function import data from a txt file into database
     * @param file the txt file including the path
     * @param sqLiteDatabase
     */
    public void importDataFromTxtFile(String file,SQLiteDatabase sqLiteDatabase){
         try( InputStream in = this.getClass().getClassLoader().getResourceAsStream(file)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in ));
            String myLine = br.readLine();

             while (myLine != null){
                 String[] array1 = myLine.split(",");
                 for (int i = 0; i < array1.length; i++){
                     array1[i] = array1[i].replaceAll("\"","");
                     array1[i] = array1[i].replaceAll("\\s+","");
                     array1[i] = array1[i].replaceAll("/","");
                     array1[i] = array1[i].replaceAll("'","");
                 }
                 sqLiteDatabase.execSQL("INSERT INTO " + BusContract.BusEntry.TABLE_NAME_ALL_BUS + " (" +
                         BusContract.BusEntry.COLUMN_BUS_STOP_CODE + ", " +
                         BusContract.BusEntry.COLUMN_BUS_NAME + ", " +
                         BusContract.BusEntry.COLUMN_BUS_STOP_LAT + ", " +
                         BusContract.BusEntry.COLUMN_BUS_STOP_LNG + ") " +
                         "VALUES (" +
                         "\'" +array1[0] + "\'," +
                         "\'" + array1[1] + "\'," +
                         "\'" + array1[3] + "\'," +
                         "\'" + array1[4] + "\');");
                myLine = br.readLine();

             }
            /**
            int data = in.read();
            while(data !=-1){
                System.out.print((char)data);
                data = in.read();
            }
             **/
        }catch (IOException e){

        }

    }
}