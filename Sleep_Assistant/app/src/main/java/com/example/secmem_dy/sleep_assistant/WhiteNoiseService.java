package com.example.secmem_dy.sleep_assistant;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Created by SECMEM-DY on 2016-07-22.
 */
public class WhiteNoiseService extends Service {

    private static SoundPlay mplay ;

    public void onCreate(){
        super.onCreate();
    }
    public void onDestroy(){
        super.onDestroy();
        mplay.stop();

        Log.e("WhiteNoiseService","stop");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        mplay = new SoundPlay(getApplicationContext(), R.raw.whitenoise);
        mplay.play();
        Log.e("WhiteNoiseService","play");
        return START_STICKY;
    }
}

