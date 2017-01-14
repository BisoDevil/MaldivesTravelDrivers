package com.globalapp.maldivestravel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView list = (ListView)findViewById(R.id.listNotify);
        final NotifyDBConnection db = new NotifyDBConnection(this);
        list.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return db.getNotification().size();
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
                View view1 = linflater.inflate(R.layout.ticket_notify, null);
                TextView txtTicketNotifyMessage = (TextView) view1.findViewById(R.id.txtTicketNotifyMessage);
                TextView txtTicketNotifyDate = (TextView) view1.findViewById(R.id.txtTicketNotifyDate);
                txtTicketNotifyMessage.setText(db.getNotification().get(position).toString());
                txtTicketNotifyDate.setText(db.getDate().get(position).toString());
                return view1;
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
}
