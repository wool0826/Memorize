package com.example.memorize;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartService extends BroadcastReceiver {
    public static String ACTION_RESTART_PERSISTENTSERVICE = "restart";
    @Override
    public void onReceive(Context context, Intent intent){
        Log.d("TAG","restart Service!!");
        if(intent.getAction().equals(ACTION_RESTART_PERSISTENTSERVICE)){
            Intent i = new Intent(context, LockService.class);
            context.startService(i);
        }
    }
}
