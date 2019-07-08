package com.example.memorize;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

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
