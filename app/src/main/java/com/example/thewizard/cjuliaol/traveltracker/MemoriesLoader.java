package com.example.thewizard.cjuliaol.traveltracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by cjuliaol on 21-Aug-15.
 */
public class MemoriesLoader extends DbCursorLoader {
   private  MemoriesDataSource mMemoriesDataSource;

    public MemoriesLoader(Context context, MemoriesDataSource memoriesDataSource){
          super(context);
          mMemoriesDataSource = memoriesDataSource;
    }

    @Override
    protected Cursor loadCursor() {
        return mMemoriesDataSource.allMemoriesCursor();
    }
}
