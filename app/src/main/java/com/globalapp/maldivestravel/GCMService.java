package com.globalapp.maldivestravel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.kinvey.android.push.KinveyGCMService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Smiley on 7/6/2016.
 */
public class GCMService extends KinveyGCMService {

    SharedPreferences sharedPreferences;

    @Override
    public void onMessage(String message) {

        sharedPreferences = getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);
        String date = new SimpleDateFormat("EEE, MMM d, yyyy").format(new Date());



       try {
            JSONObject details = new JSONObject(message);
            String msg = details.getString("message");
            if (msg.equals("New Order")) {
                Shared(message);
                displayNotification(msg,1);

            } else {

                displayNotification(msg,0);

                NotifyDBConnection db = new NotifyDBConnection(this);
                db.InsertRow(msg, date);
            }

        } catch (JSONException e) {
           Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onError(String error) {
        displayNotification(error,0);
    }

    @Override
    public void onDelete(String deleted) {
        displayNotification(deleted,0);
    }

    @Override
    public void onRegistered(String gcmID) {
        displayNotification(gcmID,0);
    }

    @Override
    public void onUnregistered(String oldID) {
        displayNotification(oldID,0);
    }


    //This method will return the WakefulBroadcastReceiver class you define in the next step
    public Class getReceiver() {
        return GCMReceiver.class;
    }


    private void displayNotification(String message,int id) {
        Intent starter = null;
        switch (id) {
            case (0):
                starter   = new Intent(getApplicationContext(), NotificationActivity.class);
                break;
            case (1):
                starter   = new Intent(getApplicationContext(), CurrentActivity.class);
                break;
        }

        PendingIntent Pending = PendingIntent.getActivity(getApplicationContext(), 0, starter, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(getApplicationContext().getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentIntent(Pending)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void Shared(String msg) {
        try {
            JSONObject details = new JSONObject(msg);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Customer_Name", details.getString("Customer_Name"));
            editor.putString("Customer_Phone_No", details.getString("Customer_Phone_No"));
            editor.putString("Customer_Location", details.getString("Customer_Location"));
            editor.putString("Customer_Destination", details.getString("Customer_Destination"));
            editor.putString("Date", details.getString("Date"));
            editor.putString("Time", details.getString("Time"));
            editor.putString("Note", details.getString("Note"));
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}


