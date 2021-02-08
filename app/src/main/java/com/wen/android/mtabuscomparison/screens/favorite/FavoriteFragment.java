package com.wen.android.mtabuscomparison.screens.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.screens.stopmonitoring.SearchResultActivity;
import com.wen.android.mtabuscomparison.stop.BusDatabase;
import com.wen.android.mtabuscomparison.stop.Favorite;
import com.wen.android.mtabuscomparison.stop.FavoriteStop;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class represent the favorite tab where user can see their save bus
 */

public class FavoriteFragment extends Fragment {
    private FloatingActionButton fab;
    private RecyclerView mBusRecyclerView;
    private BusAdapter mAdapter;
    private final static String FAVORITE_CHECKED = "favorite_checked";
    private final static String DATABASE_ROW_ID = "row_id";
    List<Favorite> mFavorite = new ArrayList<>();

    public FavoriteFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_favorite, container, false);
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
                Intent intent = new Intent(getActivity(), SaveFavorite.class);
                startActivity(intent);
            }
        });
        //updateUI();
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
        BusDatabase db = BusDatabase.Companion.getInstance(getContext().getApplicationContext());
        List<FavoriteStop> buses = db.busListDao().getAll();

        for (FavoriteStop bus : buses) {
            Favorite favorite = new Favorite();
            favorite.setRowId(String.valueOf(bus.get_id()));
            favorite.setGroupName(bus.getGroupName());
            favorite.setStopCode1(bus.getStopId());
            favorite.setStopCode2(bus.getStopId2());
            favorite.setStopCode3(bus.getStopId3());
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
            String[] stopcodeArray = new String[3];
            //get the bus code from the user input
            stopcodeArray[0] = mStopCodeView1.getText().toString();
            stopcodeArray[1] = mStopCodeView2.getText().toString();
            stopcodeArray[2] = mStopCodeView3.getText().toString();

            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
            intent.putExtra(FAVORITE_CHECKED,"favorite_check");
            intent.putExtra(DATABASE_ROW_ID,mRowId.getText().toString());
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
            Timber.i("called onBindViewHolder()");
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
            BusDatabase.Companion.getInstance(getContext()).busListDao().delete(Integer.valueOf(mFavorite.get(position).getRowId()));
            mFavorites.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void updateUI(){
        List<Favorite> favorites = getBusStopCode();
        mAdapter = new BusAdapter(favorites);
        mBusRecyclerView.setAdapter(mAdapter);
    }
}
