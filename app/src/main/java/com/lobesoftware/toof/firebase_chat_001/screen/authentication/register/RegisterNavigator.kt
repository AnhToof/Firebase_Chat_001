package com.lobesoftware.toof.firebase_chat_001.screen.authentication.register

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface RegisterNavigator {

    fun backToLoginScreen()
}

class RegisterNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), RegisterNavigator {

    override fun backToLoginScreen() {
        activity.supportFragmentManager.popBackStack()
    }
}
