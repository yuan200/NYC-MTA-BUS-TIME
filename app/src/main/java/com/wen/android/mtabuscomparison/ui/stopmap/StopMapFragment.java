package com.wen.android.mtabuscomparison.ui.stopmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.common.permission.MyPermission;
import com.wen.android.mtabuscomparison.common.permission.PermissionHelper;
import com.wen.android.mtabuscomparison.feature.stop.BusDatabase;
import com.wen.android.mtabuscomparison.feature.stop.Stop;
import com.wen.android.mtabuscomparison.feature.stop.StopInfo;
import com.wen.android.mtabuscomparison.ui.routesview.RoutesViewActivity;
import com.wen.android.mtabuscomparison.ui.search.SearchActivity;
import com.wen.android.mtabuscomparison.ui.stopmonitoring.StopMonitoringActivity;
import com.wen.android.mtabuscomparison.util.SearchHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by yuan on 4/10/2017.
 */

public class StopMapFragment extends Fragment
        implements StopMapViewMvc.OnMovedMapListener,
        StopMapViewMvc.Listener,
        StopMapViewMvc.OnStartSearchListener,
        StopMapViewMvc.MapListener, PermissionHelper.Listener {

    private final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 199;
    private PermissionHelper mPermissionHelper;
    private StopMapViewMvc mStopMapView;

    public StopMapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionHelper = new PermissionHelper(getActivity());
        if (!mPermissionHelper.hasPermission(MyPermission.FINE_LOCATION)) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mStopMapView = new StopMapViewMvcImpl(inflater, container, getChildFragmentManager(), this, this);

        setHasOptionsMenu(true);

        mStopMapView.getSearchBar().setOnEditorActionListener((v, actionId, event) -> {
            displaySearchResult(v.getText().toString());
            return true;
        });
        return mStopMapView.getRootView();
    }

    @Override
    public void onResume() {
        mStopMapView.onResume();
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mStopMapView.registerMapListener(this);
        mStopMapView.registerListener(this);
        mPermissionHelper.registerListener(this);
    }

    @Override
    public void onPause() {
        mStopMapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mStopMapView.unregisterMapListener(this);
        mStopMapView.unregisterListener(this);
        mPermissionHelper.unregisterListener(this);
    }

    @Override
    public void onDestroyView() {
        mStopMapView.onDestroy();
        super.onDestroyView();
    }

    /**
     * start a new activity and display the search result
     */
    public void displaySearchResult(String userInput) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, userInput);
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        SearchHandler searchHandler = new SearchHandler(userInput);
        if (searchHandler.keywordType() == 0) {
            String[] stopcodeArray = new String[1];
            //get the bus code from the user input
            stopcodeArray[0] = userInput;
            if (stopcodeArray[0] == null) {
                return;
            }
            Intent intent = new Intent(getActivity(), StopMonitoringActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray);
            startActivity(intent);
        } else {
            String routeEntered = userInput.toUpperCase();
            Intent intent = new Intent(getActivity(), RoutesViewActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered);
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionHelper.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    /**
     * get the current location and then find nearby stop from database
     */
    private void findNearByStop(Location location) {
        final ArrayList<StopInfo> stopList = new ArrayList<>();
        //change radius_in_meters if we want to change the range of the nearby stop
        double radius_in_meters = 800;
        double radius_pls = radius_in_meters;
        double radius_neg = 0 - radius_in_meters;
        double coef_plus = radius_pls * 0.0000089;
        double coef_neg = radius_neg * 0.0000089;
        double new_latitude1, new_latitude2, new_longitude1, new_longitude2;

        double current_latitude = location.getLatitude();
        double current_longitude = location.getLongitude();

        new_latitude1 = current_latitude + coef_neg;
        new_latitude2 = current_latitude + coef_plus;

        new_longitude1 = current_longitude + coef_plus / Math.cos(current_latitude * 0.018);
        new_longitude2 = current_longitude + coef_neg / Math.cos(current_latitude * 0.018);

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Stop> bustList = BusDatabase.Companion.getInstance(getContext()).allBusDao().getStopsInRange(new_latitude1, new_latitude2, new_longitude2, new_longitude1);
            for (Stop bus : bustList) {
                StopInfo stop = new StopInfo();
                Location tempLocation = new Location("tempLocation");
                tempLocation.setLatitude(bus.getStopLat());
                tempLocation.setLongitude(bus.getStopLon());

                float distance = location.distanceTo(tempLocation);
                stop.setStopCode(bus.getStopId());
                stop.setIntersections(bus.getStopName());
                stop.setRoutes(bus.getRouteId());
                stop.setLocation(tempLocation);
                stop.setDistance(distance);
                stopList.add(stop);
                Collections.sort(stopList);
            }
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    mStopMapView.removeMarkers();
                    for (StopInfo st : stopList) {
                        mStopMapView.addStopMarker(st);
                    }

                    updateNearbyStopList(stopList);
                });
            }
        });

    }

    private void updateNearbyStopList(List<StopInfo> nearbyStopList) {
        mStopMapView.bindStopInfo(nearbyStopList);
    }

    @Override
    public void onMovedMap(LatLng latLng) {
        Location location = new Location("");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        findNearByStop(location);
    }

    @Override
    public void onStopClicked(@NotNull StopInfo stop) {
        String[] stopcodeArray = new String[1];
        stopcodeArray[0] = stop.getStopCode();
        Intent intent = new Intent(getContext(), StopMonitoringActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray);
        startActivity(intent);
    }

    @Override
    public void onStartSearch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivityForResult(
                    new Intent(getContext(), SearchActivity.class),
                    SEARCH_ACTIVITY_REQUEST_CODE,
                    ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle()
            );
        } else {
            startActivityForResult(
                    new Intent(getContext(), SearchActivity.class),
                    SEARCH_ACTIVITY_REQUEST_CODE
            );
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                LatLng latlng = (LatLng) data.getParcelableExtra(getString(R.string.SEARCH_RESULT_POINT));
                mStopMapView.moveCameraTo(latlng);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady() {
        if (mPermissionHelper.hasPermission(MyPermission.FINE_LOCATION)) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                Timber.i("last known location: " + location);
                if (location == null) {
                    location = new Location("dummy");
                    location.setLatitude(40.74859491079061);
                    location.setLongitude(-73.98564294403914);
                }
                mStopMapView.addCurrentLocationMarker(location);
                findNearByStop(location);
            });
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull PermissionHelper.PermissionsResult result) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (result.getGranted() != null && result.getGranted().size() > 0 && result.getGranted().contains(MyPermission.FINE_LOCATION)) {
                    Timber.i("permission granted");
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location == null) {
                            Timber.i("dummy??");
                            location = new Location("dummy");
                            location.setLatitude(40.74859491079061);
                            location.setLongitude(-73.98564294403914);
                        } else {
                            Timber.i("not dummy!" + location.toString());
                        }
                        mStopMapView.enableMyLocationButton();
                        findNearByStop(location);
                        mStopMapView.addCurrentLocationMarker(location);
                    });
                } else {
                    Toast.makeText(getContext(), "Need Location permission", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    @Override
    public void onPermissionsRequestCancelled(int requestCode) {
    }
}

