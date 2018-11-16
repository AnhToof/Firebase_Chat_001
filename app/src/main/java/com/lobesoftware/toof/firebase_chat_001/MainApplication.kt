package com.lobesoftware.toof.firebase_chat_001

import android.app.Application

open class MainApplication : Application() {
    lateinit var mAppComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        mAppComponent = DaggerAppComponent.builder().applicationModule(ApplicationModule(applicationContext)).build()
    }
}
