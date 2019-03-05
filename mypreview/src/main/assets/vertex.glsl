#version 300 es
layout (location = 0) in vec4 aPosition;  //顶点位置
layout (location = 1) in vec4 aTextureCoordinate;  //顶点颜色

uniform mat4 uTextureMatrix;

out vec2 vTextureCoord;

void main() {
    gl_Position = aPosition; //根据总变换矩阵计算此次绘制此顶点位置
    vTextureCoord = (uTextureMatrix * aTextureCoordinate).xy;
}