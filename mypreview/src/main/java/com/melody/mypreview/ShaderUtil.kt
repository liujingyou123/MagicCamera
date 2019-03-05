package com.melody.mypreview

import android.annotation.SuppressLint
import android.content.res.Resources
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.charset.Charset
import javax.microedition.khronos.opengles.GL10

object ShaderUtil {

    //返回着色器脚本内容
    fun loadFromAssetsFile(file: String, r: Resources): String? {
        var result: String? = null
        try {
            val read = r.assets.open(file)
            var ch = 0
            val baos = ByteArrayOutputStream()
            while (true) {
                ch = read.read()
                if (ch <= 0) {
                    break
                }

                baos.write(ch)
            }
            val buff = baos.toByteArray()
            baos.close()
            read.close()
            result = String(buff, Charset.forName("UTF-8"))
            result = result.replace("\\r\\n".toRegex(), "\n")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    //创建着色器程序
    fun createShaderProgram(vertexSource: String, fragmentSource: String): Int {
        var vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource)
        if (vertexShader == 0) return 0

        var fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentShader == 0) return 0

        //创建渲染程序
        var program = GLES30.glCreateProgram()

        //若程序创建成功向程序中加入顶点何片元着色器
        if (program != 0) {
            //向程序中加入顶点着色器
            GLES30.glAttachShader(program, vertexShader)
            checkGlError("glAttachShader")
            //向程序中加入片元着色器
            GLES30.glAttachShader(program, fragmentShader)
            checkGlError("glAttachShader")

            //链接程序
            GLES30.glLinkProgram(program)
            //存放链接成功program数量的数组
            var linkStatus = IntArray(1)
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0)
            //若链接失败则报错并删除程序
            if (linkStatus != null && linkStatus[0] != GLES30.GL_TRUE) {
                Log.e("ES20_ERROR", "Could not link program: ")
                Log.e("ES20_ERROR", GLES30.glGetProgramInfoLog(program))
                GLES30.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    //加载着色器
    fun loadShader(shaderType: Int, source: String): Int {
        //创建新的着色器
        var shader = GLES30.glCreateShader(shaderType)
        //若创建成功则加载shader
        if (shader != 0) {
            //加载shader的源代码
            GLES30.glShaderSource(shader, source)
            //编译shader
            GLES30.glCompileShader(shader)

            var compiled = IntArray(1)
            //获取shader的编译情况
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled != null && compiled[0] == 0) {//若编译失败则显示错误日志并删除此shader
                Log.e("ES20_ERROR", "Could not compile shader $shaderType:")
                Log.e("ES20_ERROR", GLES30.glGetShaderInfoLog(shader))
                GLES30.glDeleteShader(shader);
                shader = 0
            }
        }
        return shader
    }

    fun createTextureObject(): Int {
        var tex = IntArray(1)

        //生成一个纹理
        GLES30.glGenTextures(1, tex, 0)

        //将此纹理绑定到外部纹理上
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])
        //设置纹理过滤参数
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
        GLES30.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_CLAMP_TO_EDGE.toFloat()
        )
        GLES30.glTexParameterf(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_CLAMP_TO_EDGE.toFloat()
        )

        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0)
        return tex[0]
    }

    //    将顶点和纹理坐标数据使用FloatBuffer来存储，防止内存回收
    fun createBuffer(vertexData: FloatArray): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertexData, 0, vertexData.size).position(0)
        return buffer
    }

    //检查每一步操作是否有错误的方法
    @SuppressLint("NewApi")
    fun checkGlError(op: String) {
        val error = GLES30.glGetError()
        while (error != GLES30.GL_NO_ERROR) {
            Log.e("ES20_ERROR", "$op: glError $error")
            throw RuntimeException("$op: glError $error")
        }
    }
}