package com.melody.mypreview

import android.content.Context
import android.opengl.GLES11Ext
import android.opengl.GLES30
import java.nio.FloatBuffer

class CameraEngine(var mOESTextureId: Int, var mConext: Context, mView: MyView) {
    private var mBuffer: FloatBuffer? = null

    private var mShaderProgram = -1

    private var aPositionLocation = -1
    private var aTextureCoordLocation = -1
    private var uTextureMatrixLocation = -1
    private var uTextureSamplerLocation = -1
    private val vertexData = floatArrayOf(
        1f,
        1f,
        1f,
        1f,
        -1f,
        1f,
        0f,
        1f,
        -1f,
        -1f,
        0f,
        0f,
        1f,
        1f,
        1f,
        1f,
        -1f,
        -1f,
        0f,
        0f,
        1f,
        -1f,
        1f,
        0f
    )
    init {
        mBuffer = ShaderUtil.createBuffer(vertexData)
        var vertexShader = ShaderUtil.loadFromAssetsFile("vertex.glsl", mView.resources)
        var fragmentShader = ShaderUtil.loadFromAssetsFile("colorfrag.glsl", mView.resources)
        mShaderProgram = ShaderUtil.createShaderProgram(vertexShader!!, fragmentShader!!)
    }




    val POSITION_ATTRIBUTE = "aPosition"
    val TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate"
    val TEXTURE_MATRIX_UNIFORM = "uTextureMatrix"
    val TEXTURE_SAMPLER_UNIFORM = "uTextureSampler"


    fun drawTexture(transformMatrix: FloatArray) {
        GLES30.glUseProgram(mShaderProgram)
        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, POSITION_ATTRIBUTE)
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_SAMPLER_UNIFORM)

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)
        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)

        if (mBuffer != null) {
            mBuffer!!.position(0)
            GLES30.glEnableVertexAttribArray(aPositionLocation)
            GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, mBuffer)

            mBuffer!!.position(2)
            GLES30.glEnableVertexAttribArray(aTextureCoordLocation)
            GLES30.glVertexAttribPointer(aTextureCoordLocation, 2, GLES30.GL_FLOAT, false, 16, mBuffer)

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        }
    }

    fun getShaderProgram(): Int {
        return mShaderProgram
    }

    fun getBuffer(): FloatBuffer? {
        return mBuffer
    }

    fun deinit() {
        mBuffer = null
    }
}