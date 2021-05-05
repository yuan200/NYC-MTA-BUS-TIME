package com.wen.android.mtabuscomparison.ui.favorite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.wen.android.mtabuscomparison.BusApplication;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.feature.favorite.Favorite;
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase;

import java.util.List;

import timber.log.Timber;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.BusHolder> {
    private List<Favorite> mFavorites;
    private FirebaseAnalytics mFirebaseAnalytics;
    private OnFavoriteClickedListener mListener;

    interface OnFavoriteClickedListener {
        void oncFavoriteClicked(String stopId, String check, String rowId);
    }

    public FavoriteAdapter(List<Favorite> favorites, OnFavoriteClickedListener listener) {
        mFavorites = favorites;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(BusApplication.Companion.getInstance());
        mListener = listener;
    }

    @Override
    public BusHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new BusHolder(layoutInflater, parent, mFirebaseAnalytics, mListener);
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

    public void setFavorites(List<Favorite> favorites) {
        mFavorites = favorites;
    }

    public void onItemDelete(int position) {
        BusDatabase.Companion.getInstance(BusApplication.Companion.getInstance()).favoriteStopDao().delete(Integer.valueOf(mFavorites.get(position).getRowId()));
        mFavorites.remove(position);
        notifyItemRemoved(position);
    }

    static class BusHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mHolderTextview;
        private TextView mRowId;
        private TextView mStopCodeView1;
        private TextView mStopCodeView2;
        private TextView mStopCodeView3;
        private Favorite mFavorite;
        private FirebaseAnalytics mFirebaseAnalytics;
        private OnFavoriteClickedListener mListener;


        public BusHolder(LayoutInflater inflater, ViewGroup parent, FirebaseAnalytics firebaseAnalytics, OnFavoriteClickedListener listener) {
            super(inflater.inflate(R.layout.list_item_bus, parent, false));
            itemView.findViewById(R.id.list_item_cardview).setOnClickListener(this);
            mRowId = (TextView) itemView.findViewById(R.id.item_row_id);
            mHolderTextview = (TextView) itemView.findViewById(R.id.item_stop_name);
            mStopCodeView1 = (TextView) itemView.findViewById(R.id.item_read_stop_code_1);
            mStopCodeView2 = (TextView) itemView.findViewById(R.id.item_read_stop_code_2);
            mStopCodeView3 = (TextView) itemView.findViewById(R.id.item_read_stop_code_3);
            mFirebaseAnalytics = firebaseAnalytics;
            mListener = listener;
        }

        public void bind(Favorite favorite) {
            mFavorite = favorite;
            mRowId.setText(mFavorite.getRowId());
            mHolderTextview.setText(mFavorite.getGroupName());
            mStopCodeView1.setText(mFavorite.getStopCode1());
            mStopCodeView2.setText(mFavorite.getStopCode2());
            mStopCodeView3.setText(mFavorite.getStopCode3());
        }

        @Override
        public void onClick(View v) {
            String stopId = mStopCodeView1.getText().toString();
            mListener.oncFavoriteClicked(stopId, "favorite_check", mRowId.getText().toString());
        }
    }
}
