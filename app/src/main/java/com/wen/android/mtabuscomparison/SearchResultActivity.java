package com.wen.android.mtabuscomparison;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wen.android.mtabuscomparison.handler.DatabaseHandler;
import com.wen.android.mtabuscomparison.model.TimeInfo;
import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.NetworkUtilities;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.icu.lang.UProperty.INT_START;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

public class SearchResultActivity extends AppCompatActivity {

    private TextView mBusTimeInfoView;
    private MenuItem mFavoriteMenu;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CardView mBusTimeCardview1;
    private CardView mBusTimeCardview2;
    private CardView mBusTimeCardview3;
    private TextView mBusTimeTextview1;
    private TextView mBusTimeTextview2;
    private TextView mBusTimeTextview3;
    private ArrayList<TimeInfo> mSaveForFavoriteList;
    private final static String FAVORITE_CHECKED = "favorite_checked";
    private final static String DATABASE_ROW_ID = "row_id";
    private long mRowID;
    private static int textViewIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        mSaveForFavoriteList = new ArrayList<>();
        mBusTimeCardview1 = (CardView)findViewById(R.id.bus_info_card1);
        mBusTimeCardview2 = (CardView)findViewById(R.id.bus_info_card2);
        mBusTimeCardview3 = (CardView)findViewById(R.id.bus_info_card3);
        mBusTimeTextview1 = (TextView)findViewById(R.id.bus_info_view_1);
        mBusTimeTextview2 = (TextView)findViewById(R.id.bus_info_view_2);
        mBusTimeTextview3 = (TextView)findViewById(R.id.bus_info_view_3);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.result_swipe_refresh_layout);
        mBusTimeInfoView = (TextView)findViewById(R.id.bus_info_view_1);
        mRowID = 9999999;

        final Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(DATABASE_ROW_ID)){
            mRowID = Long.parseLong(intentThatStartedThisActivity.getStringExtra(DATABASE_ROW_ID));
        }
        startBusTrackOperation(intentThatStartedThisActivity);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener(){
                    @Override
                    public void onRefresh() {
                        startBusTrackOperation(intentThatStartedThisActivity);
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_favorite, menu);
        mFavoriteMenu = menu.findItem(R.id.action_favorite);
        Intent intent = getIntent();
        if (intent.hasExtra(FAVORITE_CHECKED)){
            mFavoriteMenu.setChecked(true);
            mFavoriteMenu.setIcon(R.drawable.ic_favorite_yellow_24dp);
            Drawable drawable = mFavoriteMenu.getIcon();
            drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                //item.setIcon(R.drawable.ic_favorite_black_24dp);
                setFavorite(item);
                if (!item.isChecked()){
                    removeFromFavorite();
                }else {
                    addToFavorite();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will start operation to track the bus
     * @param intent extract the bus stop code from intent's Extra
     * @return void
     */
    public void startBusTrackOperation(Intent intent){
        mBusTimeCardview1.setVisibility(View.GONE);
        mBusTimeCardview2.setVisibility(View.GONE);
        mBusTimeCardview3.setVisibility(View.GONE);
        mBusTimeTextview1.setVisibility(View.GONE);
        mBusTimeTextview2.setVisibility(View.GONE);
        mBusTimeTextview3.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        if (intent.hasExtra(Intent.EXTRA_TEXT)){
            String[] recivedBusStopCode = intent.getStringArrayExtra(Intent.EXTRA_TEXT);
            for( int i = 0; i < recivedBusStopCode.length; i++){
                if (i == 0){
                    textViewIndicator =0;
                }
                if (!recivedBusStopCode[i].equals("")){
                    makeBusTimeSearchQuery(recivedBusStopCode[i]);
                }
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
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            ArrayList<String> busLineName = new ArrayList<>();
            TimeInfo errorChecking;
            TimeInfo expectedTime;
            TimeInfo saveForFavorite;
            incTextviewIndicator();
            TextView timeInfoView;
            Boolean isSameBusLine;

            timeInfoView = getBusInfoView();
            //if an android doens't connect to Internet, then sopsTimeInfo will be null
            if(stopsTimeInfo == null){
                timeInfoView.setText("Sorry, something went wrong, please try again later");
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            expectedTime = stopsTimeInfo.get(0);
            errorChecking = stopsTimeInfo.get(0);
            if(expectedTime.getFail() == false){
                timeInfoView.setText(errorChecking.getErrorMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }


            String boldText = stopsTimeInfo.get(0).getStopPointName();
            saveForFavorite = new TimeInfo();
            saveForFavorite.setStopNumber(stopsTimeInfo.get(0).getStopNumber());
            saveForFavorite.setStopPointName(stopsTimeInfo.get(0).getStopPointName());
            mSaveForFavoriteList.add(saveForFavorite);
            SpannableString str = new SpannableString(boldText);
            str.setSpan(new StyleSpan(Typeface.BOLD), 0, boldText.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(Color.BLUE),0 , boldText.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            //timeInfoView.append(stopsTimeInfo.get(0).getStopPointName() + "\n");
            timeInfoView.append(str);
            timeInfoView.append("\n");

            for (int i = 0; i < stopsTimeInfo.size(); i++){
                // don't wanna give too many info
                /**
                if (i > 2 ){
                    return;
                }
                 **/
                expectedTime = stopsTimeInfo.get(i);
                isSameBusLine = false;
                for(String lineName:busLineName){
                    if (expectedTime.getPublishedLineName().equalsIgnoreCase(lineName)){
                        isSameBusLine = true;
                    }
                }
                if (isSameBusLine != true){
                    busLineName.add(expectedTime.getPublishedLineName());
                }
                timeInfoView.append(expectedTime.getPublishedLineName() + "\n");
                if (!expectedTime.getExpectedArrivalTime().equals("NoExpectedItem")){

                    try{
                        strToDate = format.parse(expectedTime.getExpectedArrivalTime());
                        Date currentDate = new Date();
                        Long diff = strToDate.getTime() - currentDate.getTime();

                        int minutes = (int) ((diff / (1000*60)) % 60);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(strToDate);
                        String hour = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        SpannableString minutesStr = new SpannableString(minutes + " min");
                        minutesStr.setSpan(new StyleSpan(Typeface.BOLD), 0, minutesStr.length(),SPAN_EXCLUSIVE_EXCLUSIVE);
                        timeInfoView.append(minutesStr);
                        timeInfoView.append(",arrival time: " +hour + "\n");
                    } catch (ParseException e){
                        e.printStackTrace();
                    }
                }
                if(!expectedTime.getPresentableDistance().equals("NoNumberOfStopsAway"))
                    timeInfoView.append(expectedTime.getPresentableDistance()  + "\n");
                if(!expectedTime.getArrivalProximityText().equals("can not track distance now"))
                    timeInfoView.append(expectedTime.getArrivalProximityText() + " miles away" + "\n");
                Log.i("timedebug====", "ArrivalProximityText: " + expectedTime.getArrivalProximityText());
                if (!expectedTime.getOriginAimedDepartureTime().equals("NoOriginAimedDepartureTime")) {
                    Log.i("timedebug====", "OriginAimDepartureTime: " + expectedTime.getOriginAimedDepartureTime());
                    try{
                        strToDate = format.parse(expectedTime.getOriginAimedDepartureTime());
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(strToDate);
                        String DepartTime = String.format("%02d:%02d",cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
                        timeInfoView.append("Departure Time: " + DepartTime +"\n");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                timeInfoView.append("\n");
            }

            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    /**
     * return a textview that use to display bus time
     * @param viewNum a number indicator that tells to use which view
     * @return a textview
     */
    public TextView getBusInfoView(){
        if ( textViewIndicator == 1){
            mBusTimeTextview1.setText("");
            mBusTimeTextview1.setVisibility(View.VISIBLE);
            mBusTimeCardview1.setVisibility(View.VISIBLE);
            return  mBusTimeTextview1;
        }
        if ( textViewIndicator == 2){
            mBusTimeTextview2.setText("");
            mBusTimeTextview2.setVisibility(View.VISIBLE);
            mBusTimeCardview2.setVisibility(View.VISIBLE);
            return  mBusTimeTextview2;
        }
        if ( textViewIndicator == 3){
            mBusTimeTextview3.setText("");
            mBusTimeTextview3.setVisibility(View.VISIBLE);
            mBusTimeCardview3.setVisibility(View.VISIBLE);
            return  mBusTimeTextview3;
        }
        return null;
    }

    public void incTextviewIndicator(){
        textViewIndicator++;
    }

    public int getTextviewIndicator(){
        return textViewIndicator;
    }

    public void addToFavorite(){
        ContentValues cv = new ContentValues();
        String stopCode = null;
        DatabaseHandler db = new DatabaseHandler(this);
        for (TimeInfo item : mSaveForFavoriteList){
            String str = item.getStopNumber();
            for (int i = 0; i < str.length(); i++){
                if(Character.isDigit(str.charAt(i))){
                    stopCode = str.substring(i,str.length());
                    break;
                }
            }
            cv.put(BusContract.BusEntry.COLUMN_BUS_STOP_CODE,stopCode);
            cv.put(BusContract.BusEntry.COLUMN_BUS_STOP_GROUP,item.getStopPointName());
        }
        mRowID = db.insertToDatabase(cv);
    }

    public void removeFromFavorite(){
        DatabaseHandler db = new DatabaseHandler(this);
        Intent intent = getIntent();
        db.deleteFromDatabase(mRowID);
    }

    /**
     * sets the favorite icon, if the item is check then sets uncheck and change to responding icon
     * @param item
     */
    public void setFavorite(MenuItem item){
        if (item.isChecked()){
            item.setChecked(false);
            item.setIcon(R.drawable.ic_favorite_border_black_24dp);
        }else {
            item.setChecked(true);
            item.setIcon(R.drawable.ic_favorite_yellow_24dp);
            Drawable drawable = item.getIcon();
            drawable.setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        }
    }
}
