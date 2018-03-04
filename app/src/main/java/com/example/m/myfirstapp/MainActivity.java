package com.example.m.myfirstapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.m.myfirstapp.R.id;
import com.example.m.myfirstapp.R.layout;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_LUFTFEUCHTE;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM10;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM25;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_TEMPERATUR;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIDB;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIPERCENT;

public class MainActivity extends Activity {


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
                String param_pm25 = intent.getStringExtra(EXTRA_PARAM_PM25);
                String param_pm10 = intent.getStringExtra(EXTRA_PARAM_PM10);
                String param_wifidb = intent.getStringExtra(EXTRA_PARAM_WIFIDB);
                String param_wifipercent = intent.getStringExtra(EXTRA_PARAM_WIFIPERCENT);
                String param_temperatur = intent.getStringExtra(EXTRA_PARAM_TEMPERATUR);
                String param_luftfeuchte = intent.getStringExtra(EXTRA_PARAM_LUFTFEUCHTE);

                /* TODO do not display "0" values. */
                TextView textview = (TextView) MainActivity.this.findViewById(id.pm25_textview);
                textview.setText(String.format("PM2.5: %s µg/m3",param_pm25));

                textview = (TextView) MainActivity.this.findViewById(id.pm10_textview);
                textview.setText(String.format("PM10: %s µg/m3",param_pm10));

                textview = (TextView) MainActivity.this.findViewById(id.wifidb_textview);
                textview.setText(String.format("Wifi Signal: %s dBm",param_wifidb));

                textview = (TextView) MainActivity.this.findViewById(id.wifipercent_textview);
                textview.setText(String.format("Wifi Qualitaet: %s%%",param_wifipercent));

                textview = (TextView) MainActivity.this.findViewById(id.temperatur_textview);
                textview.setText(String.format("Temperatur: %s°C",param_temperatur));

                textview = (TextView) MainActivity.this.findViewById(id.luftfeuchte_textview);
                textview.setText(String.format("Luftfeuchte: %s%%",param_luftfeuchte));

                textview = (TextView) MainActivity.this.findViewById(id.datetime_textview);
                textview.setText(String.format("%02d:%02d:%02d",Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),Calendar.getInstance().get(Calendar.SECOND)));
            }
        }
    };

    public static String[] querySensorValues() {
        String s[] = {"0","0","0","0","0","0"};
        String tmp = null;

        /*
         * compile the regular expression to be ready to run
         * */
        final String pm25Regex = "(PM2.5)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        final String pm10Regex = "(PM10)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        final String wifidbRegex = "(Signal)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        final String wifipercentRegex = "(Qualität)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        final String temperaturRegex = "(Temperatur)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        final String luftfeuchteRegex = "(Luftfeuchte)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        /* TODO add Luftfeuchtigkeit */
        /* TODO add Temperatur */
        Pattern pm25Pattern = Pattern.compile(pm25Regex);
        Pattern pm10Pattern = Pattern.compile(pm10Regex);
        Pattern wifidbPattern = Pattern.compile(wifidbRegex);
        Pattern wifipercentPattern = Pattern.compile(wifipercentRegex);
        Pattern temperaturPattern = Pattern.compile(temperaturRegex);
        Pattern luftfeuchtePattern = Pattern.compile(luftfeuchteRegex);
        /*
         * do the HTTP request and download the
         */
        Request request = new Builder()
                /* TODO search for device within network?! */
                .url("http://192.168.10.1/values") /* 192.168.43.234 */
                .build();
        try {
            Response response = MainActivity.client.newCall(request).execute();
            if (response != null) {
                Log.d("querySensorValues","query finished!");
                Log.d("querySensorValues", String.format("HTTP Status code %d", response.code()));
                Log.d("querySensorValues", String.format("HTTP Status message %s", response.message()));
                if (response.isSuccessful()) {
                    tmp = response.body().string();
                    Log.v("querySensorValues", tmp);
                    response.close();
                }
            }
        } catch (IOException ioex) {
            Log.wtf("querySensorValues",ioex);
            Log.getStackTraceString(ioex);
            tmp = "";
        }finally {
            if(tmp == null)
            {
                tmp = "";
            }
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
            s[3] = m.group(2);
        }

        m = temperaturPattern.matcher(tmp);
        if(m.find()){
            s[4] = m.group(2);
        }

        m = luftfeuchtePattern.matcher(tmp);
        if(m.find()){
            s[5] = m.group(2);
        }

        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_main);

        MainActivity.client = new OkHttpClient();

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyIntentService.BROADCAST_ACTION_BAZ);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(this.mBroadcastReceiver, filter);

        this.downcounter = new CountDownTimer(Long.MAX_VALUE, 70000) {
            @Override
            public void onTick(long l) {
                Log.d("DownCounter",String.valueOf(l));
                MyIntentService.startActionFoo(MainActivity.this, "test2");
            }

            @Override
            public void onFinish() {
                Log.d("DownCounter","finished");
                this.start();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(this.mBroadcastReceiver);
        MainActivity.client = null;
        super.onDestroy();
    }

    public void sendMessage(View view) {

    }

    // send data to MyService
    protected void communicateToService(String parameter) {
        MyIntentService.startActionFoo(this, parameter);
    }
}
