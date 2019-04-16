package com.example.opengles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.opengles.custom.HockeyRenderer2;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        glSurfaceView = new GLSurfaceView(this);

        // 检查OpenGLES版本
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = activityManager.getDeviceConfigurationInfo();
        int glEsVersion = deviceConfigurationInfo.reqGlEsVersion;
        Log.d(TAG, "glEsVersion --> " + glEsVersion);  // 196610 --> 0x30002
        boolean supportEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;

        if (!supportEs2) {
            Toast.makeText(this, "该设备不支持OpenGL.ES 2.0", Toast.LENGTH_LONG).show();
            return;
        }

        // 初始化
        glSurfaceView.setEGLContextClientVersion(2);    // 指定版本
//        glSurfaceView.setRenderer(new HockeyRenderer(this));        // 指定渲染器
        glSurfaceView.setRenderer(new HockeyRenderer2(this));        // 指定渲染器
        rendererSet = true;

        // 设置页面View
        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

}
