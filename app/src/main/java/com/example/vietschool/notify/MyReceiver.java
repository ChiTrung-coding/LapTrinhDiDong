package com.example.vietschool.notify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionNotify = intent.getIntExtra("action_notify",0);

        Intent intentService = new Intent(context,MyService.class);
        intentService.putExtra("action_notify_service",actionNotify);

        context.startService(intentService);
    }
}
