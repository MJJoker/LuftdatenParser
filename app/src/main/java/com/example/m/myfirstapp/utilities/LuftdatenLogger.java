package com.example.m.myfirstapp.utilities;

import android.util.Log;

import com.example.m.myfirstapp.BuildConfig;


/**
 * AndroidStudio suggests that Log.d or Log.v are alawys guarded with "isLoggable" or "BuildConfig.DEBUG"
 * this is a helper class to do that
 */
public abstract class LuftdatenLogger {

    public static void d(String tag, String msg) {
        if(com.example.m.myfirstapp.BuildConfig.DEBUG) {
            Log.d(tag,msg);
        }
    }

    public static void v(String tag, String msg) {
        if(com.example.m.myfirstapp.BuildConfig.DEBUG ) {
            Log.v(tag,msg);
        }
    }

    public static void wtf(String tag, Throwable exception)
    {
        if(com.example.m.myfirstapp.BuildConfig.DEBUG) {
            Log.wtf(tag,exception);
        }
    }

    public static void getStackTraceString(Throwable exception)
    {
        if(com.example.m.myfirstapp.BuildConfig.DEBUG) {
            Log.getStackTraceString(exception);
        }
    }
}
