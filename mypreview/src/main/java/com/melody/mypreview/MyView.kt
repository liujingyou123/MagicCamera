package com.melody.mypreview

import android.content.Context
import android.opengl.GLSurfaceView

class MyView(context: Context) : GLSurfaceView(context) {
    private var mRenderer: SceneRender? = null

    fun init(camera: Camera, isPreviewStarted: Boolean, context: Context) {
        //配置OpenGL ES，主要是版本设置和设置Renderer，Renderer用于执行OpenGL的绘制
        setEGLContextClientVersion(3)
        mRenderer = SceneRender(this,camera, isPreviewStarted, context)
        //初始化renderer
//        mRenderer.init(this, camera, isPreviewStarted, context)
        //设置renderer，主要工作都在renderer的3个回调函数里面去完成了
        setRenderer(mRenderer)
    }

    fun deinit() {
        if (mRenderer != null) {
            mRenderer?.deinit()
            mRenderer = null
        }
    }
}