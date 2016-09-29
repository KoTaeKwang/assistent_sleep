package com.example.secmem_dy.sleep_assistant;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by SECMEM-DY on 2016-07-05.
 */
public class HttpClient {
    public static final String SERVER_URL="http://211.189.20.136:4389/";
    public static final String LOGIN_URL="login";
    public static final String START_SLEEP_URL="startSleep";
    public static final String CANCEL_SLEEP_URL="cancelSleep";
    public static final String PUSH_SLEEP_URL="pushSleep";
    public static final String WAKEUP_SLEEP_URL="wakeupSleep";
    public static final String SHOW_VISUAL_DATA_URL="visualdata";

    public static final String JSON_START_TIME="starttime";
    public static final String JSON_END_TIME="endtime";
    public static final String JSON_CANCEL="cancel";
    public static final String JSON_ID="id";
    public static final String JSON_PWD="pwd";
    public static final String JSON_HEART_RATE="heartRate";
    public static final String JSON_MOVE="move";
    public static final String JSON_CURRNT_TIME="currenttime";

    public static final String ACK_SUCCESS="1";
    public static final String ACK_FAIL="2";

    public static final String ACK_PLAY_WHITE_NOISE="3";
    public static final String ACK_STOP_WHITE_NOISE="4";

    public static final String ACK_KEEP_ALARM="5";
    public static final String ACK_STOP_ALARM="6";

    private static AsyncHttpClient client = new AsyncHttpClient();
    public static AsyncHttpClient getinstance(){
        return HttpClient.client;
    }
    public static String getAbsoulteUrl(String relativeUrl) {
        return SERVER_URL + relativeUrl;
    }
    public static StringEntity makeStringEntity(JSONObject jsonParams){
        try {
            return new StringEntity(jsonParams.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e("Error","StringEntity");
            e.printStackTrace();
            return null;
        }
    }
}
