package com.lobesoftware.toof.firebase_chat_001.screen.authentication

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface AuthenticationNavigator {

    fun goToMainScreen()
}

class AuthenticationNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), AuthenticationNavigator {
    override fun goToMainScreen() {
        activity.startActivity(MainActivity.getInstance(activity), removeItself = true)
    }
}
