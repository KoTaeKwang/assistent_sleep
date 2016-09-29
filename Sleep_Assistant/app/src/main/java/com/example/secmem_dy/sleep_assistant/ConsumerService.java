/*
 * Copyright (c) 2015 Samsung Electronics Co., Ltd. All rights reserved. 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that 
 * the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice, 
 *       this list of conditions and the following disclaimer. 
 *     * Redistributions in binary form must reproduce the above copyright notice, 
 *       this list of conditions and the following disclaimer in the documentation and/or 
 *       other materials provided with the distribution. 
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its contributors may be used to endorse or 
 *       promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.example.secmem_dy.sleep_assistant;
import android.app.Notification;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ConsumerService extends SAAgent {
    private static final String TAG = "ConsumerService";
    private static final int HELLOACCESSORY_CHANNEL_ID = 104;
    private static final Class<ServiceConnection> SASOCKET_CLASS = ServiceConnection.class;
    private final IBinder mBinder = new LocalBinder();
    private ServiceConnection mConnectionHandler = null;
    Handler mHandler = new Handler();

    private boolean mQuit;
    private AsyncHttpClient client;
    private static SoundPlay mplay ;
    private String id;

    private int cnt;
    public ConsumerService() {
        super(TAG, SASOCKET_CLASS);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mplay = new SoundPlay(getApplicationContext(), R.raw.whitenoise);
        SA mAccessory = new SA();
        mQuit=false;
        client=HttpClient.getinstance();
        try {
            mAccessory.initialize(this);
        } catch (SsdkUnsupportedException e) {
            // try to handle SsdkUnsupportedException
            if (processUnsupportedException(e) == true) {
                return;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            /*
             * Your application can not use Samsung Accessory SDK. Your application should work smoothly
             * without using this SDK, or you may want to notify user and close your application gracefully
             * (release resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("수면모드")
                .setContentText("수면모드를 실행 중 입니다.")
                .build();

        startForeground(1, notification);
        id=intent.getStringExtra("ID");//get ID;
        Log.i(TAG,"get ID is" + id);
        cnt=0;
        return mBinder;
    }

    @Override
    protected void onFindPeerAgentsResponse(SAPeerAgent[] peerAgents, int result) {
        if ((result == SAAgent.PEER_AGENT_FOUND) && (peerAgents != null)) {
            for(SAPeerAgent peerAgent:peerAgents)
                requestServiceConnection(peerAgent);
        } else if (result == SAAgent.FINDPEER_DEVICE_NOT_CONNECTED) {
            Toast.makeText(getApplicationContext(), "FINDPEER_DEVICE_NOT_CONNECTED", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.FINDPEER_SERVICE_NOT_FOUND) {
            Toast.makeText(getApplicationContext(), "FINDPEER_SERVICE_NOT_FOUND", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.NoPeersFound, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onServiceConnectionRequested(SAPeerAgent peerAgent) {
        if (peerAgent != null) {
            acceptServiceConnectionRequest(peerAgent);
        }
    }

    @Override
    protected void onServiceConnectionResponse(SAPeerAgent peerAgent, SASocket socket, int result) {
        if (result == SAAgent.CONNECTION_SUCCESS) {
            this.mConnectionHandler = (ServiceConnection) socket;
        } else if (result == SAAgent.CONNECTION_ALREADY_EXIST) {
            Toast.makeText(getBaseContext(), "CONNECTION_ALREADY_EXIST", Toast.LENGTH_LONG).show();
        } else if (result == SAAgent.CONNECTION_DUPLICATE_REQUEST) {
            Toast.makeText(getBaseContext(), "CONNECTION_DUPLICATE_REQUEST", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), R.string.ConnectionFailure, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onError(SAPeerAgent peerAgent, String errorMessage, int errorCode) {
        super.onError(peerAgent, errorMessage, errorCode);
    }

    @Override
    protected void onPeerAgentsUpdated(SAPeerAgent[] peerAgents, int result) {
        final SAPeerAgent[] peers = peerAgents;
        final int status = result;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (peers != null) {
                    if (status == SAAgent.PEER_AGENT_AVAILABLE) {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_AVAILABLE", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "PEER_AGENT_UNAVAILABLE", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public class ServiceConnection extends SASocket {//SASocket 을 이용하기 위한 내부 클래스 생성
        public ServiceConnection() {
            super(ServiceConnection.class.getName());
        }
        @Override
        public void onError(int channelId, String errorMessage, int errorCode) {
        }

        @Override
        public void onReceive(int channelId, byte[] data) {//기어로 부터 데이터를 받은 후 액션
            final String message = new String(data);
            String sleepStateURl;
            Toast.makeText(getApplicationContext(), "get "+message, Toast.LENGTH_SHORT).show();
            String currntData=new String(data);
            Log.i(TAG,currntData);

            StringEntity entity=null;
            JSONObject jsonParams = new JSONObject();
            try {
                GregorianCalendar currenttTimecalendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                jsonParams.put(HttpClient.JSON_ID,id);
                jsonParams.put(HttpClient.JSON_HEART_RATE,currntData);
                jsonParams.put(HttpClient.JSON_CURRNT_TIME,sdf.format(currenttTimecalendar.getTime()));
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Error","jsonParams");
            }
            entity=HttpClient.makeStringEntity(jsonParams);

            if(SoundPlay.isWakeUpTime) {
                sleepStateURl = HttpClient.WAKEUP_SLEEP_URL;
                if(cnt==0) {
                    Log.i(TAG,"send wake_up msg to gear");
                    if(sendData("1"))
                        cnt++;
                }
            }
            else
                sleepStateURl=HttpClient.PUSH_SLEEP_URL;//set URL

            client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(sleepStateURl),entity,"application/json",new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String ackData=null;
                    try {
                        ackData=new String(responseBody,"UTF-8");
                        Log.i(TAG,"success_getAck:"+ackData);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(ackData!=null){
                        if(ackData.equals(HttpClient.ACK_SUCCESS)) {// Success isPreWakeUpTime
                            Log.i(TAG,"state_success");
                        }else if( ackData.equals(HttpClient.ACK_FAIL)){
                            Log.i(TAG,"state_fail");
                        }
                        else if(ackData.equals(HttpClient.ACK_PLAY_WHITE_NOISE)){
                            SoundPlay.startWhiteNoiseSound(getApplicationContext(), R.raw.whitenoise);
                        }else if(ackData.equals(HttpClient.ACK_STOP_WHITE_NOISE)){
                            SoundPlay.stopWhiteNoiseSound();
                        }else if(ackData.equals(HttpClient.ACK_KEEP_ALARM)){
                            //keep going alarm;
                        }else if(ackData.equals(HttpClient.ACK_STOP_ALARM)){
                            SoundPlay.stopAlarmSound();
                            Intent receiverIntent=new Intent();
                            receiverIntent.setAction("End_Alarm");//AlarmSettingActivity에 BR을 보냄
                            sendBroadcast(receiverIntent);
                            Log.i(TAG,"close Connection");
                            closeConnection();
                        }
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.i(TAG,"data_send_fail");
                }
            } );
        }
        @Override
        protected void onServiceConnectionLost(int reason) {
            closeConnection();
        }
    }

    public class LocalBinder extends Binder {
        public ConsumerService getService() {
            return ConsumerService.this;
        }
    }

    public void findPeers() {
        findPeerAgents();
    }

    public boolean sendData(final String data) {
        boolean retvalue = false;
        if (mConnectionHandler != null) {
            try {
                mConnectionHandler.send(HELLOACCESSORY_CHANNEL_ID, data.getBytes());
                retvalue = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retvalue;
    }
    public boolean closeConnection() {
        if (mConnectionHandler != null) {
            mConnectionHandler.close();
            mConnectionHandler = null;
            this.stopForeground(true);
            return true;
        } else {
            return false;
        }
    }

    private boolean processUnsupportedException(SsdkUnsupportedException e) {
        e.printStackTrace();
        int errType = e.getType();
        if (errType == SsdkUnsupportedException.VENDOR_NOT_SUPPORTED
                || errType == SsdkUnsupportedException.DEVICE_NOT_SUPPORTED) {
            /*
             * Your application can not use Samsung Accessory SDK. You application should work smoothly
             * without using this SDK, or you may want to notify user and close your app gracefully (release
             * resources, stop Service threads, close UI thread, etc.)
             */
            stopSelf();
        } else if (errType == SsdkUnsupportedException.LIBRARY_NOT_INSTALLED) {
            Log.e(TAG, "You need to install Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_REQUIRED) {
            Log.e(TAG, "You need to update Samsung Accessory SDK to use this application.");
        } else if (errType == SsdkUnsupportedException.LIBRARY_UPDATE_IS_RECOMMENDED) {
            Log.e(TAG, "We recommend that you update your Samsung Accessory SDK before using this application.");
            return false;
        }
        return true;
    }

    private void updateTextView(final String str) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
              //  ConsumerActivity.updateTextView(str);
            }
        });
    }
    private void addMessage(final String prefix, final String data) {
        final String strToUI = prefix.concat(data);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
               // ConsumerActivity.addMessage(strToUI);
            }
        });
    }
}
