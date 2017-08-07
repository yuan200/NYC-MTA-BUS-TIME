package com.wen.android.mtabuscomparison.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wen.android.mtabuscomparison.MainActivity;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.RoutesViewActivity;
import com.wen.android.mtabuscomparison.SearchResultActivity;
import com.wen.android.mtabuscomparison.handler.SearchHandler;
import com.wen.android.mtabuscomparison.model.Favorite;
import com.wen.android.mtabuscomparison.model.StopsForRoute;
import com.wen.android.mtabuscomparison.utilities.BusContract;
import com.wen.android.mtabuscomparison.utilities.BusDbHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 4/10/2017.
 */

public class SearchFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
,OnMapReadyCallback{
    private MapView mMapView;
    GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private final String TAG_LOCATION = "TAG_LOCATION";
    private SQLiteDatabase mDb;
    private RecyclerView mRecyclerView;
    private NearbyAdapter mAdapter;
    public SearchFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            /**
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
             **/
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_search, container, false);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        //gets googlemap from the mapview and does initialization stuff
        mRecyclerView = (RecyclerView) v.findViewById(R.id.nearbyRecycleView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        /**
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
         **/
        setHasOptionsMenu(true);
        BusDbHelper dbHelper = new BusDbHelper(getContext());
        mDb = dbHelper.getReadableDatabase();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        /**
         SupportMapFragment mapFragment = (SupportMapFragment)getActivity().getSupportFragmentManager()
        .findFragmentById(R.id.map);
        if(mapFragment !=null){
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().remove(mapFragment).commit();
        }
         **/
        super.onPause();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onDestroyView() {
        /**
        SupportMapFragment mapFragment = (SupportMapFragment)getActivity().getSupportFragmentManager()
        .findFragmentById(R.id.map);
        if(mapFragment !=null){
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().remove(mapFragment).commit();
        }
         **/
        mMapView.onDestroy();
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        mGoogleMap = googleMap;
        /**
        LatLng here = new LatLng(40.730823,-73.897413);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(here,16);
        mGoogleMap.addMarker(new MarkerOptions().position(here).title("test"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        mGoogleMap.animateCamera(cameraUpdate);
         **/

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            /**
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mLocationView.setText(String.valueOf(mLastLocation.getLatitude()));
            }else {
                mLocationView.setText("mLastLocation is null");
            }
             **/
            findNearByStop();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        //mLocationView.setText(location.toString());
        //findNearByStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_view,menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                displaySearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * start a new activity and display the search result
     */
    public void displaySearchResult(String userInput){
        SearchHandler searchHandler = new SearchHandler(userInput);
        if (searchHandler.keywordType() == 0){
            String[] stopcodeArray = new String[1];
            //get the bus code from the user input
            stopcodeArray[0] = userInput;
            if (stopcodeArray[0] == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT,stopcodeArray);
            startActivity(intent);
        }else{
            String routeEntered = userInput.toUpperCase();
            Intent intent = new Intent(getActivity(), RoutesViewActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered);
            startActivity(intent);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    findNearByStop();
                }else{
                    Toast.makeText(getContext(),"Need Location permission",Toast.LENGTH_LONG)
                            .show();
                }
            break;
        }
    }


    /**
     * get the current location and then find nearby stop from database
     */
    public void findNearByStop(){
        final ArrayList<StopsForRoute> stopList = new ArrayList<>();
        double current_latitude = 0, current_longitude = 0;
        //change radius_in_meters if we want to change the range of the nearby stop
        double radius_in_meters = 500;
        double radius_pls = radius_in_meters;
        double radius_neg = 0 - radius_in_meters;
        double coef_plus = radius_pls * 0.0000089;
        double coef_neg = radius_neg * 0.0000089;
        double new_latitude1, new_latitude2, new_longitude1, new_longitude2;
        // The if block finds the current location
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        current_latitude = mLastLocation.getLatitude();
                        current_longitude = mLastLocation.getLongitude();
                        LatLng here = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(here,16);
                        mGoogleMap.clear();
                        mGoogleMap.addMarker(new MarkerOptions().position(here).title("You are here")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_36dp)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(here));
                        mGoogleMap.animateCamera(cameraUpdate);

                    }
        new_latitude1 = current_latitude + coef_neg;
        new_latitude2 = current_latitude + coef_plus;

        new_longitude1 = current_longitude + coef_plus/Math.cos(current_latitude * 0.018);
        new_longitude2 = current_longitude + coef_neg/Math.cos(current_latitude * 0.018);

        //prepare to read from database
        //define a projection that specifies which columns from the database we will use
        String[] projection = {
                BusContract.BusEntry.COLUMN_BUS_STOP_CODE,
                BusContract.BusEntry.COLUMN_BUS_NAME,
                BusContract.BusEntry.COLUMN_BUS_STOP_LAT,
                BusContract.BusEntry.COLUMN_BUS_STOP_LNG,
        };

        //Filter results WHERE lat BETWEEN latitude1 AND latitude2 AND lng BETWEEN lng1 AND lng2
        String selection = BusContract.BusEntry.COLUMN_BUS_STOP_LAT
                + " BETWEEN ? AND ? AND "
                + BusContract.BusEntry.COLUMN_BUS_STOP_LNG
                + " BETWEEN ? AND ? ";
        String[] selectionArgs = {String.valueOf(new_latitude1),
                String.valueOf(new_latitude2),
                String.valueOf(new_longitude1),
                String.valueOf(new_longitude2)
        };

        Cursor cursor = mDb.query(
                BusContract.BusEntry.TABLE_NAME_ALL_BUS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int i = 0;
        while(cursor.moveToNext()) {
            StopsForRoute stop = new StopsForRoute();
            i++;
            Location tempLocation = new Location("tempLocation");
            tempLocation.setLatitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_LAT))));
            tempLocation.setLongitude(Double.parseDouble(cursor.getString(cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_LNG))));
            /**
            LatLng nearbyStopLatLng = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
            .position(nearbyStopLatLng)
            .title(cursor.getString(cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_NAME))));
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(getActivity(),"haha",Toast.LENGTH_LONG).show();

                    return false;
                }
            });
             **/
            float distance = mLastLocation.distanceTo(tempLocation);
            stop.setStopCode(cursor.getString(cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_STOP_CODE)));
            stop.setIntersections(cursor.getString(cursor.getColumnIndex(BusContract.BusEntry.COLUMN_BUS_NAME)));
            stop.setLocation(tempLocation);
            stop.setDistance(distance);
            stopList.add(stop);
            Collections.sort(stopList);
        }
        int index = 0;
        for (StopsForRoute st : stopList){
        //for (int ii = 0; ii< stopList.size(); ii++){
            final int stopIndex = index++;
            final String stopName = st.getIntersections();
            LatLng nearbyStopLatLng = new LatLng(st.getLocation().getLatitude(),st.getLocation().getLongitude());
            //LatLng nearbyStopLatLng = new LatLng(stopList.get(ii).getLocation().getLatitude(),stopList.get(ii).getLocation().getLongitude());
            mGoogleMap.addMarker(new MarkerOptions()
            .position(nearbyStopLatLng)
            .title(st.getIntersections()));
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    for (int ii = 0; ii<stopList.size();ii++){
                        int lat = Double.compare(marker.getPosition().latitude, stopList.get(ii).getLocation().getLatitude());
                        int Lon = Double.compare(marker.getPosition().longitude, stopList.get(ii).getLocation().getLongitude());
                        if (lat == 0 & Lon == 0){
                            mRecyclerView.getLayoutManager().scrollToPosition(ii);
                        }
                    }
                    return false;
                }
            });
        }

        updateNearbyList(stopList);
        cursor.close();
    }

    public GoogleMap.OnMarkerClickListener onMarkerClick(final int index){
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mRecyclerView.getLayoutManager().scrollToPosition(index);
                return false;
            }
        };
    }

    private class NearByStopHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
        private TextView mStopCodeView;
        private TextView mStopNameView;
        private StopsForRoute mFavorite;
        public NearByStopHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_bus,parent, false));
            itemView.setOnClickListener(this);
            mStopCodeView = (TextView)itemView.findViewById(R.id.item_read_stop_code_1);
            mStopNameView = (TextView) itemView.findViewById(R.id.item_read_group);
        }

        public void bind(StopsForRoute favorite){
            mFavorite = favorite;
            mStopNameView.setText(mFavorite.getIntersections());
            mStopCodeView.setText(mFavorite.getStopCode());
        }

        @Override
        public void onClick(View v) {
            String[] stopcodeArray = new String[1];
            stopcodeArray[0] = mFavorite.getStopCode();

            Intent intent = new Intent(getActivity(),SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray);
            startActivity(intent);
        }
    }

    private class NearbyAdapter extends RecyclerView.Adapter<NearByStopHolder>{
        private List<StopsForRoute> mFavorites;
        public NearbyAdapter(List<StopsForRoute> favorites){
            mFavorites = favorites;
        }

        @Override
        public NearByStopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new NearByStopHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(NearByStopHolder holder, int position) {
            StopsForRoute favorite = mFavorites.get(position);
            holder.bind(favorite);
        }

        @Override
        public int getItemCount() {
            return mFavorites.size();
        }
    }
    private void updateNearbyList(List<StopsForRoute> nearbyFavorite){
        List<StopsForRoute> favorites = nearbyFavorite;
        for (StopsForRoute fav : favorites)
        mAdapter = new NearbyAdapter(favorites);
        mRecyclerView.setAdapter(mAdapter);
    }
}
