package com.example.m.myfirstapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(message);

        sendMessage2();
    }

    public void sendMessage2() {
        String s = "";
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
}
