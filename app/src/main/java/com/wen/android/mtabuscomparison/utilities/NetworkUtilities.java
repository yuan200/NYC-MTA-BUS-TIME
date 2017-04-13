package com.wen.android.mtabuscomparison.utilities;

import android.net.Uri;
import android.util.Log;

import com.wen.android.mtabuscomparison.model.TimeInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by yuan on 4/11/2017.
 */

public final class NetworkUtilities {
    private static final String TAG = "JsonObject";
    private static final String SIRI_BUSTIME_URL =
            "http://bustime.mta.info/api/siri/stop-monitoring.json";
    private static final String api_key = "key";
    static final String API_KEY =
            "272e0b38-54a4-485f-875a-e9b1460a9509";
    static final String MONITORING_REF ="MonitoringRef";

    /**
     * Builds the URL used to talk to the MTA BUS API using a few parameter
     * @param busStopCode the bus stop code that the user want to check.
     * @return the URL to use to query the bus time
     */
    public static URL buildUrl(String busStopCode){
        Uri builtUri = Uri.parse(SIRI_BUSTIME_URL).buildUpon()
                .appendQueryParameter(api_key, API_KEY)
                .appendQueryParameter(MONITORING_REF, busStopCode)
                .build();
        URL url = null;
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from..
     * @return the contents of the HTTP response.
     * @throws java.io.IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static TimeInfo getSpecificItem(String result,String item){
        TimeInfo timeInfoObject = new TimeInfo();
        try{
            String[] retrievedBusinfo = new String[6];
            JSONObject jsonObject = new JSONObject(result);
            Log.v(TAG,"json ojbect0: " + jsonObject);
            JSONObject siriJsonObject = jsonObject.getJSONObject("Siri");
            Log.v(TAG,"json ojbect1: " + siriJsonObject);
            JSONObject serveicDeliveryObject =siriJsonObject.getJSONObject("ServiceDelivery");
            Log.v(TAG,"json ojbect2: " + serveicDeliveryObject);
            JSONArray stopMonitoringDelivery = serveicDeliveryObject.getJSONArray("StopMonitoringDelivery");
            Log.v(TAG,"json ojbect3: " + stopMonitoringDelivery);
            JSONObject stopMonitoringDeliveryJsonObject = stopMonitoringDelivery.getJSONObject(0);
            Log.v(TAG,"json ojbect4: " + stopMonitoringDeliveryJsonObject);
            JSONArray monitorStopVisitJsonArray = stopMonitoringDeliveryJsonObject.getJSONArray("MonitoredStopVisit");
            Log.v(TAG,"json ojbect5: " + monitorStopVisitJsonArray);
            JSONObject monitoredVehicleJourneyJsonObject = monitorStopVisitJsonArray.getJSONObject(0);
            Log.v(TAG,"json ojbect6: " + monitoredVehicleJourneyJsonObject);
            JSONObject monitoredVehicleJourneyJsonObject1 = monitoredVehicleJourneyJsonObject.getJSONObject("MonitoredVehicleJourney");
            Log.v(TAG,"json ojbect7: " + monitoredVehicleJourneyJsonObject1);
            if (monitoredVehicleJourneyJsonObject1.has("OriginAimedDepartureTime")){
                retrievedBusinfo[3] = monitoredVehicleJourneyJsonObject1.getString("OriginAimedDepartureTime");
                timeInfoObject.setOriginAimedDepartureTime(monitoredVehicleJourneyJsonObject1.getString("OriginAimedDepartureTime"));
            }else{
                timeInfoObject.setOriginAimedDepartureTime("NoOriginAimedDepartureTime");
                retrievedBusinfo[3] = "NoOriginAimedDepartureTime";
            }
            retrievedBusinfo[4] = monitoredVehicleJourneyJsonObject1.getString("PublishedLineName");
            timeInfoObject.setPublishedLineName(monitoredVehicleJourneyJsonObject1.getString("PublishedLineName"));
            Log.v("q18tag","json ojbect8: " + retrievedBusinfo[4]);

            JSONObject monitoredCallJsonObject = monitoredVehicleJourneyJsonObject1.getJSONObject("MonitoredCall");

            retrievedBusinfo[0] = monitoredCallJsonObject.getString("StopPointName");
            timeInfoObject.setStopPointName(monitoredCallJsonObject.getString("StopPointName"));
            if (monitoredCallJsonObject.has("ArrivalProximityText")){
                retrievedBusinfo[2] = monitoredCallJsonObject.getString("ArrivalProximityText");
                timeInfoObject.setArrivalProximityText(monitoredCallJsonObject.getString("ArrivalProximityText"));
            }else{
                timeInfoObject.setArrivalProximityText("can not track distance now");
                retrievedBusinfo[2] = "can not track distance now";
            }

            //check if the return object has ExpectedArrivalTime, if no then doesn't need to calculate the remain time
            if (monitoredCallJsonObject.has("ExpectedArrivalTime")){
                timeInfoObject.setExpectedArrivalTime(monitoredCallJsonObject.getString("ExpectedArrivalTime"));
                retrievedBusinfo[1] = monitoredCallJsonObject.getString("ExpectedArrivalTime");
            } else{
                timeInfoObject.setExpectedArrivalTime("NoExpectedItem");
                retrievedBusinfo[1]  ="NoExpectedItem";
            }
            Iterator<String> keys = monitoredCallJsonObject.keys();

            JSONObject distanceJsonObject = monitoredCallJsonObject.getJSONObject("Extensions");
            JSONObject presentableDistanceJsonObject = distanceJsonObject.getJSONObject("Distances");
            if (presentableDistanceJsonObject.has("PresentableDistance")){
                retrievedBusinfo[5] = presentableDistanceJsonObject.getString("PresentableDistance");
                timeInfoObject.setPresentableDistance( presentableDistanceJsonObject.getString("PresentableDistance"));
                timeInfoObject.setStopsFromCall(presentableDistanceJsonObject.getString("PresentableDistance"));
                Log.d("jsonkey","json key!!!： " + presentableDistanceJsonObject.getString("PresentableDistance"));
                Log.d("jsonkey","json key!!!： " + presentableDistanceJsonObject.getString("StopsFromCall"));
            } else{
                retrievedBusinfo[5]  ="NoNumberOfStopsAway";
                timeInfoObject.setPresentableDistance("NoNumberOfStopsAway");
            }

            return timeInfoObject;
        } catch(JSONException e){
            e.printStackTrace();
        }
        return null;

    }


}