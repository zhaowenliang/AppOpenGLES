package com.example.opengles.custom;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.opengles.utils.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ColorRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "ColorRenderer";

    private int color;

    public ColorRenderer(int color) {
        this.color = color;
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        LogUtils.d(TAG, "onSurfaceCreated()");
        float redF = Color.red(color) / 255F;
        float greenF = Color.green(color) / 255F;
        float blueF = Color.blue(color) / 255F;
        float alphaF = Color.alpha(color) / 255F;

        GLES20.glClearColor(redF, greenF, blueF, alphaF);
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        LogUtils.d(TAG, "onSurfaceChanged()  width: " + width + "  height: " + height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }
}
