package com.melody.mypreview.magic.engine

import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.Matrix
import android.view.View
import com.melody.mypreview.Engine
import com.melody.mypreview.ShaderUtil
import java.nio.FloatBuffer

class SoulOutEngine :
    Engine {

    private var mBuffer: FloatBuffer? = null

    private var mShaderProgram = -1

    private var aPositionLocation = -1
    private var aTextureCoordLocation = -1
    private var uTextureMatrixLocation = -1
    private var uTextureSamplerLocation = -1
    private var uTextureAlphaLocation = -1
    private var uMvpMatrixLocation = -1

    private var mOESTextureId: Int = -1


    private var mProgress = 0.0f

    private var mFrames = 0

    private val mMaxFrames = 15

    private val mSkipFrames = 8
    private val mMvpMatrix = FloatArray(16)

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

    override
    fun initConfig(textureId: Int, view: View) {
        this.mOESTextureId = textureId

        mBuffer = ShaderUtil.createBuffer(vertexData)
        var vertexShader = ShaderUtil.loadFromAssetsFile("souloutvertex.glsl", view.resources!!)
        var fragmentShader = ShaderUtil.loadFromAssetsFile("souloutcolor.glsl", view.resources!!)
        mShaderProgram = ShaderUtil.createShaderProgram(vertexShader!!, fragmentShader!!)
    }

    val POSITION_ATTRIBUTE = "aPosition"
    val TEXTURE_COORD_ATTRIBUTE = "aTextureCoordinate"
    val TEXTURE_MATRIX_UNIFORM = "uTextureMatrix"
    val TEXTURE_SAMPLER_UNIFORM = "uTextureSampler"
    val TEXTURE_APLHA_UNIFORM = "uAlpha"
    val MVP_MATRIX = "uMvpMatrix"

    private fun useProgram() {
        GLES30.glUseProgram(mShaderProgram)
        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, POSITION_ATTRIBUTE)
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_SAMPLER_UNIFORM)
        uTextureAlphaLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_APLHA_UNIFORM)
        uMvpMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, MVP_MATRIX)


    }

    override fun drawTexture(transformMatrix: FloatArray) {
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA)
        GLES30.glUseProgram(mShaderProgram)
        aPositionLocation = GLES30.glGetAttribLocation(mShaderProgram, POSITION_ATTRIBUTE)
        aTextureCoordLocation = GLES30.glGetAttribLocation(mShaderProgram, TEXTURE_COORD_ATTRIBUTE)
        uTextureMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_MATRIX_UNIFORM)
        uTextureSamplerLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_SAMPLER_UNIFORM)
        uTextureAlphaLocation = GLES30.glGetUniformLocation(mShaderProgram, TEXTURE_APLHA_UNIFORM)
        uMvpMatrixLocation = GLES30.glGetUniformLocation(mShaderProgram, MVP_MATRIX)

        mProgress = mFrames.toFloat() / mMaxFrames
        if (mProgress > 1f) {
            mProgress = 0f
        }
        mFrames++
        if (mFrames > mMaxFrames + mSkipFrames) {
            mFrames = 0
        }

        var backAlpha = 1f
        var alpha = 0f
        if (mProgress > 0f) {
            alpha = 0.2f - mProgress * 0.2f
            backAlpha = 1 - alpha
        }

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mOESTextureId)
        GLES30.glUniform1i(uTextureSamplerLocation, 0)
        GLES30.glUniformMatrix4fv(uTextureMatrixLocation, 1, false, transformMatrix, 0)

        Matrix.setIdentityM(mMvpMatrix, 0)
        GLES30.glUniformMatrix4fv(uMvpMatrixLocation, 1, false, mMvpMatrix, 0)
        GLES30.glUniform1f(uTextureAlphaLocation, backAlpha)
        if (mBuffer != null) {
            mBuffer!!.position(0)
            GLES30.glEnableVertexAttribArray(aPositionLocation)
            GLES30.glVertexAttribPointer(aPositionLocation, 2, GLES30.GL_FLOAT, false, 16, mBuffer)

            mBuffer!!.position(2)
            GLES30.glEnableVertexAttribArray(aTextureCoordLocation)
            GLES30.glVertexAttribPointer(aTextureCoordLocation, 2, GLES30.GL_FLOAT, false, 16, mBuffer)

            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        }

        if (mProgress > 0f) {
            GLES30.glUniform1f(uTextureAlphaLocation, alpha)
            val scale = 1.0f + 1f * mProgress
            Matrix.scaleM(mMvpMatrix, 0, scale, scale, scale)
            GLES30.glUniformMatrix4fv(uMvpMatrixLocation, 1, false, mMvpMatrix, 0)
            GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        }
//        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES30.glDisable(GLES30.GL_BLEND)
    }


    fun getShaderProgram(): Int {
        return mShaderProgram
    }

    fun getBuffer(): FloatBuffer? {
        return mBuffer
    }

    override
    fun deinit() {
        mBuffer = null
    }
}