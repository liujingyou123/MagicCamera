#version 300 es
#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES uTextureSampler;//纹理内容数据
in vec2 vTextureCoord; //接收从顶点着色器过来的参数
out vec4 fragColor;

void main()
{
   //进行纹理采样
   fragColor = texture2D(uTextureSampler, vTextureCoord);
}