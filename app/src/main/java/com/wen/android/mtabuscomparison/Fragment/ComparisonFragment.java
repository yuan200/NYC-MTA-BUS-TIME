package com.wen.android.mtabuscomparison.Fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.SaveComparison;
import com.wen.android.mtabuscomparison.SearchResultActivity;
import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.BusDbHelper;

/**
 * Created by yuan on 4/10/2017.
 */

public class ComparisonFragment extends Fragment {
    private FloatingActionButton fab;
    private CardView mCardView;
    private TextView mReadStopCode1;
    private TextView mReadStopCode2;
    private TextView mReadGounp;
    private SQLiteDatabase mDb;
    private Cursor mCursor;

    public ComparisonFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_comparison, container, false);
        BusDbHelper dbHelper = new BusDbHelper(getContext());
        //get a reference to the mdb
        mDb = dbHelper.getWritableDatabase();

        mCardView = (CardView)v.findViewById(R.id.card_view);
        mReadStopCode1 =(TextView) v.findViewById(R.id.read_stop_code_1);
        mReadStopCode2 =(TextView) v.findViewById(R.id.read_stop_code_2);
        mReadGounp =(TextView) v.findViewById(R.id.read_group);
        displayBusCode(getBusStopCode());
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaveComparison.class);
                startActivity(intent);
            }
        });

        mCardView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                displaySearchResult();
            }
        });
        return v;
    }

    private Cursor getBusStopCode(){
        return mDb.query(
                BusContract.BusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BusContract.BusEntry.COLUMN_TIMESTAMP
        );
    }

    public void displayBusCode(Cursor cursor){
        mCursor = cursor;

        while(mCursor.moveToNext()){
            String name = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE));
            String name2 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE2));
            String name3 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_GROUP));
            mReadStopCode1.setText(name);
            mReadStopCode2.setText(name2);
            mReadGounp.setText(name3);
            Log.d("databasedebug: ", "stop1!!!!!: " + name +"  stop2::" + name2);

        }


    }

    /**
     * start a new activity and display the search result
     */
    public void displaySearchResult(){
        String[] stopcodeArray = new String[2];
        //get the bus code from the user input
        stopcodeArray[0] = mReadStopCode1.getText().toString();
        stopcodeArray[1] = mReadStopCode2.getText().toString();

        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
        startActivity(intent);
    }



}
