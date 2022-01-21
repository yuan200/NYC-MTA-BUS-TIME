package com.wen.android.mtabuscomparison.util;

import android.net.Uri;

import com.wen.android.mtabuscomparison.BusApplication;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDirection;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import timber.log.Timber;

/**
 * Created by yuan on 4/11/2017.
 */

public final class NetworkUtilities {
    //for MTA siri
    private static final String TAG = "JsonObject";
    private static final String SIRI_BUSTIME_URL =
            "http://bustime.mta.info/api/siri/stop-monitoring.json";
    private static final String api_key = "key";
    //todo move api key
    static final String API_KEY = BusApplication.Companion.getInstance().getString(R.string.mta_bus_api_key);
    static final String MONITORING_REF ="MonitoringRef";

    //for MTA one bus away
    private static final String[] OBA_BUSTIME_URL ={
            "http://bustime.mta.info/api/where/stops-for-route/MTABC_",
            "http://bustime.mta.info/api/where/stops-for-route/MTA NYCT_",
            "http://bustime.mta.info/api/where/stops-for-route/MTA_"
    };

    private static final String STOPS_FOR_ROUTH =
            "stops-for-route/";
    private static final String JSON_FORMAT = ".json";

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
            Timber.i(url.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Builds the URL used to talk to the ONE BUS AWAY API
     * @param route the bus route number(ex:Q18)
     * @return a URL array of size 3, because there is 3 different agency, we need to have 3 url to check
     */
    public static URL[] oneBusAwayBuildUrl(String route){
        URL[] returnURL = new URL[3];
        for (int i = 0; i < 3; i++){
            String newOBAUrl = OBA_BUSTIME_URL[i] + route + JSON_FORMAT;
            Uri buildOBAuri = Uri.parse(newOBAUrl).buildUpon()
                    .appendQueryParameter(api_key, API_KEY)
                    .build();
            URL url = null;
            try{
                url = new URL(buildOBAuri.toString());
            } catch (MalformedURLException e){
                e.printStackTrace();
            }
            returnURL[i] = url;
        }
        return returnURL;
    }

    /**
     * This method returns the entire result from the HTTP response.
     * @param url The URL to fetch the HTTP response from..
     * @return the contents of the HTTP response.
     * @throws java.io.IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Timber.i(url.toString());
        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){
                throw new IOException(urlConnection.getResponseMessage() +
                ":errorchecking: with " +
                url);
            }
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

    /**
     * read info from json
     * @param result
     * @return
     */
    public static BusDirection getStopListForRoute(String result){
//        BusDirection busDirection = new BusDirection();
//        ArrayList<StopInfo> stopInfoList0 = new ArrayList<>();
//        ArrayList<StopInfo> stopInfoList1 = new ArrayList<>();
//        try{
//            JSONObject rootJsonObject = new JSONObject(result);
//            String code = rootJsonObject.getString("code");
//            /**if code does not equal to 200 then something went wrong(ex incorrect route number), display a error message and don't let it crash the app
//            *
//             **/
//            if (!code.equals("200")){
//                StopInfo errorStop = new StopInfo();
//                errorStop.setId("error");
//                stopInfoList0.add(errorStop);
//                stopInfoList1.add(errorStop);
//                busDirection.setDirection0(stopInfoList0);
//                busDirection.setDirection1(stopInfoList1);
//                Log.i("error checking: " , "handle error success");
//                return busDirection;
//            }
//            JSONObject dataJsonObject = rootJsonObject.getJSONObject("data");
//            JSONArray stopGroupingsJsonObject = dataJsonObject.getJSONArray("stopGroupings");
//            JSONObject stopGoupingsJsonObject1 = stopGroupingsJsonObject.getJSONObject(0);
//            JSONArray stopGroupsJsonObject = stopGoupingsJsonObject1.getJSONArray("stopGroups");
//            //stopGroups sub object 1
//            JSONObject stopGroupsSubObject0 = stopGroupsJsonObject.getJSONObject(0);
//
//            JSONObject nameJsonObject = stopGroupsSubObject0.getJSONObject("name");
//
//            //direction
//            JSONArray stopIdsJsonArray = stopGroupsSubObject0.getJSONArray("stopIds");
//
//            //stop id
//            for (int i = 0; i< stopIdsJsonArray.length(); i++){
//                StopInfo stop = new StopInfo();
//                String stopIds = stopIdsJsonArray.getString(i);
//                Log.i("json array!!!", "json array: " + stopIds);
//                stop.setId(stopIds);
//                stop.setBusDirection(nameJsonObject.getString("name"));
//                stopInfoList0.add(stop);
//            }
//
//            // stopGroups sub object 2
//            JSONObject stopGroupsSubObject1 = stopGroupsJsonObject.getJSONObject(1);
//            JSONObject nameJsonObject2 = stopGroupsSubObject1.getJSONObject("name");
//            JSONArray stopIdsJsonArray2 = stopGroupsSubObject1.getJSONArray("stopIds");
//
//            for (int i = 0; i< stopIdsJsonArray2.length(); i++){
//                StopInfo stop = new StopInfo();
//                String stopIds = stopIdsJsonArray2.getString(i);
//                Log.i("json array!!!", "json array: " + stopIds);
//                stop.setId(stopIds);
//                stop.setBusDirection(nameJsonObject2.getString("name"));
//                stopInfoList1.add(stop);
//            }
//
//            JSONArray stopsJsonArray = dataJsonObject.getJSONArray("stops");
//            for (int i = 0; i < stopsJsonArray.length(); i++){
//                JSONObject stopObject = stopsJsonArray.getJSONObject(i);
//                String stopid = stopObject.getString("id");
//                for (int j = 0; j < stopInfoList0.size(); j++){
//                    StopInfo stop = stopInfoList0.get(j);
//                    if (stopid.equals(stop.getId())){
//                        stop.setStopCode(stopObject.getString("code"));
//                        stop.setIntersections(stopObject.getString("name"));
//                        stopInfoList0.set(j, stop);
//                    }
//                }
//                for (int j = 0; j < stopInfoList1.size(); j++){
//                    StopInfo stop = stopInfoList1.get(j);
//                    if (stopid.equals(stop.getId())){
//                        stop.setStopCode(stopObject.getString("code"));
//                        stop.setIntersections(stopObject.getString("name"));
//                        stopInfoList1.set(j, stop);
//                    }
//                }
//            }
//
//            busDirection.setDirection0(stopInfoList0);
//            busDirection.setDirection1(stopInfoList1);
//
//
//        } catch (JSONException e){
//            e.printStackTrace();
//        }

        return null;
    }

}