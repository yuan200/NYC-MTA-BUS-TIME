package com.wen.android.mtabuscomparison.stop;

import java.util.List;

/**
 * Created by yuan on 4/20/2017.
 */

public class BusDirection {
    private List<StopInfo> mDirection0;
    private List<StopInfo> mDirection1;

    public List<StopInfo> getDirection0() {
        return mDirection0;
    }

    public void setDirection0(List<StopInfo> direction0) {
        mDirection0 = direction0;
    }

    public List<StopInfo> getDirection1() {
        return mDirection1;
    }

    public void setDirection1(List<StopInfo> direction1) {
        mDirection1 = direction1;
    }
}
