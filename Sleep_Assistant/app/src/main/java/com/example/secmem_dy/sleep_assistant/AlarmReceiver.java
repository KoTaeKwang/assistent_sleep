package com.example.secmem_dy.sleep_assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by SECMEM-DY on 2016-07-04.
 */

public class AlarmReceiver extends BroadcastReceiver {
    private static final String	TAG= "AlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive AlarmReceiver");
        SoundPlay.isWakeUpTime=true;
        SoundPlay.startAlarmSound(context,R.raw.alarm);
    }
}
