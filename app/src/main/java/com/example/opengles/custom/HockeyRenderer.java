package com.example.opengles.custom;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.opengles.R;
import com.example.opengles.utils.MatrixHelper;
import com.example.opengles.utils.ShaderHelper;
import com.example.opengles.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HockeyRenderer implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    // 跨距，每组数据之间跨距（目前数组中一组数据包含一个坐标位置和一组颜色值）
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private FloatBuffer vertexData;

    private float[] tableVerticesWithTriangles = {
            //  X, Y,        R, G, B
            // 三角扇形
            0,      0,      1f,     1f,     1f,
           -0.5f,  -0.8f,   0.7f,   0.7f,   0.7f,
            0.5f,  -0.8f,   0.7f,   0.7f,   0.7f,
            0.5f,   0.8f,   0.7f,   0.7f,   0.7f,
           -0.5f,   0.8f,   0.7f,   0.7f,   0.7f,
           -0.5f,  -0.8f,   0.7f,   0.7f,   0.7f,
            // 中间的分界线
           -0.5f,   0f,     1f,     0f,     0f,
            0.5f,   0f,     1f,     0f,     0f,
            // 两个木槌的质点位置
            0f,    -0.25f,  0f,     0f,     1f,
            0f,     0.25f,  1f,     0f,     0f,
    };

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] uMatrix = new float[16];

    private String vertexShaderSource;          // 订单着色器
    private String fragmentShaderSource;        // 片段着色器

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

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
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // 构建程序
        int programId = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource);
        GLES20.glUseProgram(programId);

        aPositionLocation = GLES20.glGetAttribLocation(programId, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(programId, A_COLOR);
        uMatrixLocation = GLES20.glGetUniformLocation(programId, U_MATRIX);

        // 让OpenGL到缓冲区vertexData中寻找a_Position对应的数据
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(2);
        GLES20.glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);
    }

    // Surface尺寸变化时调用，横竖屏切换调用。
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置区域，当前是全屏。
        GLES20.glViewport(0, 0, width, height);

//        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
//        if (width > height) {
//            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float) height, 1f, 100f);

        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);         // 平移
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);    // 旋转

        Matrix.multiplyMM(uMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
    }

    // 绘制每一帧时调用，一定要绘制一些东西，即使只是清空屏幕。
    // 因为该方法结束后渲染缓冲区会被交换，并显示在屏幕上。如果什么都没画，可能会看到糟糕的闪烁效果。
    // 我们调用 GLES20.glClear(GL_COLOR_BUFFER_BIT)清空屏幕，这会擦除屏幕上的所有颜色，并用之前glClearColor调用定义的颜色填充整个屏幕。
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

//        GLES20.glUniformMatrix4fv(uMatrixLocation,1, false,  projectionMatrix,0);
        GLES20.glUniformMatrix4fv(uMatrixLocation,1, false,  uMatrix,0);

        // 告诉OpenGL要画三角形，0表示从顶点数组的开头处开始读顶点，6表示读取6组元素，所以会画两个三角形。
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);

        // 绘制线
        // 从数组的第6组开始，读取两组元素。
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);

        // 绘制两个点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);
    }

}