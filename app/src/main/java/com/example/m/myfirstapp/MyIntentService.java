package com.example.m.myfirstapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class MyIntentService extends IntentService {
    private static final String ACTION_FOO = "com.myapp.action.FOO";
    private static final String EXTRA_PARAM_A = "com.myapp.extra.PARAM_A";

    public static final String BROADCAST_ACTION_BAZ = "com.myapp.broadcast_action.FOO";
    public static final String EXTRA_PARAM_PM25 = "com.myapp.extra.PARAM_PM25";
    public static final String EXTRA_PARAM_PM10 = "com.myapp.extra.PARAM_PM10";
    public static final String EXTRA_PARAM_WIFIDB = "com.myapp.extra.PARAM_WIFIDB";
    public static final String EXTRA_PARAM_WIFIPERCENT = "com.myapp.extra.PARAM_WIFIPERCENT";
    public static final String EXTRA_PARAM_LUFTFEUCHTE = "com.myapp.extra.PARAM_LUFTFEUCHTE";
    public static final String EXTRA_PARAM_TEMPERATUR = "com.myapp.extra.PARAM_TEMPERATUR";



    // called by activity to communicate to service
    public static void startActionFoo(Context context, String param1) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM_A, param1);
        context.startService(intent);
    }

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String queryresult[];
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM_A);
                Log.d("MyIntentService",param1);
                queryresult = MainActivity.querySensorValues();

                broadcastActionBaz(queryresult);
            }
        }
    }

    // called to send data to Activity
    public void broadcastActionBaz(String params[]) {
        Intent intent = new Intent(BROADCAST_ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM_PM25, params[0]);
        intent.putExtra(EXTRA_PARAM_PM10, params[1]);
        intent.putExtra(EXTRA_PARAM_WIFIDB, params[2]);
        intent.putExtra(EXTRA_PARAM_WIFIPERCENT, params[3]);
        intent.putExtra(EXTRA_PARAM_TEMPERATUR, params[4]);
        intent.putExtra(EXTRA_PARAM_LUFTFEUCHTE, params[5]);

        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(this);
        bm.sendBroadcast(intent);
    }
}
