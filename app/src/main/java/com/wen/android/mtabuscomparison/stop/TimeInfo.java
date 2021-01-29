package com.wen.android.mtabuscomparison.stop;

/**
 * Created by yuan on 4/11/2017.
 */

public class TimeInfo {
    private String mStopNumber;
    private String mExpectedArrivalTime;
    private String mStopPointName;
    private String mArrivalProximityText;
    private String mOriginAimedDepartureTime;
    private String mPresentableDistance;
    private String mPublishedLineName;
    private String mDestinationName;
    private String mStopsFromCall;
    private Boolean mIsSuccess;
    private String mErrorMessage;

    public TimeInfo(){
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
}
