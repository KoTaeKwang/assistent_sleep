package com.example.secmem_dy.sleep_assistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by SECMEM-DY on 2016-07-07.
 */

public class LoginActivity extends Activity {
    private final String TAG="LoginActivity";
    private EditText idInput, passwordInput;
    private CheckBox autoLogin;
    private Button loginButton;
    private AsyncHttpClient client;
    private String id;
    private String password;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check auto login
        client=HttpClient.getinstance();
        setContentView(R.layout.login);
        idInput=(EditText)findViewById(R.id.idinput);
        passwordInput=(EditText)findViewById(R.id.passwordInput);
        loginButton=(Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check id,pswd
                checkLogin();
            }
        });
    }
    private void checkLogin() {
        StringEntity entity=null;
        id=idInput.getText().toString();
        password=passwordInput.getText().toString();
        //check from server
        JSONObject jsonParams = new JSONObject();
        try {
            id="test";
            jsonParams.put(HttpClient.JSON_ID,id);
            jsonParams.put(HttpClient.JSON_PWD,password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity=HttpClient.makeStringEntity(jsonParams);

        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
        client.post(getApplicationContext(),HttpClient.getAbsoulteUrl(HttpClient.LOGIN_URL),entity,"application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String ackData=null;
                Log.i(TAG,"success");
                try {
                    ackData=new String(responseBody,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG,"akcData"+ackData);

                if(ackData!=null && ackData.equals(HttpClient.ACK_SUCCESS)) {//Login Success
                    Intent intent = new Intent(LoginActivity.this, AlarmSettingActivity.class);
                    intent.putExtra("ID",id);
                    startActivity(intent);
                }else if(ackData!=null && ackData.equals(HttpClient.ACK_FAIL))
                    Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG,"onFailure");
                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
