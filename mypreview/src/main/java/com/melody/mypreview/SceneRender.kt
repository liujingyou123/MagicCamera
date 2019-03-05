package com.melody.mypreview

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SceneRender(
    var mGLSurfaceView: MyView,
    var mCamera: Camera?,
    var isPreviewStarted: Boolean,
    var context: Context
) :
    GLSurfaceView.Renderer {

    var TAG = "SceneRender"
    private var mFBOIds = IntArray(1)
    private val transformMatrix = FloatArray(16)

    private var mOESTextureId = -1
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mCameraEngine: CameraEngine? = null


    override fun onDrawFrame(gl: GL10?) {
        val t1 = System.currentTimeMillis()

        if (mSurfaceTexture != null) {
            mSurfaceTexture!!.updateTexImage()
            mSurfaceTexture!!.getTransformMatrix(transformMatrix)
        }

        if (!isPreviewStarted) {
            //在onDrawFrame方法中调用此方法initSurfaceTexture（）;
            // 有了外部纹理，现在可以实例化一个SurfaceTexture了，之后即可开启Camera预览
            isPreviewStarted = initSurfaceTexture()
            isPreviewStarted = true
            return
        }

        GLES30.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        mCameraEngine?.drawTexture(transformMatrix)

        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0)

        val t2 = System.currentTimeMillis()
        val t = t2 - t1
        Log.i("onDrawFrame", "onDrawFrame: time: $t")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mOESTextureId = ShaderUtil.createTextureObject()

        mCameraEngine = CameraEngine(mOESTextureId, context, mGLSurfaceView)

        GLES30.glGenFramebuffers(1, mFBOIds, 0)
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, mFBOIds[0])
        Log.i(TAG, "onSurfaceCreated: mFBOId: " + mFBOIds[0])

    }

    fun initSurfaceTexture(): Boolean {
        if (mCamera == null || mGLSurfaceView == null) {
            Log.i(TAG, "mCamera or mGLSurfaceView is null!")
            return false
        }

        mSurfaceTexture = SurfaceTexture(mOESTextureId)
        mSurfaceTexture?.setOnFrameAvailableListener {
            mGLSurfaceView.requestRender()
        }
        //将此SurfaceTexture作为相机预览输出
        mCamera?.setPreviewTexture(mSurfaceTexture!!)
        mCamera?.startPreview()
        return true
    }

    fun deinit() {
        if (mCameraEngine != null) {
            mCameraEngine?.deinit()
            mCameraEngine = null
        }

        if (mSurfaceTexture != null) {
            mSurfaceTexture?.release()
            mSurfaceTexture = null
        }
        mCamera = null
        mOESTextureId = -1
        isPreviewStarted = false
    }

}