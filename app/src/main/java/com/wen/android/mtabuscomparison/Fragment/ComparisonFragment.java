package com.wen.android.mtabuscomparison.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.SaveComparison;
import com.wen.android.mtabuscomparison.SearchResultActivity;
import com.wen.android.mtabuscomparison.model.Buses;
import com.wen.android.mtabuscomparison.model.Favorite;
import com.wen.android.mtabuscomparison.model.TimeInfo;
import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.BusDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represent the favorite tab where user can see their save bus
 */

public class ComparisonFragment extends Fragment {
    private FloatingActionButton fab;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private RecyclerView mBusRecyclerView;
    private BusAdapter mAdapter;

    public ComparisonFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_comparison, container, false);
        BusDbHelper dbHelper = new BusDbHelper(getContext());
        //get a reference to the mdb
        mDb = dbHelper.getWritableDatabase();
        mBusRecyclerView = (RecyclerView)v.findViewById(R.id.bus_recycler_view);
        mBusRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fab = (FloatingActionButton)v.findViewById(R.id.fab);
        //this button will open a new activity that let user save bus stop code
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SaveComparison.class);
                startActivity(intent);
            }
        });
        updateUI();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //when this activity back to the foreground update the UI if user saves new bus stop
        updateUI();
    }

    /**
     * This method reads data from database and store them in a list
     * @return a list that contains bus stop code and custom name
     */
    private List<Favorite> getBusStopCode(){
        List<Favorite> mFavorite = new ArrayList<>();
        mCursor = mDb.query(
                BusContract.BusEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                BusContract.BusEntry.COLUMN_TIMESTAMP
        );
        while (mCursor.moveToNext()){
            String name = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE));
            String name2 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE2));
            String name3 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_GROUP));
            Favorite favorite = new Favorite();
            favorite.setGroupName(name3);
            favorite.setStopCode1(name);
            favorite.setStopCode2(name2);
            mFavorite.add(favorite);
        }
        return mFavorite;
    }

    private class BusHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{
        private TextView mHolderTextview;
        private TextView mStopCodeView1;
        private TextView mStopCodeView2;
        private Favorite mFavorite;
        public BusHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_bus, parent, false));
            itemView.setOnClickListener(this);
            mHolderTextview = (TextView)itemView.findViewById(R.id.item_read_group);
            mStopCodeView1 = (TextView)itemView.findViewById(R.id.item_read_stop_code_1);
            mStopCodeView2 = (TextView)itemView.findViewById(R.id.item_read_stop_code_2);
        }

        public void bind(Favorite favorite){
            mFavorite = favorite;
            mHolderTextview.setText(mFavorite.getGroupName());
            mStopCodeView1.setText(mFavorite.getStopCode1());
            mStopCodeView2.setText(mFavorite.getStopCode2());
        }

        @Override
        public void onClick(View v) {
            Log.d("onclick recycler view: " , "clicked");
            String[] stopcodeArray = new String[2];
            //get the bus code from the user input
            stopcodeArray[0] = mStopCodeView1.getText().toString();
            stopcodeArray[1] = mStopCodeView2.getText().toString();

            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
            startActivity(intent);

        }
    }

    private class BusAdapter extends RecyclerView.Adapter<BusHolder>{
        private List<Favorite> mFavorites;
        public BusAdapter(List<Favorite> favorites) {
            mFavorites = favorites;
        }

        @Override
        public BusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new BusHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(BusHolder holder, int position) {
            Favorite favorite = mFavorites.get(position);
            Log.d("recycledebug: " , "onBindViewHolder " +favorite.getGroupName());
            holder.bind(favorite);
        }

        @Override
        public int getItemCount() {
            Log.d("recycledebug: " , "size: " + mFavorites.size());
            return mFavorites.size();
        }
        public void setFavorites(List<Favorite> favorites){
            mFavorites = favorites;
        }
    }

    private void updateUI(){
        List<Favorite> favorites = getBusStopCode();

        if (mAdapter == null){
            mAdapter = new BusAdapter(favorites);
            mBusRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setFavorites(favorites);
        }
    }



}
