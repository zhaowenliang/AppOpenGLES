package com.example.opengles.utils;

import android.opengl.GLES20;

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译着色器
     *
     * @param type       着色器类型
     * @param shaderCode 着色器代码
     * @return 编译状态
     */
    private static int compileShader(int type, String shaderCode) {
        // 创建一个着色器对象
        int shaderObjectId = GLES20.glCreateShader(type);
        if (shaderObjectId == 0) {
            if (LogUtils.ON) {
                LogUtils.d(TAG, "Warning! Could not create new shader, glGetError:" + GLES20.glGetError());
            }
            return 0;
        }

        // 上传和编译着色器代码
        GLES20.glShaderSource(shaderObjectId, shaderCode);
        GLES20.glCompileShader(shaderObjectId);

        // 获取编译状态
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (LogUtils.ON) {
            LogUtils.i(TAG, "Result of compiling source:" + "\n" + shaderCode + "\n" + GLES20.glGetShaderInfoLog(shaderObjectId));
        }

        if (compileStatus[0] == 0) {
            // 如果编译失败则删除着色器
            GLES20.glDeleteShader(shaderObjectId);
            if (LogUtils.ON) {
                LogUtils.w(TAG, "Warning! Compilation of shader failed, glGetError:" + GLES20.glGetError());
            }
            return 0;
        }

        return shaderObjectId;
    }

    /**
     * 链接着色器到OpenGL的程序
     *
     * @param vertexShaderId   顶点着色器Id
     * @param fragmentShaderId 片段着色器Id
     * @return 链接状态
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 创建程序对象
        int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            if (LogUtils.ON) {
                LogUtils.w(TAG, " Warning! Could not create new program, glGetError:" + GLES20.glGetError());
            }
            return 0;
        }

        // 绑定顶点着色器和片段着色器
        GLES20.glAttachShader(programObjectId, vertexShaderId);
        GLES20.glAttachShader(programObjectId, fragmentShaderId);
        // 链接两个着色器
        GLES20.glLinkProgram(programObjectId);

        // 获取链接状态
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (LogUtils.ON) {
            LogUtils.i(TAG, "Result of linking program:" + GLES20.glGetProgramInfoLog(programObjectId));
        }

        if (linkStatus[0] == 0) {
            // 如果链接失败则删除程序
            GLES20.glDeleteProgram(programObjectId);
            if (LogUtils.ON) {
                LogUtils.w(TAG, " Warning! Linking of program failed, glGetError:" + GLES20.glGetError());
            }
            return 0;
        }

        return programObjectId;
    }

    /**
     * 构建程序
     * @param vertexShaderSource 顶点着色器代码
     * @param fragmentShaderSource 片段着色器代码
     * @return 程序id
     */
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        int programObjectId = linkProgram(vertexShader, fragmentShader);

        validateProgram(programObjectId);
        return programObjectId;
    }

    /**
     * 验证程序是否有效
     * @param programObjectId 程序id
     * @return 是否有效
     */
    private static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);

        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);
        if (LogUtils.ON) {
            LogUtils.i(TAG, "Result of validating program:" + validateStatus[0] + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId));
        }

        return validateStatus[0] != GLES20.GL_FALSE;
    }

}
