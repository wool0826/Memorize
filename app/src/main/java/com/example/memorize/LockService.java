package com.example.memorize;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class LockService extends Service{

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyBroadCastReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent != null){
            if(intent.getAction() == null) {
                if (mReceiver == null) {
                    mReceiver = new MyBroadCastReceiver();
                    IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                    registerReceiver(mReceiver, filter);
                }
            }
        }

        NotificationCompat.Builder builder;
        String channelId = "Channel";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationManager notificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance
            );

            notificationManager.createNotificationChannel(mChannel);
            builder = new NotificationCompat.Builder(this, channelId);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("Service Running..");

        startForeground(1, builder.build());

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        if(mReceiver != null) unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
