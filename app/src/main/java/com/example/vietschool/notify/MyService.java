package com.example.vietschool.notify;



import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.example.vietschool.R;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.vietschool.taikhoan.LoginActivity;

import static com.example.vietschool.notify.MyApplication.CHANNEL_ID;

public class MyService extends Service {

    private static final int ACTION_OK=1;



    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int actionNotify = -1;

        try {
            actionNotify = intent.getIntExtra("action_notify_service",0);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        if(actionNotify!=0 && actionNotify!=-1){
            handleActionNotify(actionNotify);
        }else{
            sendNotification();
        }



        return START_NOT_STICKY;
    }

    private void handleActionNotify(int action){
        switch (action){
            case ACTION_OK:
                okFunction();
                break;
        }
    }

    private void okFunction(){
        // getting the static instance of activity
        LoginActivity activity = LoginActivity.instance;
        if (activity != null) {
            // we are calling here activity's method
            activity.clickStopService();
        }

    }


    private void sendNotification() {
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notification_login);

        remoteViews.setOnClickPendingIntent(R.id.btn_ok,getPendingIntent(this,ACTION_OK));

        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setCustomContentView(remoteViews)
                    .build();

            startForeground(1,notification);

    }

    private PendingIntent getPendingIntent(Context context,int action){

        Intent intent = new Intent(this,MyReceiver.class);
        intent.putExtra("action_notify",action);

        return PendingIntent.getBroadcast(context.getApplicationContext(),action,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
