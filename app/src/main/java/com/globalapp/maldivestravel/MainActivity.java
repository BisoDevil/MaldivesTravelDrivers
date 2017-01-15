package com.globalapp.maldivestravel;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.java.Query;
import com.kinvey.java.core.KinveyClientCallback;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private Location GPS;
    Marker mMarker;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Enabling Broadcast
        try {
            final Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
            mKinveyClient.push().initialize(getApplication());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // Location sensor

        try {
            startService(new Intent(getApplicationContext(), Locations.class));
            Toast.makeText(MainActivity.this, "Service is started", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if ((int) Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {


                } else {


                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_Location);


                }
            }
            return;
        }


        locationManager.requestLocationUpdates(getProviderName(), 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {

                    Client mKinveyClient = new Client.Builder(getApplicationContext()).build();
                    Query query = mKinveyClient.query();
                    query.equals("user", sharedPreferences.getString("Customer_Phone_No", ""));
                    AsyncAppData<GenericJson> myData = mKinveyClient.appData("UserTracking", GenericJson.class);
                    myData.get(query, new KinveyListCallback<GenericJson>() {
                        @Override
                        public void onSuccess(GenericJson[] genericJsons) {
                            try {
                                LatLng latlong = new LatLng(Double.valueOf(genericJsons[0].get("lat").toString()),
                                        Double.valueOf(genericJsons[0].get("long").toString()));
                                if (mMarker == null) {
                                    mMarker = mMap.addMarker(new MarkerOptions().position(latlong)

                                            .flat(true)

                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user))
                                    );
                                } else {
                                    animateMarker(mMarker, latlong, false);
                                }
//                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 16));

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                        }
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();

                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                // TODO: 10/1/2016 Send mail to open location
//                Toast.makeText(MainActivity.this, "Disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    final int MY_PERMISSIONS_REQUEST_Location = 123;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case (MY_PERMISSIONS_REQUEST_Location):
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Sorry, we need it", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case (R.id.nav_my_Travel):
                Intent starter = new Intent(getApplicationContext(), MyTravelsActivity.class);
                startActivity(starter);
                break;
            case (R.id.nav_MyCurrentTravel):
                Intent current = new Intent(getApplicationContext(), CurrentActivity.class);
                startActivity(current);


                break;
            case (R.id.nav_myNotificatios):
                Intent Notify = new Intent(getApplicationContext(), NotificationActivity.class);
                startActivity(Notify);


                break;


            case (R.id.nav_logout):

                Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
                mKinveyClient.user().logout().execute();
                Intent Main = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(Main);

                break;
            case (R.id.nav_about):

                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage(getString(R.string.about_message))
                        .setTitle(getString(R.string.nav_about))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

                break;
            case (R.id.nav_help):
                Intent help = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(help);

                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(true);


        LocationManager Locationmanager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        GPS = Locationmanager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (GPS == null) {
            GPS = Locationmanager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        try {
            LatLng Center = new LatLng(GPS.getLatitude(), GPS.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Center, 12));
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String location = marker.getPosition().latitude + "," + marker.getPosition().longitude;
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location);
//
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return false;
            }
        });

    }

    public void btnArrived(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Did you really arrive?")
                .setTitle(android.R.string.dialog_alert_title)
                .setIcon(android.R.drawable.stat_sys_warning)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Arrived();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


    }

    public void arrievedToClient(View view) {
        sendMessage("I arrived to Client Now...!");

    }

    public void Arrived() {
        Client mKinveyClient = new Client.Builder(getApplicationContext()).build();
        GenericJson myInput = new GenericJson();
        myInput.put("Customer", sharedPreferences.getString("Customer_Phone_No", ""));
        myInput.put("message", "Have a nice day");

        mKinveyClient.customEndpoints(mKinveyClient.getUserClass()).callEndpoint("Driver", myInput, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson genericJson) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Customer_Name", "");
                editor.putString("Customer_Phone_No", "");
                editor.putString("Customer_Location", "");
                editor.putString("Customer_Destination", "");
                editor.putString("Note", "");
                editor.apply();
                try {
                    mMap.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
        sendMessage("I finished my trip now!");
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
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
        final ProgressDialog dialog = new ProgressDialog(this);

        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();
        Client mKinveyClient = new Client.Builder(getApplicationContext()).build();
        GenericJson myInput = new GenericJson();
        myInput.put("Customer", "Admin");
        myInput.put("message", message);
        myInput.put("Driver", sharedPreferences.getString("full_Name", ""));

        mKinveyClient.customEndpoints(mKinveyClient.getUserClass()).callEndpoint("Driver", myInput, new KinveyClientCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson genericJson) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onFailure(Throwable throwable) {
                dialog.dismiss();

            }
        });
    }

    public void btnLocateUser(View view) {
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 16));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
