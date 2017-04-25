package com.wen.android.mtabuscomparison;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.wen.android.mtabuscomparison.model.TimeInfo;
import com.wen.android.mtabuscomparison.utilities.NetworkUtilities;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchResultActivity extends AppCompatActivity {

    private TextView mBusTimeInfoView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.result_swipe_refresh_layout);
        mBusTimeInfoView = (TextView)findViewById(R.id.bus_info_view_1);
        Date curDate = new Date();

        final Intent intentThatStartedThisActivity = getIntent();
        startBusTrackOperation(intentThatStartedThisActivity);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh() {
                        mBusTimeInfoView.setText("");
                        startBusTrackOperation(intentThatStartedThisActivity);
                    }
                }
        );
    }

    /**
     * This method will start operation to track the bus
     * @param intent extract the bus stop code from intent's Extra
     * @return void
     */
    public void startBusTrackOperation(Intent intent){
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            String[] recivedBusStopCode = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            for(String stopcode:recivedBusStopCode){
                Log.i("stopcodedebug:", "stop code:" + stopcode);
                if (!stopcode.equals("")){
                    makeBusTimeSearchQuery(stopcode);
                }
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * This method retrieves the stop code from the parent activity, constructs the URL
     */
    public void makeBusTimeSearchQuery(String stopcode){
        URL siriQueryUrl = NetworkUtilities.buildUrl(stopcode);
        new SearchResultActivity.FetchBusTimeTask().execute(siriQueryUrl);
    }


    public class FetchBusTimeTask extends AsyncTask<URL, Void, ArrayList<TimeInfo>> {
        @Override
        protected ArrayList<TimeInfo> doInBackground(URL... params) {
            URL  searchUrl = params[0];
            String busTimeInqueryResult = null;
            TimeInfo expectedTime = null;
            ArrayList<TimeInfo> stopsTimeInfo = null;
            try{
                busTimeInqueryResult = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                //expectedTime = NetworkUtilities.getSpecificItem(busTimeInqueryResult,"ExpectedArrivalTime");
                stopsTimeInfo = (ArrayList<TimeInfo>)NetworkUtilities.getSpecificItem(busTimeInqueryResult, "ExpectedArrivalTime");
            } catch (IOException e){
                e.printStackTrace();
            }
            //return expectedTime;
            return stopsTimeInfo;
        }

        @Override
        protected void onPostExecute(ArrayList<TimeInfo> stopsTimeInfo) {
            Date strToDate;
            //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

            TimeInfo errorChecking = stopsTimeInfo.get(0);

            for (int i = 0; i < stopsTimeInfo.size(); i++){
                // don't wanna give too many info
                if (i > 2 ){
                    return;
                }
                TimeInfo expectedTime = stopsTimeInfo.get(i);
                if(expectedTime.getFail() == false){
                    mBusTimeInfoView.setText(errorChecking.getErrorMessage());
                    return;
                }
                mBusTimeInfoView.append("\n" + expectedTime.getPublishedLineName() + "\n");

                Log.i("timedebug==========", "size:" + stopsTimeInfo.size());
                Log.i("timedebug==========", expectedTime.getPublishedLineName());
                mBusTimeInfoView.append(expectedTime.getStopPointName() + "\n");
                if (!expectedTime.getExpectedArrivalTime().equals("NoExpectedItem")){

                    try{
                        strToDate = format.parse(expectedTime.getExpectedArrivalTime());
                        Date currentDate = new Date();
                        Long diff = strToDate.getTime() - currentDate.getTime();

                        int minutes = (int) ((diff / (1000*60)) % 60);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(strToDate);
                        //mBusTimeInfoView.append(strToDate.getHours() + ":" + strToDate.getMinutes() + "\n");
                        String hour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        //mBusTimeInfoView.append(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + "\n");
                        mBusTimeInfoView.append("Expected arrival time: " +hour + "\n");
                        mBusTimeInfoView.append(minutes + " min" +"\n");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(!expectedTime.getPresentableDistance().equals("NoNumberOfStopsAway"))
                    mBusTimeInfoView.append(expectedTime.getPresentableDistance()  + "\n");
                if(!expectedTime.getArrivalProximityText().equals("can not track distance now"))
                    mBusTimeInfoView.append(expectedTime.getArrivalProximityText() + " miles away" + "\n");
                Log.i("timedebug====", "ArrivalProximityText: " + expectedTime.getArrivalProximityText());
                if (!expectedTime.getOriginAimedDepartureTime().equals("NoOriginAimedDepartureTime")) {
                    Log.i("timedebug====", "OriginAimDepartureTime: " + expectedTime.getOriginAimedDepartureTime());
                    try{
                        strToDate = format.parse(expectedTime.getOriginAimedDepartureTime());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(strToDate);
                        String DepartTime = String.format("%02d:%02d",cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        mBusTimeInfoView.append("Departure Time: " + DepartTime+ "\n");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }else {
                    mBusTimeInfoView.append("\n\n");
                }
            }

            //mSwipeRefreshLayout.setRefreshing(false);

        }
    }
}
