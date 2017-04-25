package com.wen.android.mtabuscomparison.model;

import java.util.List;

/**
 * Created by yuan on 4/20/2017.
 */

public class BusDirection {
    private List<StopsForRoute> mDirection0;
    private List<StopsForRoute> mDirection1;

    public List<StopsForRoute> getDirection0() {
        return mDirection0;
    }

    public void setDirection0(List<StopsForRoute> direction0) {
        mDirection0 = direction0;
    }

    public List<StopsForRoute> getDirection1() {
        return mDirection1;
    }

    public void setDirection1(List<StopsForRoute> direction1) {
        mDirection1 = direction1;
    }
}
