package com.melody.mypreview

import android.view.View

interface Engine {
    fun drawTexture(transformMatrix: FloatArray)
    fun deinit()
    fun initConfig(textureId: Int,view: View)
}