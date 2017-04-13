package com.wen.android.mtabuscomparison;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.Date;

public class SearchResultActivity extends AppCompatActivity {

    private TextView mBusTimeInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mBusTimeInfoView = (TextView)findViewById(R.id.bus_info_view_1);
        Date curDate = new Date();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            String[] recivedBusStopCode = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
            for(String stopcode:recivedBusStopCode){
                makeBusTimeSearchQuery(stopcode);
            }

        }
    }

    /**
     * This method retrieves the stop code from the parent activity, constructs the URL
     */
    public void makeBusTimeSearchQuery(String stopcode){
        URL siriQueryUrl = NetworkUtilities.buildUrl(stopcode);
        new SearchResultActivity.FetchBusTimeTask().execute(siriQueryUrl);
    }


    public class FetchBusTimeTask extends AsyncTask<URL, Void, TimeInfo> {
        @Override
        protected TimeInfo doInBackground(URL... params) {
            URL  searchUrl = params[0];
            String busTimeInqueryResult = null;
            TimeInfo expectedTime = null;
            try{
                busTimeInqueryResult = NetworkUtilities.getResponseFromHttpUrl(searchUrl);
                expectedTime = NetworkUtilities.getSpecificItem(busTimeInqueryResult,"ExpectedArrivalTime");
            } catch (IOException e){
                e.printStackTrace();
            }
            return expectedTime;
        }

        @Override
        protected void onPostExecute(TimeInfo expectedTime) {
            Date strToDate;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            mBusTimeInfoView.append(expectedTime.getPublishedLineName() + "\n\n");
            mBusTimeInfoView.append(expectedTime.getStopPointName() + "\n");
            if (!expectedTime.getExpectedArrivalTime().equals("NoExpectedItem")){

                try{
                    strToDate = format.parse(expectedTime.getExpectedArrivalTime());
                    Date currentDate = new Date();
                    Log.d("timedebug", "default pattern: "+ strToDate);
                    Long diff = strToDate.getTime() - currentDate.getTime();
                    Log.d("timedebug", "diff: "+ diff);

                    int minutes = (int) ((diff / (1000*60)) % 60);
                    Log.d("timedebug", "diff: "+ minutes);
                    mBusTimeInfoView.append(strToDate.getHours() + ":" + strToDate.getMinutes() + "\n");
                    mBusTimeInfoView.append(minutes + " min" +"\n");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if(!expectedTime.getPresentableDistance().equals("NoNumberOfStopsAway"))
                mBusTimeInfoView.append(expectedTime.getPresentableDistance()  + "\n");
            if(!expectedTime.getArrivalProximityText().equals("can not track distance now"))
                mBusTimeInfoView.append(expectedTime.getArrivalProximityText() + " miles away" + "\n");
            if (!expectedTime.getOriginAimedDepartureTime().equals("NoOriginAimedDepartureTime")) {
                try{
                    strToDate = format.parse(expectedTime.getOriginAimedDepartureTime());
                    mBusTimeInfoView.append("Departure Time: " + strToDate.getHours() + ":" + strToDate.getMinutes() + "\n");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else {
                mBusTimeInfoView.append("\n\n");
            }

        }
    }
}
