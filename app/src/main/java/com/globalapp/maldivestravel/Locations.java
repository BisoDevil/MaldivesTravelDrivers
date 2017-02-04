package com.globalapp.maldivestravel;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;

import android.support.v4.app.ActivityCompat;
import android.widget.Toast;


import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Smiley on 10/9/2016.
 */

public class Locations extends Service implements LocationListener {

    SharedPreferences sharedPreferences;
    final static long hours = 28800000;
    final static long min = 1000 * 60;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 1f;

    CountDownTimer timer;
    private Location GPS;


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
        locationManager.requestLocationUpdates(getProviderName(), LOCATION_INTERVAL, LOCATION_DISTANCE, this);


        StoreData();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            timer.cancel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void StoreData() {
        timer = new CountDownTimer(hours, min) {
            @Override
            public void onTick(long millisUntilFinished) {
                LocationManager Locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                GPS = Locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (GPS == null) {
                    GPS = Locationmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }


                try {
                    DBTracking dbTracking = new DBTracking(getBaseContext());
                    dbTracking.onCreate(dbTracking.getWritableDatabase());
                    String customer = sharedPreferences.getString("Customer_Phone_No", "");
                    String Driver = sharedPreferences.getString("PhoneNumber", "");
                    String Lat = String.valueOf(GPS.getLatitude());
                    String Long = String.valueOf(GPS.getLongitude());
                    String Speed = String.valueOf(GPS.getSpeed() * 3.6);
                    String Time = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date());
                    dbTracking.addRow(customer, Driver, Lat, Long, Speed, Time);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();

    }

    String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(true); // Chose if speed for first location fix is required.

        criteria.setAltitudeRequired(false); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(false); // Choose if this provider can waste money :-)

        // Provide your criteria and flag enabledOnly that tells
        // LocationManager only to return active providers.
        return locationManager.getBestProvider(criteria, true);
    }

    public void sendMessage(String message) {
        Client mKinveyClient = new Client.Builder(getApplicationContext()).build();
        GenericJson myInput = new GenericJson();
        myInput.put("Customer", "Admin");
        myInput.put("message", message);
        myInput.put("Driver", sharedPreferences.getString("full_Name", ""));

        mKinveyClient.customEndpoints(mKinveyClient.getUserClass()).callEndpoint("Driver", myInput, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson genericJson) {
                Toast.makeText(Locations.this, "Please, Slow down your speed.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(final Location location) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Client mKinveyClient = new Client.Builder(getApplicationContext()).build();

                    GenericJson appdata = new GenericJson();
                    appdata.put("_id", mKinveyClient.user().getId());

                    appdata.put("speed", String.format(Locale.US, "%.2f", location.getSpeed() * 3.6));
                    appdata.put("lat", location.getLatitude());
                    appdata.put("long", location.getLongitude());
                    appdata.put("driver", sharedPreferences.getString("full_Name", ""));
                    appdata.put("car", sharedPreferences.getString("CarNo", ""));
                    appdata.put("phone", sharedPreferences.getString("PhoneNumber", ""));
                    AsyncAppData<GenericJson> mylocation = mKinveyClient.appData("Tracking", GenericJson.class);
                    mylocation.save(appdata, new KinveyClientCallback<GenericJson>() {


                        @Override
                        public void onSuccess(GenericJson genericJson) {

                        }

                        @Override
                        public void onFailure(Throwable throwable) {


                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();


                }
                if (location.getSpeed() * 3.6 > 110) {
                    sendMessage("Over speed");
                }
            }
        };
        thread.start();

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        sendMessage(s + " is Enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        sendMessage(s + " is disabled");
    }
}
