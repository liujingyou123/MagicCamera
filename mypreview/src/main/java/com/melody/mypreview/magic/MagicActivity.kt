package com.melody.mypreview.magic

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import com.melody.mypreview.Camera
import com.melody.mypreview.magic.view.MyGlSurfaceView

class MagicActivity : Activity() {
    private var mGLSurfaceView: MyGlSurfaceView? = null
    private var mCameraId: Int = 0
    private var mCamera: Camera? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //实例化一个GLSurfaceView
        mGLSurfaceView = MyGlSurfaceView(this)
        mCameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
        val dm = DisplayMetrics()
        mCamera = Camera(this)
        if (mCamera != null && !mCamera?.openCamera(dm.widthPixels, dm.heightPixels, mCameraId)!!) {
            return
        }
        //初始化GLSurfaceView,这个函数还调用了Renderer的init（）,所以一并初始化了渲染环境
        mGLSurfaceView?.init(mCamera!!, false, this)
        //在屏幕上显示GLSurfaceView
        setContentView(mGLSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (mGLSurfaceView != null) {
            mGLSurfaceView?.onPause()
            mGLSurfaceView?.deinit()
            mGLSurfaceView = null
        }

        if (mCamera != null) {
            mCamera?.stopPreview()
            mCamera?.releaseCamera()
            mCamera = null
        }
    }
}