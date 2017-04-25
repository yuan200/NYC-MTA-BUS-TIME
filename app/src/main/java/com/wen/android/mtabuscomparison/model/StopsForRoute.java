package com.wen.android.mtabuscomparison.model;

/**
 * Created by yuan on 4/19/2017.
 */

public class StopsForRoute {
    private String mId;
    private String mStopCode;
    private String mIntersections;
    private String mBusDirection;

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
}
