package com.imaginers.onirban.tourit.Class;

import android.provider.BaseColumns;


public class EventDatabase {

    //This class defines the database table and columns

    private EventDatabase(){}

    public static class DefineTable implements BaseColumns {

        public static final String TABLE_NAME = "EventData";
        public static final String COLUMN_NAME_ENAME = "e_name";
        public static final String COLUMN_NAME_DESTI = "desti";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_DES = "des";
        public static final String COLUMN_NAME_BUDGET = "budget";
        public static final String COLUMN_NAME_DEL_ID="del_id";

    }
}
