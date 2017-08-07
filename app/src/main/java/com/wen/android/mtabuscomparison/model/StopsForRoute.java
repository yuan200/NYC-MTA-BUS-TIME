package com.wen.android.mtabuscomparison.model;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Created by yuan on 4/19/2017.
 */

public class StopsForRoute implements Comparable<StopsForRoute>{
    private String mId;
    private String mStopCode;
    private String mIntersections;
    private String mBusDirection;
    private Location mLocation;
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

    @Override
    public int compareTo(@NonNull StopsForRoute st) {
        if (distance == st.distance)
            return 0;
        else if (distance > st.distance)
            return 1;
        else
            return -1;
    }
}
