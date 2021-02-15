package com.wen.android.mtabuscomparison.feature.stop;

import android.location.Location;
import androidx.annotation.NonNull;

/**
 * Created by yuan on 4/19/2017.
 */

public class StopInfo implements Comparable<StopInfo>{
    private String mId;
    private String mStopCode;
    private String mIntersections;
    private String mBusDirection;
    private Location mLocation;
    private String routes;
    private float distance;

    public String getStopCode() {
        return mStopCode;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setStopCode(String stopCode) {
        mStopCode = stopCode;
    }

    public String getIntersections() {
        return mIntersections;
    }

    public void setIntersections(String intersections) {
        mIntersections = intersections;
    }

    public String getBusDirection() {
        return mBusDirection;
    }

    public void setBusDirection(String busDirection) {
        mBusDirection = busDirection;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        mLocation = location;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    @Override
    public int compareTo(@NonNull StopInfo st) {
        if (distance == st.distance)
            return 0;
        else if (distance > st.distance)
            return 1;
        else
            return -1;
    }
}
