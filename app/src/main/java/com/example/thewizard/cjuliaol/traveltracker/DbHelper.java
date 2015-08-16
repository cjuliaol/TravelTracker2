package com.example.thewizard.cjuliaol.traveltracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cjuliaol on 16-Aug-15.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME ="traveltracker.db";
    private static final int DATABASE_VERSION =1;

    private static final String MEMORIES_TABLE="memories";
    private static final String COLUMN_LATITUDE="latitude";
    private static final String COLUMN_LONGITUDE="longitude";
    private static final String COLUMN_CITY="city";
    private static final String COLUMN_COUNTRY="country";
    private static final String COLUMN_NOTES="notes";
    private static final String COLUMN_ID="_id";
    private static DbHelper singleton =null;

    public static DbHelper getInstance(Context context){

        if( singleton == null) {
          singleton = new DbHelper(context.getApplicationContext());
        }

        return  singleton;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL("CREATE TABLE " +MEMORIES_TABLE + " ( " +
               COLUMN_ID +  " INTEGER PRIMARY KEY AUTOINCREMENT," +
               COLUMN_LATITUDE +" DOUBLE, " +
               COLUMN_LONGITUDE+ " DOUBLE, " +
               COLUMN_CITY +" TEXT, " +
               COLUMN_COUNTRY +" TEXT, " +
               COLUMN_NOTES +" TEXT  )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //This not for production
        db.execSQL("DROP TABLE IF EXISTS "+ MEMORIES_TABLE);
        onCreate(db);

    }
}
