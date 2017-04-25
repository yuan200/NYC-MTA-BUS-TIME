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
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.wen.android.mtabuscomparison.utilities.SwipeableRecyclerViewTouchListener;

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
    List<Favorite> mFavorite = new ArrayList<>();

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
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(
                            final RecyclerView recyclerView,
                            final RecyclerView.ViewHolder viewHolder,
                            final RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(
                            final RecyclerView.ViewHolder viewHolder,
                            final int swipeDir) {
                        //adapter.remove(viewHolder.getAdapterPosition());
                        mAdapter.onItemDelete(viewHolder.getAdapterPosition());
                    }
                };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                simpleItemTouchCallback
        );
        itemTouchHelper.attachToRecyclerView(mBusRecyclerView);

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
        List<Favorite> mFavoriteList = new ArrayList<>();
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
            String rowId = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry._ID));
            String name = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE));
            String name2 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE2));
            String name4 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE3));
            String name3 = mCursor.getString(mCursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_GROUP));
            Favorite favorite = new Favorite();
            favorite.setRowId(rowId);
            favorite.setGroupName(name3);
            favorite.setStopCode1(name);
            favorite.setStopCode2(name2);
            favorite.setStopCode3(name4);
            mFavoriteList.add(favorite);
        }
        mFavorite = mFavoriteList;
        return mFavoriteList;
    }

    private class BusHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {
        private TextView mHolderTextview;
        private TextView mRowId;
        private TextView mStopCodeView1;
        private TextView mStopCodeView2;
        private TextView mStopCodeView3;
        private Favorite mFavorite;
        public BusHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_bus, parent, false));
            itemView.setOnClickListener(this);
            mRowId = (TextView)itemView.findViewById(R.id.item_row_id);
            mHolderTextview = (TextView)itemView.findViewById(R.id.item_read_group);
            mStopCodeView1 = (TextView)itemView.findViewById(R.id.item_read_stop_code_1);
            mStopCodeView2 = (TextView)itemView.findViewById(R.id.item_read_stop_code_2);
            mStopCodeView3 = (TextView)itemView.findViewById(R.id.item_read_stop_code_3);
        }

        public void bind(Favorite favorite){
            mFavorite = favorite;
            mRowId.setText(mFavorite.getRowId());
            mHolderTextview.setText(mFavorite.getGroupName());
            mStopCodeView1.setText(mFavorite.getStopCode1());
            mStopCodeView2.setText(mFavorite.getStopCode2());
            mStopCodeView3.setText(mFavorite.getStopCode3());
        }

        @Override
        public void onClick(View v) {
            Log.d("onclick recycler view: " , "clicked");
            String[] stopcodeArray = new String[3];
            //get the bus code from the user input
            stopcodeArray[0] = mStopCodeView1.getText().toString();
            stopcodeArray[1] = mStopCodeView2.getText().toString();
            stopcodeArray[2] = mStopCodeView3.getText().toString();

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
            holder.bind(favorite);
        }

        @Override
        public int getItemCount() {
            return mFavorites.size();
        }
        public void setFavorites(List<Favorite> favorites){
            mFavorites = favorites;
        }

        public void onItemDelete(int position){
            mDb.delete(BusContract.BusEntry.TABLE_NAME, BusContract.BusEntry._ID + " = " + mFavorites.get(position).getRowId(),null );
            mFavorites.remove(position);
            notifyItemRemoved(position);
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
