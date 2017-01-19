package com.globalapp.maldivestravel;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Smiley on 10/10/2016.
 */

  class DBTracking extends SQLiteOpenHelper {

     private static  String TABLE_NAME ="Places";
     private static String DBName="Tracking.db";
      DBTracking(Context context) {

        super(context, Environment.getExternalStorageDirectory()+ File.separator+"Maldives"
                +File.separator+DBName,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TABLE_NAME = new SimpleDateFormat("EEE_MMM_d_yyyy").format(new Date());
        db.execSQL("Create table IF NOT EXISTS "+TABLE_NAME+" (id INTEGER primary key,CustomerName TEXT" +
                ",Latitude TEXT" +
                ",Longitude TEXT" +
                ",Speed TEXT" +
                ",Time TEXT" +
                ",Driver TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if EXISTS "+TABLE_NAME);
        onCreate(db);
    }

     void addRow(String CustomerName,String Driver,String Lat,String Long,String Speed,String Time){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        Values.put("CustomerName", CustomerName);
        Values.put("Driver", Driver);
        Values.put("Latitude", Lat);
        Values.put("Longitude", Long);
        Values.put("Speed", Speed);
        Values.put("Time", Time);
        db.insert(TABLE_NAME, null, Values);

    }
}
