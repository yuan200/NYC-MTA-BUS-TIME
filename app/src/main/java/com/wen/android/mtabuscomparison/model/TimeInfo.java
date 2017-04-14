package com.wen.android.mtabuscomparison.model;

import java.util.concurrent.TimeUnit;

/**
 * Created by yuan on 4/11/2017.
 */

public class TimeInfo {
    private String mExpectedArrivalTime;
    private String mStopPointName;
    private String mArrivalProximityText;
    private String mOriginAimedDepartureTime;
    private String mPresentableDistance;
    private String mPublishedLineName;
    private String mStopsFromCall;
    private Boolean mIsFail;
    private String mErrorMessage;

    public TimeInfo(){
        mIsFail = true;
        mErrorMessage = "error";
    }

    public String getExpectedArrivalTime() {
        return mExpectedArrivalTime;
    }

    public void setExpectedArrivalTime(String expectedArrivalTime) {
        mExpectedArrivalTime = expectedArrivalTime;
    }

    public String getStopPointName() {
        return mStopPointName;
    }

    public void setStopPointName(String stopPointName) {
        mStopPointName = stopPointName;
    }

    public String getArrivalProximityText() {
        return mArrivalProximityText;
    }

    public void setArrivalProximityText(String arrivalProximityText) {
        mArrivalProximityText = arrivalProximityText;
    }

    public String getOriginAimedDepartureTime() {
        return mOriginAimedDepartureTime;
    }

    public void setOriginAimedDepartureTime(String originAimedDepartureTime) {
        mOriginAimedDepartureTime = originAimedDepartureTime;
    }

    public String getPresentableDistance() {
        return mPresentableDistance;
    }

    public void setPresentableDistance(String presentableDistance) {
        mPresentableDistance = presentableDistance;
    }

    public String getPublishedLineName() {
        return mPublishedLineName;
    }

    public void setPublishedLineName(String publishedLineName) {
        mPublishedLineName = publishedLineName;
    }

    public String getStopsFromCall() {
        return mStopsFromCall;
    }

    public void setStopsFromCall(String stopsFromCall) {
        mStopsFromCall = stopsFromCall;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        mErrorMessage = errorMessage;
    }

    public Boolean getFail() {
        return mIsFail;
    }

    public void setFail(Boolean fail) {
        mIsFail = fail;
    }
}
