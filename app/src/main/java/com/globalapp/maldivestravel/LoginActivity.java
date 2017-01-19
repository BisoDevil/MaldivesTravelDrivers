package com.globalapp.maldivestravel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences=getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);

        TelephonyManager mymanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        try {
            final EditText txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
            txtPhoneNumber.setText(mymanager.getLine1Number());
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void LoginButton(View view) {

        final ProgressDialog dialog = new ProgressDialog(this);

        dialog.setMessage(getString(R.string.please_wait));
        dialog.show();

        final EditText txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        final EditText txtPassword = (EditText) findViewById(R.id.txtPassword);
        final EditText txtCarNo = (EditText) findViewById(R.id.txtCarNo);
        final Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
        final Intent myintet = new Intent(this, MainActivity.class);
        mKinveyClient.user().logout().execute();

        mKinveyClient.user().login(txtPhoneNumber.getText().toString(), txtPassword.getText().toString(), new KinveyUserCallback() {


            @Override
            public void onSuccess(User user) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.welcome) + " " + user.getUsername(), Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("PhoneNumber",user.getUsername());
                editor.putString("full_Name",user.get("full_Name").toString());
                editor.putString("CarNo",txtCarNo.getText().toString());
                editor.apply();

                startActivity(myintet);
                finish();

            }

            @Override
            public void onFailure(Throwable throwable) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }



}
