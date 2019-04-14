package com.example.opengles.custom;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.example.opengles.R;
import com.example.opengles.utils.ShaderHelper;
import com.example.opengles.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;

    private float[] tableVerticesWithTriangles = {
//            // 第一个三角形
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//            // 第二个三角形
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            0.5f, 0.5f,
//            // 中间的分界线
//            -0.5f, 0f,
//            0.5f, 0f,
//            // 两个木槌的质点位置
//            0f, -0.25f,
//            0f, 0.25f

            // 三角扇
            0,     0,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f,  0.5f,
            -0.5f,  0.5f,
            -0.5f, -0.5f,
            // 中间的分界线
            -0.5f, 0f,
            0.5f, 0f,
            // 两个木槌的质点位置
            0f, -0.25f,
            0f, 0.25f
    };

    private String vertexShaderSource;          // 订单着色器
    private String fragmentShaderSource;        // 片段着色器

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;
    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    public HockeyRenderer(Context context) {
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

        vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
    }

    // Surface创建是调用，资源被回收后会重新调用。
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 设置清空屏幕用的颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        // 构建程序
        int programId = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(programId);

        uColorLocation = GLES20.glGetUniformLocation(programId, U_COLOR);
        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);

        // 让OpenGL到缓冲区vertexData中寻找a_Position对应的数据
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);
    }

    // Surface尺寸变化时调用，横竖屏切换调用。
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置区域，当前是全屏。
        GLES20.glViewport(0, 0, width, height);
    }

    // 绘制每一帧时调用，一定要绘制一些东西，即使只是清空屏幕。
    // 因为该方法结束后渲染缓冲区会被交换，并显示在屏幕上。如果什么都没画，可能会看到糟糕的闪烁效果。
    // 我们调用 GLES20.glClear(GL_COLOR_BUFFER_BIT)清空屏幕，这会擦除屏幕上的所有颜色，并用之前glClearColor调用定义的颜色填充整个屏幕。
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // 更新着色器代码中u_Color的值
        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
//        // 告诉OpenGL要画三角形，0表示从顶点数组的开头处开始读顶点，6表示读取6组元素，所以会画两个三角形。
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0 ,6);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        // 绘制线
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        // 从数组的第6组开始，读取两组元素。
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        // 绘制两个点
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }

}