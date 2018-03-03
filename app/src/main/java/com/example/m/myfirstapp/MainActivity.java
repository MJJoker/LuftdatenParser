package com.example.m.myfirstapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM10;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM25;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIDB;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIPERCENT;

public class MainActivity extends AppCompatActivity {


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    public static final int MSG_UNCOLOR_START = 0;
    public static final int MSG_UNCOLOR_STOP = 1;
    public static final int MSG_COLOR_START = 2;
    public static final int MSG_COLOR_STOP = 3;
    private static OkHttpClient client;


    private CountDownTimer downcounter;

    // handler for received data from service
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MyIntentService.BROADCAST_ACTION_BAZ)) {
                final String param_pm25 = intent.getStringExtra(EXTRA_PARAM_PM25);
                final String param_pm10 = intent.getStringExtra(EXTRA_PARAM_PM10);
                final String param_wifidb = intent.getStringExtra(EXTRA_PARAM_WIFIDB);
                final String param_wifipercent = intent.getStringExtra(EXTRA_PARAM_WIFIPERCENT);

                TextView textview = (TextView) findViewById(R.id.pm25_textview);
                textview.setText(param_pm25);

                textview = (TextView) findViewById(R.id.pm10_textview);
                textview.setText(param_pm10);

                textview = (TextView) findViewById(R.id.wifidb_textview);
                textview.setText(param_wifidb);

                textview = (TextView) findViewById(R.id.wifipercent_textview);
                textview.setText(param_wifipercent);
            }
        }
    };

    public static String[] querySensorValues() {
        String s[] = {"0","0","0","0"};
        String tmp = "";

        /*
         * compile the regular expression to be ready to run
         * */
        String pm25Regex = "(PM2.5)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        String pm10Regex = "(PM10)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        String wifidbRegex = "(Signal)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        String wifipercentRegex = "(Qualität)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        Pattern pm25Pattern = Pattern.compile(pm25Regex);
        Pattern pm10Pattern = Pattern.compile(pm10Regex);
        Pattern wifidbPattern = Pattern.compile(wifidbRegex);
        Pattern wifipercentPattern = Pattern.compile(wifipercentRegex);
        /*
         * do the HTTP request and download the
         */
        Request request = new Request.Builder()
                .url("http://192.168.10.1/values")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                tmp = response.body().string();
                Log.d("MainActivity",tmp);
                response.close();
            }
        } catch (IOException ioex) {
            Log.wtf("MainActivity",ioex);
            Log.getStackTraceString(ioex);
            tmp = "";
        }

        Matcher m = pm25Pattern.matcher(tmp);
        if(m.find()){
            s[0] = m.group(2);
        }

        m = pm10Pattern.matcher(tmp);
        if(m.find()){
            s[1] = m.group(2);
        }

        m = wifidbPattern.matcher(tmp);
        if(m.find()){
            s[2] = m.group(2);
        }

        m = wifipercentPattern.matcher(tmp);
        if(m.find()){
            s[2] = m.group(2);
        }

        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyIntentService.BROADCAST_ACTION_BAZ);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(mBroadcastReceiver, filter);

        downcounter = new CountDownTimer(Long.MAX_VALUE, 70000) {
            @Override
            public void onTick(long l) {
                Log.d("DownCounter",String.valueOf(l));
                MyIntentService.startActionFoo(MainActivity.this, "test2");
            }

            @Override
            public void onFinish() {
                Log.d("DownCounter","finished");
                start();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(mBroadcastReceiver);
        client = null;
        super.onDestroy();
    }

    public void sendMessage(View view) {

    }

    // send data to MyService
    protected void communicateToService(String parameter) {
        MyIntentService.startActionFoo(this, parameter);
    }
}
