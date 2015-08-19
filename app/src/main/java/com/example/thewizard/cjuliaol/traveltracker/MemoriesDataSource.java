package com.example.thewizard.cjuliaol.traveltracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjuliaol on 16-Aug-15.
 */
public class MemoriesDataSource {
    private DbHelper mDbHelper;
    private SQLiteDatabase mSQLiteDatabase;

    private String[] allColumns = {
            DbHelper.COLUMN_ID, DbHelper.COLUMN_CITY,
            DbHelper.COLUMN_COUNTRY, DbHelper.COLUMN_LATITUDE,
            DbHelper.COLUMN_LONGITUDE, DbHelper.COLUMN_NOTES
    };

    public MemoriesDataSource(Context context) {
        mDbHelper = DbHelper.getInstance(context);
    }

    public void createMemory(Memory memory) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CITY, memory.city);
        values.put(DbHelper.COLUMN_COUNTRY, memory.country);
        values.put(DbHelper.COLUMN_NOTES, memory.notes);
        values.put(DbHelper.COLUMN_LATITUDE, memory.latitude);
        values.put(DbHelper.COLUMN_LONGITUDE, memory.longitude);

      memory.id =  mDbHelper.getWritableDatabase().insert(mDbHelper.MEMORIES_TABLE, null, values);

    }


    public List<Memory> getAllMemories() {
        List<Memory> memories = new ArrayList<>();
        Cursor cursor = mDbHelper.getReadableDatabase().query(mDbHelper.MEMORIES_TABLE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Memory memory = cursorToMemory(cursor);
            memories.add(memory);
            cursor.moveToNext();
        }
        cursor.close();

        return  memories;

    }

    public void updateMemory(Memory memory){
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_CITY, memory.city);
        values.put(DbHelper.COLUMN_COUNTRY, memory.country);
        values.put(DbHelper.COLUMN_NOTES, memory.notes);
        values.put(DbHelper.COLUMN_LATITUDE, memory.latitude);
        values.put(DbHelper.COLUMN_LONGITUDE, memory.longitude);

        String [] whereArgs = {String.valueOf(memory.id) };

        mDbHelper.getWritableDatabase()
                .update(DbHelper.MEMORIES_TABLE,
                        values,
                        DbHelper.COLUMN_ID + "=?",
                        whereArgs
                 );

    }

    private Memory cursorToMemory(Cursor cursor) {
        Memory memory = new Memory();
        memory.id = cursor.getLong(0);
        memory.city = cursor.getString(1);
        memory.country = cursor.getString(2);
        memory.latitude = cursor.getDouble(3);
        memory.longitude = cursor.getDouble(4);
        memory.notes = cursor.getString(5);
        return memory;
    }


}
