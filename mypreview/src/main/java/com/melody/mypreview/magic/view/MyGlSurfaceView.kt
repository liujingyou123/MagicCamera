package com.melody.mypreview.magic.view

import android.content.Context
import android.opengl.GLSurfaceView
import com.melody.mypreview.Camera
import com.melody.mypreview.magic.engine.SoulOutEngine
import com.melody.mypreview.magic.render.MagicRender

class MyGlSurfaceView(context: Context) : GLSurfaceView(context) {
    private var mRenderer: MagicRender? = null

    fun init(camera: Camera, isPreviewStarted: Boolean, context: Context) {
        //配置OpenGL ES，主要是版本设置和设置Renderer，Renderer用于执行OpenGL的绘制
        setEGLContextClientVersion(3)
        mRenderer = MagicRender(this, camera, isPreviewStarted, context)
        mRenderer?.setEngine(SoulOutEngine())
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
