package com.globalapp.maldivestravel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.Client;
import com.kinvey.java.core.KinveyClientCallback;

public class CurrentActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtPhone = (TextView) findViewById(R.id.txtCustomerPhone);
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                // TODO: 7/23/2016 changing phone number
                phoneIntent.setData(Uri.parse("tel:" + txtPhone.getText()));
                LoadPermission();
                if (ActivityCompat.checkSelfPermission(CurrentActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                startActivity(phoneIntent);
                Snackbar.make(view, getString(R.string.calling), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        LoadCustomerData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LoadCustomerData() {
        TextView txtCustomerPhone = (TextView) findViewById(R.id.txtCustomerPhone);
        TextView txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        TextView txtCustomerLocation = (TextView) findViewById(R.id.txtCustomerLocation);
        TextView txtCustomerDestination = (TextView) findViewById(R.id.txtCustomerDestination);
        TextView txtDate = (TextView) findViewById(R.id.txtDate);
        TextView txtTime = (TextView) findViewById(R.id.txtTime);
        txtCustomerName.setText(sharedPreferences.getString("Customer_Name", ""));
        txtCustomerPhone.setText(sharedPreferences.getString("Customer_Phone_No", ""));
        txtCustomerLocation.setText(sharedPreferences.getString("Customer_Location", ""));
        txtCustomerDestination.setText(sharedPreferences.getString("Customer_Destination", ""));
        txtDate.setText(sharedPreferences.getString("Date", "Date"));
        txtTime.setText(sharedPreferences.getString("Time", "Time"));

    }

    final int MY_PERMISSIONS_REQUEST_Storage = 200;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case (MY_PERMISSIONS_REQUEST_Storage):
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    Toast.makeText(this, "Sorry, we need it", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void LoadPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            if ((int) Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.CALL_PHONE)) {


                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_Storage);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
            }
            return;
        }

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
                Toast.makeText(CurrentActivity.this, "Message Sent", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void btnAceeptOrder(View view) {
        sendMessage("I Accepted the Order");
    }

    public void fbMap(View view) {
        TextView txtCustomerLocation = (TextView) findViewById(R.id.txtCustomerLocation);
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + txtCustomerLocation.getText().toString());
//        Toast.makeText(MainActivity.this,location, Toast.LENGTH_SHORT).show();
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
