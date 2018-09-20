package com.imaginers.onirban.tourit.Class;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class EventDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SaveEvent.db";

    public EventDatabaseOpenHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventDatabase.DefineTable.TABLE_NAME + " (" +
                    EventDatabase.DefineTable.COLUMN_NAME_ENAME + " TEXT," +
                    EventDatabase.DefineTable.COLUMN_NAME_DESTI + " TEXT,"+
                    EventDatabase.DefineTable.COLUMN_NAME_DATE + " TEXT," +
                    EventDatabase.DefineTable.COLUMN_NAME_TIME + " TEXT,"+
                    EventDatabase.DefineTable.COLUMN_NAME_DES + " TEXT," +
                    EventDatabase.DefineTable.COLUMN_NAME_BUDGET + " TEXT,"+
                    EventDatabase.DefineTable.COLUMN_NAME_DEL_ID +" TEXT )";
}
