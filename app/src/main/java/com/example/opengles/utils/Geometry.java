package com.example.opengles.utils;

public class Geometry {

    // 几何点
    public static class Point {
        public final float x, y, z;

        public Point(final float x, final float y, final float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateX(float value) {
            return new Point(x + value, y, z);
        }

        public Point translateY(float value) {
            return new Point(x, y + value, z);
        }

        public Point translateZ(float value) {
            return new Point(x, y, z + value);
        }
    }

    // 圆形 = 中心点 + 半径
    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(final Point center, final float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }
    }

    // 圆柱
    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(final Point center, final float radius, final float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }
    }

}
