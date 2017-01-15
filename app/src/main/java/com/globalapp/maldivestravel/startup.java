package com.globalapp.maldivestravel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by babdallah on 1/15/2017.
 */

public class startup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Intent myIntent = new Intent(context, Locations.class);
            context.startService(myIntent);
        }

    }
}
