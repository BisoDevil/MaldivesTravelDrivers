package com.globalapp.maldivestravel;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.client.json.GenericJson;
import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.offline.SqlLiteOfflineStore;
import com.kinvey.java.Query;
import com.kinvey.java.cache.CachePolicy;
import com.kinvey.java.cache.InMemoryLRUCache;
import com.kinvey.java.offline.OfflinePolicy;
import com.kinvey.java.offline.OfflineStore;
import com.kinvey.java.query.AbstractQuery;

public class MyTravelsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_travels);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences=getSharedPreferences("MaldivesDriver", Context.MODE_PRIVATE);
        final ProgressBar PBarTravels= (ProgressBar)findViewById(R.id.PBarTravels);

        Client mKinveyClient = new Client.Builder(this.getApplicationContext()).build();
        Query query = mKinveyClient.query();

        query.equals("Driver_Phone_No", sharedPreferences.getString("PhoneNumber", ""));
        query.addSort("Date", AbstractQuery.SortOrder.DESC);
        AsyncAppData<GenericJson> myData = mKinveyClient.appData("Travels", GenericJson.class);
        myData.setCache(new InMemoryLRUCache(), CachePolicy.NETWORKFIRST);
        myData.setOffline(OfflinePolicy.ONLINE_FIRST, new SqlLiteOfflineStore<GenericJson>(getApplicationContext()));
        myData.get(query, new KinveyListCallback<GenericJson>() {
            @Override
            public void onSuccess(GenericJson[] genericJsons) {
                try {
                    PBarTravels.setVisibility(View.INVISIBLE);
                    SetupListView(genericJsons);
                } catch (Exception ex) {
                    ex.printStackTrace();

                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                PBarTravels.setVisibility(View.INVISIBLE);
                Toast.makeText(MyTravelsActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
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
    private void SetupListView(final GenericJson[] genericJsons) {

        ListView list = (ListView) findViewById(R.id.listTravel);
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return genericJsons.length;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater linflater = getLayoutInflater();
                View view1 = linflater.inflate(R.layout.list_tecket_mytravel, null);
                TextView txtTicketCustomerName = (TextView) view1.findViewById(R.id.txtTicketCustomerName);
                TextView txtTicketCustomerLocation = (TextView) view1.findViewById(R.id.txtTicketCustomerLocation);
                TextView txtTicketCustomerDestination = (TextView) view1.findViewById(R.id.txtTicketCustomerDestination);
                TextView txtTicketDate = (TextView) view1.findViewById(R.id.txtTicketDate);
                TextView txtTicketTime = (TextView) view1.findViewById(R.id.txtTicketTime);

                txtTicketCustomerName.setText(genericJsons[position].get("Customer_Name").toString());
                txtTicketCustomerLocation.setText(genericJsons[position].get("Customer_Location").toString());
                txtTicketCustomerDestination.setText(genericJsons[position].get("Customer_Destination").toString());
                txtTicketDate.setText(genericJsons[position].get("Date").toString());
                txtTicketTime.setText(genericJsons[position].get("Time").toString());

                return view1;
            }
        });


    }
}
