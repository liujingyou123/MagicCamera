package com.melody.mypreview

import android.app.Activity
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.util.Log
import android.view.Surface
import java.io.IOException

@Suppress("DEPRECATION")
class Camera (var mActivity: Activity){
    private var mCameraId: Int = 0
    private var mCamera: Camera? = null

    fun openCamera(screenWidth: Int, screenHeight: Int, cameraId: Int): Boolean{
        try {
            //设置相机参数
            mCameraId = cameraId
            mCamera = Camera.open(mCameraId)
            val parameters = mCamera!!.parameters
            parameters.set("orientation", "portrait")
            parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            parameters.setPreviewSize(1280, 720)
            setCameraDisplayOrientation(mActivity, mCameraId, mCamera!!)
            mCamera!!.parameters = parameters
            Log.i("lb6905", "open camera")
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    fun setCameraDisplayOrientation(activity: Activity, cameraId: Int, camera: Camera) {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        val rotation = activity.windowManager.defaultDisplay
            .rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360
        }
        camera.setDisplayOrientation(result)
    }

    fun startPreview() {
        if (mCamera != null) {
            mCamera!!.startPreview()
        }
    }

    fun stopPreview() {
        if (mCamera != null) {
            mCamera!!.stopPreview()
        }
    }

    fun setPreviewTexture(surfaceTexture: SurfaceTexture) {
        if (mCamera != null) {
            try {
                mCamera!!.setPreviewTexture(surfaceTexture)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun releaseCamera() {
        if (mCamera != null) {
            mCamera!!.release()
            mCamera = null
        }
    }
}