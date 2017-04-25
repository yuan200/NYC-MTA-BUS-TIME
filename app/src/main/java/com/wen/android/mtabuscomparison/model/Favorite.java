package com.wen.android.mtabuscomparison.model;

/**
 * Created by yuan on 4/13/2017.
 */

public class Favorite {
    String mRowId;
    String mGroupName;
    String mStopCode1;
    String mStopCode2;
    String mStopCode3;

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        mGroupName = groupName;
    }

    public String getStopCode1() {
        return mStopCode1;
    }

    public void setStopCode1(String stopCode1) {
        mStopCode1 = stopCode1;
    }

    public String getStopCode2() {
        return mStopCode2;
    }

    public void setStopCode2(String stopCode2) {
        mStopCode2 = stopCode2;
    }

    public String getStopCode3() {
        return mStopCode3;
    }

    public void setStopCode3(String stopCode3) {
        mStopCode3 = stopCode3;
    }

    public String getRowId() {
        return mRowId;
    }

    public void setRowId(String rowId) {
        mRowId = rowId;
    }
}
