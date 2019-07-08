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
        unregisterRestartAlarm();
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyBroadCastReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent == null)
        {
            startForeground(1, new Notification());
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            mReceiver = new MyBroadCastReceiver();
            registerReceiver(mReceiver, filter);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        registerRestartAlarm();
        unregisterReceiver(mReceiver);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void registerRestartAlarm(){
        Log.d("TAG","register Alarm");
        Intent intent = new Intent(LockService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(LockService.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 1 * 1000;

        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, 10*1000, sender);
    }

    void unregisterRestartAlarm(){
        Log.d("TAG","unregister Alarm");
        Intent intent = new Intent(LockService.this, RestartService.class);
        intent.setAction(RestartService.ACTION_RESTART_PERSISTENTSERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(LockService.this, 0, intent, 0);
        AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        am.cancel(sender);
    }


}
