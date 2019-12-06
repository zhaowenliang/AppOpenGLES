package com.example.opengles.custom;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import androidx.annotation.NonNull;

import com.example.opengles.R;
import com.example.opengles.utils.ShaderHelper;
import com.example.opengles.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SimpleRenderer implements GLSurfaceView.Renderer {

    private static final float[] tableVerticesWithTriangles = {
            // X, Y, R, G, B
            // 三角扇
            0, 0, 1f, 1f, 1f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            // 中间的分界线
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,
            // 两个木槌的质点位置
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f,
    };

    // 一个顶点有两个分量（x，y），所以首先创建一个常量用来记住这一事实
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private final FloatBuffer vertexData;

    private final String vertexShaderSource;
    private final String fragmentShaderSource;

    private final float[] projectionMatrix = new float[16];

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    public SimpleRenderer(@NonNull Context context) {
        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

        vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);

        int programId = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(programId);

        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX);

        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);

        // 在虚拟坐标空间中，拟定顶点数据
        // 进行各种投影操作
        // 转化成着色器gl_Position
        // 透视除法
        // 归一化设备坐标
        // 视口变换
        // 窗口坐标

        // 计算正交投影矩阵（将坐标放入虚拟空间计算位置，然后使用正交投影将其转化为归一化设备坐标，解决横竖屏切换宽高比变化问题）
        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        if (width > height) {
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1F, 1F, -1F, 1F);
        } else {
            Matrix.orthoM(projectionMatrix, 0, -1F, 1F, -aspectRatio, aspectRatio, -1F, 1F);
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 将正交投影矩阵传递到着色器shader
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);      // 三角扇
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }
}
