package com.wen.android.mtabuscomparison.feature.stop;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuan on 4/13/2017.
 */

public class Buses {
    private static Buses sBuses;
    private List<StopMonitoringListItem> mBusTime;

    public static Buses get(Context context){
        if (sBuses == null){
            sBuses = new Buses(context);
        }
        return sBuses;
    }

    private Buses(Context context){
        mBusTime = new ArrayList<>();
        for (int i = 0; i <100 ; i++){
            StopMonitoringListItem t = new StopMonitoringListItem();
            t.setPublishedLineName("Bus Line: Q" + i);
            mBusTime.add(t);
        }
    }

    public List<StopMonitoringListItem> getTimeInfo(){
        return mBusTime;
    }
}
