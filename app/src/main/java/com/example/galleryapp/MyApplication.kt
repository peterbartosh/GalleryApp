package com.example.galleryapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication() : Application(){
    override fun onCreate() {
//        try {
//            val field: Field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
//            field.isAccessible = true
//            field.set(null, 100 * 1024 * 1024) // 100MB is the new size
//        } catch (e: Exception) {
//            if (BuildConfig.DEBUG) {
//                e.printStackTrace()
//            }
//        }
        super.onCreate()
    }
}