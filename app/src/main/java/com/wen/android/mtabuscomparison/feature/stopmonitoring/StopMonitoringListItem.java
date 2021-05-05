package com.wen.android.mtabuscomparison.feature.stopmonitoring;

import com.wen.android.mtabuscomparison.data.remote.bustime.VehicleLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuan on 4/11/2017.
 */

public class StopMonitoringListItem {
    private String mStopNumber;
    private String mExpectedArrivalTime;
    private int mExpectedMinute;
    private String mStopPointName;
    private String mArrivalProximityText;
    private String mExpectedDepartureTime;
    private String mPresentableDistance;
    private String mPublishedLineName;
    private String mDestinationName;
    private String mStopsFromCall;
    private Boolean mIsSuccess;
    private String mErrorMessage;
    private List<String> mNextBusTime = new ArrayList<>();
    private List<String> situationSummary = new ArrayList<>();
    private VehicleLocation mVehicleLocation;

    public StopMonitoringListItem(){
        mIsSuccess = true;
        mErrorMessage = "error";
    }

    public String getStopNumber() {
        return mStopNumber;
    }

    public void setStopNumber(String stopNumber) {
        mStopNumber = stopNumber;
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

    public String getExpectedDepartureTime() {
        return mExpectedDepartureTime;
    }

    public void setExpectedDepartureTime(String originAimedDepartureTime) {
        mExpectedDepartureTime = originAimedDepartureTime;
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

    public String getDestinationName() {
        return mDestinationName;
    }

    public void setDestinationName(String destinationName) {
        mDestinationName = destinationName;
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
        return mIsSuccess;
    }

    public void setFail(Boolean fail) {
        mIsSuccess = fail;
    }

    public List<String> getNextBusTime() {
        return mNextBusTime;
    }

    public void setNextBusTime(List<String> nextBusTime) {
        this.mNextBusTime = nextBusTime;
    }

    public List<String> getSituationSummary() {
        return situationSummary;
    }

    public void setSituationSummary(List<String> situationSummary) {
        this.situationSummary = situationSummary;
    }

    public VehicleLocation getVehicleLocation() {
        return mVehicleLocation;
    }

    public void setVehicleLocation(VehicleLocation mVehicleLocation) {
        this.mVehicleLocation = mVehicleLocation;
    }
}
