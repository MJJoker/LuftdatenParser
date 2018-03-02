package com.example.m.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    int counter = 0;
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

        Intent startServiceIntent = new Intent(this, MyJobService.class);
        startService(startServiceIntent);
    }
    /**
     * Called when the user taps the Send button
     */
    public void sendMessage2() {
        String s = "";
        EditText editText = (EditText) findViewById(R.id.editText);

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
            if(response.isSuccessful())
            {
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
        editText.setText(s);
//        if (m.find()) {
//            editText.setText(m.group(1));
//        } else {
//            editText.setText(String.format("Nothing found %d", ++counter));
//    }
        }
    }
