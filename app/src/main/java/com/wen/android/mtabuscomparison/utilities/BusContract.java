package com.wen.android.mtabuscomparison.utilities;

import android.provider.BaseColumns;

/**
 * Created by yuan on 4/11/2017.
 */

public class BusContract {
    public static final class BusEntry implements BaseColumns {
        public static final  String TABLE_NAME = "Buslist";
        public static final String TABLE_NAME_ALL_BUS = "allBus";
        public static final String COLUMN_BUS_STOP_CODE = "busStopCode";
        public static final String COLUMN_BUS_STOP_CODE2 = "busStopCode2";
        public static final String COLUMN_BUS_STOP_CODE3 = "busStopCode3";
        public static final String COLUMN_BUS_LINE = "busLine";
        public static final String COLUMN_BUS_NAME = "busName";
        public static final String COLUMN_BUS_STOP_GROUP = "groupName";
        public static final String COLUMN_BUS_STOP_LAT = "lat";
        public static final String COLUMN_BUS_STOP_LNG = "lng";
        public static final String  COLUMN_TIMESTAMP = "timestamp";
    }
}
