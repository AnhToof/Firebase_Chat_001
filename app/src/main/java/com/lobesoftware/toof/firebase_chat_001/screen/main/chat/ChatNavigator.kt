package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface ChatNavigator {

    fun goToAuthenticationScreen()
}

class ChatNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ChatNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }
}
