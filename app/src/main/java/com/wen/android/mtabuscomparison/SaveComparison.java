package com.wen.android.mtabuscomparison;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.BusDbHelper;

public class SaveComparison extends AppCompatActivity {
    private EditText mSaveStopCode1;
    private EditText mSaveStopCode2;
    private EditText mGroupName;
    private Button mSaveButton;
    private SQLiteDatabase mDb;
    private Cursor mCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_comparison);

        mSaveStopCode1 = (EditText) findViewById(R.id.save_stop_code1);
        mSaveStopCode2 = (EditText) findViewById(R.id.save_stop_code2);
        mGroupName = (EditText) findViewById(R.id.save_group_name) ;
        mSaveButton = (Button) findViewById(R.id.save_group_button);
        BusDbHelper dbHelper = new BusDbHelper(this);
        //get a reference to the mdb
        mDb = dbHelper.getWritableDatabase();
        mSaveButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                saveStopCode();
                finish();
            }
        });


    }

    public long saveStopCode(){
        ContentValues cv = new ContentValues();
        cv.put(BusContract.BusEntry.COLUMN_BUS_STOP_CODE, mSaveStopCode1.getText().toString());
        cv.put(BusContract.BusEntry.COLUMN_BUS_STOP_CODE2, mSaveStopCode2.getText().toString());
        cv.put(BusContract.BusEntry.COLUMN_BUS_STOP_GROUP, mGroupName.getText().toString());
        return mDb.insert(BusContract.BusEntry.TABLE_NAME, null, cv);
    }


}
