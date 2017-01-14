package com.globalapp.maldivestravel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Smiley on 7/6/2016.
 */
public class NotifyDBConnection extends SQLiteOpenHelper {
    public NotifyDBConnection(Context context) {
        super(context, "Notifications.db", null, 1);
//        super(context, Environment.getExternalStorageDirectory()+ File.separator+"Car.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table IF NOT EXISTS Notifications (id INTEGER primary key,notification TEXT,date TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("Drop table if EXISTS Notifications");
        onCreate(db);

    }

    public void InsertRow(String notification, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues Values = new ContentValues();
        Values.put("notification", notification);
        Values.put("date", date);
        db.insert("Notifications", null, Values);
    }

    public ArrayList getNotification() {
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Notifications", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            array_list.add(res.getString(res.getColumnIndex("notification")));


            res.moveToNext();
        }
        return array_list;
    }

    public ArrayList getDate() {
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Notifications", null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {

            array_list.add(res.getString(res.getColumnIndex("date")));


            res.moveToNext();
        }
        return array_list;
    }
}
