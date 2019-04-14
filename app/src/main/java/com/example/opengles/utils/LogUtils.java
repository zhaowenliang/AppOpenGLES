package com.example.opengles.utils;

import android.util.Log;

public class LogUtils {

    public static boolean ON = true;

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
