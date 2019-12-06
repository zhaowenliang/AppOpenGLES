package com.example.opengles.data;

import android.opengl.GLES20;

public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            //x,    y,      s,      t
            0f,     0f,     0.5f,   0.5f,
            -0.5f,  -0.8f,  0f,     0.9f,
            0.5f,   -0.8f,  1f,     0.9f,
            0.5f,   0.8f,   1f,     0.1f,
            -0.5f,  0.8f,   0f,     0.1f,
            -0.5f,  -0.8f,  0f,     0.9f,
    };

    private final VertexArray vertexArray;

    public Table(){
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram shaderProgram) {
        vertexArray.setVertexAttributePointer(shaderProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE, 0);
        vertexArray.setVertexAttributePointer(shaderProgram.aTextureCoordinatesLocation, TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE, POSITION_COMPONENT_COUNT);
    }

    public void draw(){
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }

}