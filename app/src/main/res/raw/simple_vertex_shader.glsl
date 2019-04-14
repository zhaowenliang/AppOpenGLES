attribute vec4 a_Position;

void main() {
    // 内置变量，用于顶点着色器顶点位置
    gl_Position = a_Position;
    // 内置变量，用于顶点着色器光栅后的点大小，像素个数
    gl_PointSize = 10.0;                            // 指定点的大小五为10.0
}