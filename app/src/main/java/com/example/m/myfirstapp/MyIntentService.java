package com.example.m.myfirstapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.example.m.myfirstapp.Data.AppDatabase;
import com.example.m.myfirstapp.Data.SensorSample;
import com.example.m.myfirstapp.utilities.LuftdatenLogger;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyIntentService extends IntentService {
    public static final String BROADCAST_ACTION_BAZ = "com.myapp.broadcast_action.FOO";
    public static final String EXTRA_PARAM_PM25 = "com.myapp.extra.PARAM_PM25";
    public static final String EXTRA_PARAM_PM10 = "com.myapp.extra.PARAM_PM10";
    public static final String EXTRA_PARAM_WIFIDB = "com.myapp.extra.PARAM_WIFIDB";
    public static final String EXTRA_PARAM_WIFIPERCENT = "com.myapp.extra.PARAM_WIFIPERCENT";
    public static final String EXTRA_PARAM_LUFTFEUCHTE = "com.myapp.extra.PARAM_LUFTFEUCHTE";
    public static final String EXTRA_PARAM_TEMPERATUR = "com.myapp.extra.PARAM_TEMPERATUR";
    public static final String EXTRA_PARAM_SENSORID = "com.myapp.extra.PARAM_SENSORID";
    private static final String ACTION_FINDSENSOR = "com.myapp.action.FINDSENSOR";
    private static final String ACTION_TAKESAMPLE = "com.myapp.action.TAKESAMPLE";
    private static final String ACTION_DUMPDB = "com.myapp.action.DUMPDB";
    private static AppDatabase db = null;


    public MyIntentService() {
        super("MyIntentService");
    }

    // called by activity to communicate to service
    public static void startActionFindSensor(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FINDSENSOR);
        context.startService(intent);
    }

    // called by activity to communicate to service
    public static void startActionTakeSample(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_TAKESAMPLE);
        context.startService(intent);
    }

    // called by activity to communicate to service
    public static void startActionDumpDB(Context context) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_DUMPDB);
        context.startService(intent);
    }

    private static void DumpSensorSampleDB() {
        if (db != null) {
            List<SensorSample> list = db.SensorSampleDao().getAll();
            LuftdatenLogger.d("AppDatabase", String.format("Dumping Database: %d entries", list.size()));
            LuftdatenLogger.d("AppDatabase", "SensorID;PM25;PM10;Temperatur;Humidity;WifidB;WifiPercent");
            for (SensorSample sample : list) {
                LuftdatenLogger.d("AppDatabase", String.format("%d;%d;%f;%f;%f;%f;%f;%f",
                        sample.getTimestamp(),
                        sample.getSensorid(),
                        sample.getPm2_5(),
                        sample.getPm10(),
                        sample.getTemperatur(),
                        sample.getHumidity(),
                        sample.getWifidb(),
                        sample.getWifipercent()));
            }
        }
    }

    private static void FindSensor() {
        LuftdatenLogger.d("FindSensor", "scanning the area");
        try {
            NetworkInterface intf = NetworkInterface.getByName("wlan0");

            if (null != intf) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    String sAddr = addr.getHostAddress();
                    LuftdatenLogger.d("FindSensor", "own IP is: " + sAddr);
                    boolean isIPv4 = sAddr.indexOf(':') < 0;

                    if (isIPv4) {
                        OkHttpClient okclient = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).connectTimeout(10, TimeUnit.SECONDS).build();


                        for (int lastbyte = 1; lastbyte <= 254; ++lastbyte) {
                            byte ip[] = addr.getAddress();
                            ip[3] = (byte)lastbyte;

                            String newaddress = "http://" + InetAddress.getByAddress(ip).getHostAddress() + "/values";
                            LuftdatenLogger.d("Fingsensor",newaddress);

                            Request request = new Request.Builder().url(newaddress).build();
                            try {
                                Response res = okclient.newCall(request).execute();
                                if (null != res) {
                                    if (res.isSuccessful()) {
                                        LuftdatenLogger.d("FindSensor", String.format("Found the damn sensor: %d.%d.%d.%d- %s", (ip[1] & 0xFF), (ip[2]&0xFF), (ip[3]&0xFF),newaddress));
                                        return;
                                    } else {
                                        LuftdatenLogger.d("FindSensor", String.format("not sensor there: %d.%d.%d.%d - %s", (ip[0] & 0xFF), (ip[1] & 0xFF), (ip[2]&0xFF), (ip[3]&0xFF),newaddress));
                                    }
                                    res.close();
                                } else {
                                    LuftdatenLogger.d("FindSensor", String.format("not sensor there: %d.%d.%d.%d - %s", (ip[0] & 0xFF), (ip[1] & 0xFF), (ip[2]&0xFF), (ip[3]&0xFF),newaddress));
                                }
                            } catch(Exception ex)
                            {
                                //LuftdatenLogger.wtf("FindSensor",ex);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LuftdatenLogger.wtf("FindSensor", ex);
            LuftdatenLogger.getStackTraceString(ex);
        } // for now eat exceptions
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (db == null) {
            db = AppDatabase.getAppDatabase(getApplicationContext());
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String queryresult[];
        if (intent != null) {

            if (db == null) {
                db = AppDatabase.getAppDatabase(getApplicationContext());
            }

            String action = intent.getAction();

            switch (action) {
                case ACTION_TAKESAMPLE:
                    queryresult = MainActivity.querySensorValues();
                    broadcastActionBaz(queryresult);
                    break;
                case ACTION_DUMPDB:
                    DumpSensorSampleDB();
                    break;
                case ACTION_FINDSENSOR:
                    FindSensor();
                    break;
            }
        }
    }

    // called to send data to Activity
    public void broadcastActionBaz(String params[]) {
        /* check if array is big enough*/
        Intent intent = new Intent(BROADCAST_ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM_PM25, params[0]);
        intent.putExtra(EXTRA_PARAM_PM10, params[1]);
        intent.putExtra(EXTRA_PARAM_WIFIDB, params[2]);
        intent.putExtra(EXTRA_PARAM_WIFIPERCENT, params[3]);
        intent.putExtra(EXTRA_PARAM_TEMPERATUR, params[4]);
        intent.putExtra(EXTRA_PARAM_LUFTFEUCHTE, params[5]);
        intent.putExtra(EXTRA_PARAM_SENSORID, params[6]);

        try {
            SensorSample sample = new SensorSample();
            sample.setPm2_5_raw(params[0]);
            sample.setPm2_5(Double.valueOf(params[0]));

            sample.setPm10_raw(params[1]);
            sample.setPm10(Double.valueOf(params[1]));

            sample.setHumidity_raw(params[2]);
            sample.setHumidity(Double.valueOf(params[2]));

            sample.setTemperatur_raw(params[3]);
            sample.setTemperatur(Double.valueOf(params[3]));

            sample.setWifidb_raw(params[4]);
            sample.setWifidb(Double.valueOf(params[4]));

            sample.setWifipercent_raw(params[5]);
            sample.setWifipercent(Double.valueOf(params[5]));

            sample.setTimestamp(System.currentTimeMillis() / 1000);
            sample.setSensorid(Integer.valueOf(params[6]));

            db.SensorSampleDao().insertAll(sample);
        } catch (NumberFormatException nuforex) {
            LuftdatenLogger.wtf("MyIntentService", nuforex);
            LuftdatenLogger.getStackTraceString(nuforex);
        }

        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(intent);
    }
}
