package com.example.opengles;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.MessageFormat;


public class MainActivity extends AppCompatActivity {

    private TextView textHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        checkOpenGL();
    }

    private void initView() {
        this.textHello = findViewById(R.id.tv_hello);
    }

    // 检查OpenGLES版本
    private void checkOpenGL() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = activityManager.getDeviceConfigurationInfo();
        int glEsVersion = deviceConfigurationInfo.reqGlEsVersion;

        String hexVersion = Integer.toHexString(glEsVersion);
        this.textHello.setText(MessageFormat.format("0x{0}", hexVersion));

        if (glEsVersion < 0x20000) {
            Toast.makeText(this, "该设备不支持OpenGL.ES 2.0", Toast.LENGTH_LONG).show();
        }
    }

    // 点击进入OpenGLES学习
    public void onClickOpenGLES(View view) {
        /*
         OpenGL   -> Open Graphics Library
         OpenGLES -> OpenGL for Embedded Systems

         Android 1.0 开始支持 OpenGL ES 1.0 及 1.1
         Android 2.2 开始支持 OpenGL ES 2.0
         Android 4.3 开始支持 OpenGL ES 3.0
         Android 5.0 开始支持 OpenGL ES 3.1
         */

        startActivity(new Intent(this, RendererActivity.class));
    }
}
