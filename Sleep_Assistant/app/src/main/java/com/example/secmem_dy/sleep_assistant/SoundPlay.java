package com.example.secmem_dy.sleep_assistant;

/**
 * Created by SECMEM-DY on 2016-07-04.
 */import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class SoundPlay {
    public static boolean isWakeUpTime=false;
    public static boolean isPreWakeUpTime=false;

    private static MediaPlayer alarmSound=null;
    private static MediaPlayer whiteNoiseSound=null;
    MediaPlayer mp = null;

    public static void startAlarmSound( Context context, int id ){
        if(alarmSound==null){
            alarmSound=MediaPlayer.create(context,id);
        }
        alarmSound.setLooping(true);
        alarmSound.seekTo(0);
        alarmSound.start();
    }
    public static void stopAlarmSound(){
        if(alarmSound!=null) {
            alarmSound.stop();
            alarmSound.release();
        }
        alarmSound=null;
    }
    public static void startWhiteNoiseSound(Context context,int id){
        if(whiteNoiseSound==null){
            whiteNoiseSound=MediaPlayer.create(context,id);
        }
        whiteNoiseSound.seekTo(0);
        whiteNoiseSound.start();
    }
    public static void stopWhiteNoiseSound(){
        if(whiteNoiseSound!=null){
            whiteNoiseSound.stop();
            whiteNoiseSound.release();
        }
        whiteNoiseSound=null;
    }

    public SoundPlay ( Context context, int id ) {
        mp = MediaPlayer.create(context, id);
    }
    public void play() {
        Log.e("SoundPlay","play");
        mp.setLooping(true);
        mp.seekTo(0);
        mp.start();
    }
    public void stop(){
        Log.e("SoundPlay","stop");
        mp.stop();
        mp.release();
    }
}
