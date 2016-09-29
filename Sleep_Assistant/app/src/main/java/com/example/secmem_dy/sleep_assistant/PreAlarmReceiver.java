package com.example.secmem_dy.sleep_assistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by SECMEM-DY on 2016-08-16.
 */
public class PreAlarmReceiver extends BroadcastReceiver {
    private static final String	TAG= "PreAlarmReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        SoundPlay.isPreWakeUpTime=true;
        SoundPlay.startAlarmSound(context,R.raw.alarm);
    }
}