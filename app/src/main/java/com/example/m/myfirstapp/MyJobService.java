package com.example.m.myfirstapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyJobService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.m.myfirstapp.action.FOO";
    private static final String ACTION_BAZ = "com.example.m.myfirstapp.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.m.myfirstapp.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.m.myfirstapp.extra.PARAM2";

    public MyJobService() {
        super("MyJobService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyJobService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyJobService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public void sendMessage2() {
        String s = "";
        Activity.g
        TextView viewText = (TextView) findViewById(R.id.textView);
        OkHttpClient client = new OkHttpClient();

        /*
         * compile the regular expression to be ready to run
         * */
        String patternStr = "Windows\\s([0-9]+\\.[0-9]+\\.[0-9]+)";
        Pattern p = Pattern.compile(patternStr);

        /*
         * do the HTTP request and download the
         */
        Request request = new Request.Builder()
                .url("http://192.168.43.143/dashboard/index.html")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                response.close();
                s = response.body().string();
            }

        } catch (IOException ioex) {
            s = ioex.getMessage();
            s = "bar";
        }

        /*
         * do the regex matching an extract the caputre group
         * */
        //Matcher m = p.matcher(s);

        /*
         * if we found something, display it.
         * else, display the "error message"
         *  */
        viewText.setText(s);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
            sendMessage2();
//        if (intent != null) {
//            final String action = intent.getAction();
//            if (ACTION_FOO.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionFoo(param1, param2);
//            } else if (ACTION_BAZ.equals(action)) {
//                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
//                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
//                handleActionBaz(param1, param2);
//            }
//        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
