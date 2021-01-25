package com.wen.android.mtabuscomparison;

import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.wen.android.mtabuscomparison.model.BusDirection;
import com.wen.android.mtabuscomparison.model.StopsForRoute;
import com.wen.android.mtabuscomparison.utilities.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class RoutesViewActivity extends AppCompatActivity {

    ExpandableLinearLayout mExpandableLinearLayout0;
    Button mExpandableButton;
    ExpandableLinearLayout mExpandableLinearLayout1;
    Button mExpandableButton1;
    TextView mTextView;
    private RecyclerView mRouteRecyclerView;
    private RecyclerView mRouteRecyclerView1;
    private StopForRouteAdapter mAdapter;
    private StopForRouteAdapter mAdapter1;
    private List<StopsForRoute> mBusRoute0;
    private List<StopsForRoute> mBusRoute1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes_view);

        //mTextView = (TextView) findViewById(R.id.expanableText);
        final Intent intentThatStartedThisActivity = getIntent();
        mRouteRecyclerView = (RecyclerView)findViewById(R.id.route_recycler_view);
        mRouteRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRouteRecyclerView1 = (RecyclerView)findViewById(R.id.route_recycler_view1);
        mRouteRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
        mExpandableLinearLayout0 = (ExpandableLinearLayout)findViewById(R.id.expandableLinearLayout0);
        mExpandableLinearLayout1 = (ExpandableLinearLayout)findViewById(R.id.expandableLinearLayout1);
        mExpandableButton = (Button) findViewById(R.id.expanable_button);
        mExpandableButton1 = (Button) findViewById(R.id.expanable_button1);
        mExpandableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(mBusRoute0);
                mExpandableLinearLayout0.expand();
            }
        });
        mExpandableButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUI(mBusRoute1);
                mExpandableLinearLayout0.expand();
            }
        });
        String userInputRoute = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        builTest(userInputRoute);;


    }

    public void builTest(String userInput ){
        URL[] obaUrl = NetworkUtilities.oneBusAwayBuildUrl(userInput);
        Log.i("obaurl: ", obaUrl[2].toString());
        new RoutesViewActivity.FetchBusTimeTask().execute(obaUrl);
    }

    public class FetchBusTimeTask extends AsyncTask<URL, Void, BusDirection> {
        @Override
        protected BusDirection doInBackground(URL... params) {
            URL searchUrl;
            String code = "nothing";
            String busTimeInqueryResult = null;
            BusDirection routes = new BusDirection();
            //loop 3 times because we have 3 url to check
            int j = 0;
            for (int i = 0; i < 3; i++) {
                searchUrl = params[i];
                try {
                    busTimeInqueryResult = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                    routes = NetworkUtilities.getStopListForRoute(busTimeInqueryResult);
                } catch (IOException e) {
                    e.printStackTrace();
                    // if j >= 3 then all url fails, display an error message
                    j++;
                    if (j >= 3){
                        routes = null;
                        return routes;
                    }
                }
                /**
                if (busTimeInqueryResult != null){
                    break;
                }
                 **/
            }
            return routes;
        }

        @Override
        protected void onPostExecute(BusDirection s) {
          //  String errorChecking = mBusRoute0.get(0).getId();
            if (s == null){
                mExpandableButton.setText("Sorry, we are unable to track the bus you asked");
                mExpandableButton.setEnabled(false);
                mExpandableButton1.setVisibility(View.GONE);
                mExpandableButton1.setEnabled(false);
                return;
            }
            mBusRoute0 = s.getDirection0();
            mBusRoute1 = s.getDirection1();
            //
            mExpandableButton.setText(mBusRoute0.get(0).getBusDirection());
            mExpandableButton1.setText(mBusRoute1.get(0).getBusDirection());
            updateUI(mBusRoute0);
        }
    }

    public class StopForRouteHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        private TextView mIntersection;
        private TextView mStopCode;
        private StopsForRoute mStopsForRoute;
        public StopForRouteHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_route_bus, parent, false));
            mIntersection = (TextView) itemView.findViewById(R.id.route_stop_intersection);
            mStopCode = (TextView) itemView.findViewById(R.id.route_stop_stopcode);

            itemView.setOnClickListener(this);
//            mIntersection = (TextView) findViewById(R.id.route_stop_intersection);
        }
        public void bind(StopsForRoute stopsForRoute){
            mStopsForRoute = stopsForRoute;
            mIntersection.setText(mStopsForRoute.getIntersections());
            mStopCode.setText((mStopsForRoute.getStopCode()));
        }

        @Override
        public void onClick(View v) {
            String[] stopcodeArray = new String[1];
            //get the bus code from the textview
            stopcodeArray[0] = mStopCode.getText().toString();

            Intent intent = new Intent(itemView.getContext(),SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
            itemView.getContext().startActivity(intent);
        }
    }

    public class StopForRouteAdapter extends RecyclerView.Adapter<StopForRouteHolder> {
        private List<StopsForRoute> mStopsList;

        public StopForRouteAdapter(List<StopsForRoute> busDirection){
            mStopsList = busDirection;
        }

        @Override
        public StopForRouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new StopForRouteHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(StopForRouteHolder holder, int position) {
            StopsForRoute singleStop = mStopsList.get(position);
            holder.setIsRecyclable(false);
            holder.bind(singleStop);
        }

        @Override
        public int getItemCount() {
            return mStopsList.size();
        }
    }

    private void updateUI(List<StopsForRoute> stops_List){
        List<StopsForRoute> stopsList = stops_List;

        mAdapter = new StopForRouteAdapter(stopsList);
        mRouteRecyclerView.setAdapter(mAdapter);
    }
}
