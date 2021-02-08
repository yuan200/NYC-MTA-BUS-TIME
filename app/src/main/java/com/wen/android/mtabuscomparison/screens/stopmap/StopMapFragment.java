package com.wen.android.mtabuscomparison.screens.stopmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.wen.android.mtabuscomparison.R;
import com.wen.android.mtabuscomparison.screens.routesview.RoutesViewActivity;
import com.wen.android.mtabuscomparison.screens.stopmonitoring.SearchResultActivity;
import com.wen.android.mtabuscomparison.stop.BusDatabase;
import com.wen.android.mtabuscomparison.stop.Stop;
import com.wen.android.mtabuscomparison.stop.StopInfo;
import com.wen.android.mtabuscomparison.utilities.SearchHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import timber.log.Timber;

/**
 * Created by yuan on 4/10/2017.
 */

public class StopMapFragment extends Fragment implements StopMapView.OnMovedMapListener, StopMapView.Listener {

    private final int PERMISSION_ACCESS_FINE_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private StopMapView stopMapView;

    public StopMapFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_FINE_LOCATION);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        stopMapView = new StopMapViewImpl(inflater, container, getChildFragmentManager());

        stopMapView.registerMapListener(this);
        stopMapView.registerListener(this);
        setHasOptionsMenu(true);
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                Timber.i("last known location: " + location);
                if (location == null) {
                    location = new Location("dummy");
                    location.setLatitude(40.74859491079061);
                    location.setLongitude(-73.98564294403914);
                }
                stopMapView.addCurrentLocationMarker(location);
                findNearByStop(location);

            });
        }
        return stopMapView.getRootView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopMapView.unregisterMapListener(this);
        stopMapView.unregisterListener(this);
        stopMapView = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_search_view, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    public void displaySearchResult(String userInput) {
        SearchHandler searchHandler = new SearchHandler(userInput);
        if (searchHandler.keywordType() == 0) {
            String[] stopcodeArray = new String[1];
            //get the bus code from the user input
            stopcodeArray[0] = userInput;
            if (stopcodeArray[0] == null) {
                return;
            }
            Timber.i("'displaySearchResult()");
            Intent intent = new Intent(getActivity(), SearchResultActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray);
            startActivity(intent);
        } else {
            String routeEntered = userInput.toUpperCase();
            Intent intent = new Intent(getActivity(), RoutesViewActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, routeEntered);
            startActivity(intent);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    stopMapView.enableMyLocationButton();
                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location == null) {
                            location = new Location("dummy");
                            location.setLatitude(40.74859491079061);
                            location.setLongitude(-73.98564294403914);
                        }
                        stopMapView.addCurrentLocationMarker(location);
                        findNearByStop(location);
                    });
                } else {
                    Toast.makeText(getContext(), "Need Location permission", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }
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
            List<Stop> bustList = BusDatabase.Companion.getInstance(getContext()).allBusDao() .getStopsInRange(new_latitude1, new_latitude2, new_longitude2, new_longitude1);
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
            getActivity().runOnUiThread(() -> {
                stopMapView.removeMarkers();
                int i = 0;
                for (StopInfo st : stopList) {
                    stopMapView.addStopMarker(st);
                    i++;
                }
                updateNearbyStopList(stopList);
            });

        });

    }

    private void updateNearbyStopList(List<StopInfo> nearbyStopList) {
        stopMapView.bindStopInfo(nearbyStopList);
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
        Timber.i("onStopClick(stop)");
        String[] stopcodeArray = new String[1];
        stopcodeArray[0] = stop.getStopCode();
        Intent intent = new Intent(getContext(), SearchResultActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, stopcodeArray);
        startActivity(intent);
    }
}
