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
import android.widget.TextView;

import com.example.m.myfirstapp.R.id;
import com.example.m.myfirstapp.R.layout;
import com.example.m.myfirstapp.utilities.LuftdatenLogger;

import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_LUFTFEUCHTE;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM10;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_PM25;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_TEMPERATUR;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIDB;
import static com.example.m.myfirstapp.MyIntentService.EXTRA_PARAM_WIFIPERCENT;

public class MainActivity extends Activity {


    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    private CountDownTimer m_downcounter;

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
                TextView textview = MainActivity.this.findViewById(id.pm25_textview);
                textview.setText(String.format("PM2.5: %s µg/m3",param_pm25));

                textview = findViewById(id.pm10_textview);
                textview.setText(String.format("PM10: %s µg/m3",param_pm10));

                textview = findViewById(id.wifidb_textview);
                textview.setText(String.format("Wifi Signal: %s dBm",param_wifidb));

                textview = findViewById(id.wifipercent_textview);
                textview.setText(String.format("Wifi Qualitaet: %s%%",param_wifipercent));

                textview = findViewById(id.temperatur_textview);
                textview.setText(String.format("Temperatur: %s°C",param_temperatur));

                textview = findViewById(id.luftfeuchte_textview);
                textview.setText(String.format("Luftfeuchte: %s%%",param_luftfeuchte));

                textview = findViewById(id.datetime_textview);
                textview.setText(String.format("%02d:%02d:%02d",Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE),Calendar.getInstance().get(Calendar.SECOND)));
            }
        }
    };

    public static String[] querySensorValues() {
        OkHttpClient client = new OkHttpClient();
        ResponseBody resBody;
        String s[] = {"0","0","0","0","0","0","0"};
        String tmp = null;

        /*
         * compile the regular expression to be ready to run
         * */
        String pm25Regex = "(PM2.5)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        String pm10Regex = "(PM10)[a-z<>\\/ \\=\\']*([0-9\\.]+)+";
        String wifidbRegex = "(Signal)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        String wifipercentRegex = "(Qualität)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        String temperaturRegex = "(Temperatur)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        String luftfeuchteRegex = "(Luftfeuchte)(?:[<>\\/a-z \\=']+)([0-9\\.-]+)";
        String SensorIdRegex = "(ID:)(?:[<>\\/a-z \\=']+)([0-9]{3,})";
        /* TODO add Luftfeuchtigkeit */
        /* TODO add Temperatur */
        Pattern pm25Pattern = Pattern.compile(pm25Regex);
        Pattern pm10Pattern = Pattern.compile(pm10Regex);
        Pattern wifidbPattern = Pattern.compile(wifidbRegex);
        Pattern wifipercentPattern = Pattern.compile(wifipercentRegex);
        Pattern temperaturPattern = Pattern.compile(temperaturRegex);
        Pattern luftfeuchtePattern = Pattern.compile(luftfeuchteRegex);
        Pattern SensorIdPattern = Pattern.compile(SensorIdRegex);
        /*
         * do the HTTP request and download the
         */
        Request request = new Builder()
                /* TODO search for device within network?! */
                /*ESP_403723 -> Sensor Marco */
                /* 192.168.43.21 -> IP Marco */
                /* 192.168.43.234 -> IP Michael */
                .url("http://192.168.43.21/values")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response != null) {
                LuftdatenLogger.d("querySensorValues","query finished!");
                LuftdatenLogger.d("querySensorValues", String.format("HTTP Status code %d", response.code()));
                LuftdatenLogger.d("querySensorValues", String.format("HTTP Status message %s", response.message()));
                if (response.isSuccessful()) {
                    resBody = response.body();

                    // if we have a responseBody and the HTTP StatusCode is 200 (everything OK)
                    if(resBody != null && response.code() == 200) {
                        tmp = response.body().string();
                        LuftdatenLogger.v("querySensorValues", tmp);
                    } else {
                        /* not sure in which situation this can happen  */
                        tmp = "";
                    }
                    response.close();
                }
            }
        } catch (IOException ioex) {
            LuftdatenLogger.wtf("querySensorValues",ioex);
            LuftdatenLogger.getStackTraceString(ioex);
            tmp = "";
        } finally {
            if(null == tmp)
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

        m = SensorIdPattern.matcher(tmp);
        if(m.find()) {
            s[6] = m.group(2);
        }

        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MyIntentService.BROADCAST_ACTION_BAZ);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.registerReceiver(this.mBroadcastReceiver, filter);

        m_downcounter = new CountDownTimer(Long.MAX_VALUE, 70000) {
            @Override
            public void onTick(long l) {
                LuftdatenLogger.d("DownCounter",String.valueOf(l));
                //MyIntentService.startActionTakeSample(MainActivity.this);
            }

            @Override
            public void onFinish() {
                LuftdatenLogger.d("DownCounter","finished");
                this.start();
            }
        }.start();

        MyIntentService.startActionDumpDB(this);

    @Override
    protected void onDestroy() {
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.unregisterReceiver(this.mBroadcastReceiver);
        super.onDestroy();
    }

    public void sendMessage(View view){

        MyIntentService.startActionFindSensor(this);

    }
}
