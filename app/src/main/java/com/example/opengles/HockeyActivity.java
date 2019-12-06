package com.example.opengles;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.opengles.custom.HockeyRenderer;


public class HockeyActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        // 初始化
        glSurfaceView.setEGLContextClientVersion(2);    // 指定版本
        glSurfaceView.setRenderer(new HockeyRenderer(this));        // 指定渲染器
//        glSurfaceView.setRenderer(new HockeyRenderer2(this));        // 指定渲染器
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
