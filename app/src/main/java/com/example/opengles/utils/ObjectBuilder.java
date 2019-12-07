package com.example.opengles.utils;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder {

    private static final int FLOATS_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset = 0;

    private final List<DrawCommand> drawList = new ArrayList<>();

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final List<DrawCommand> drawCommands;
        final float[] vertexData;

        GeneratedData(final float[] vertexData, final List<DrawCommand> drawCommands) {
            this.drawCommands = drawCommands;
            this.vertexData = vertexData;
        }
    }

    // 构建一个圆形所需的顶点数
    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    // 构建一个圆柱侧面所需顶点数
    private static int sizeOfCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    /**
     * 创建一个圆
     */
    private void createCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // 圆心点
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    /**
     * 创建一个圆柱
     */
    private void createCylinder(Geometry.Cylinder cylinder, final int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCylinderInVertices(numPoints);

        final float yStart = cylinder.center.y - cylinder.height / 2;
        final float yEnd = cylinder.center.y + cylinder.height / 2;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);

            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    /**
     * 创建一个冰球
     */
    private GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) + sizeOfCylinderInVertices(numPoints);

        ObjectBuilder objectBuilder = new ObjectBuilder(size);
        Geometry.Circle puckTop  = new Geometry.Circle(puck.center.translateY(puck.height / 2), puck.radius);
        objectBuilder.createCircle(puckTop, numPoints);
        objectBuilder.createCylinder(puck, numPoints);

        return objectBuilder.build();
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

}
