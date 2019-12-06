package com.example.opengles.utils;

import android.util.Log;

import com.example.opengles.BuildConfig;

public class LogUtils {

    public static boolean ON = BuildConfig.DEBUG;

    public static void i(String tag, String text) {
        if (ON) {
            Log.i(tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (ON) {
            Log.d(tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (ON) {
            Log.w(tag, text);
        }
    }

}
