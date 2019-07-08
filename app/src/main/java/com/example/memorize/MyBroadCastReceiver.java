package com.example.memorize;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_SCREEN_ON))
        {
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            Intent in = new Intent(context, LockScreen.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
        else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
        }
    }
}
