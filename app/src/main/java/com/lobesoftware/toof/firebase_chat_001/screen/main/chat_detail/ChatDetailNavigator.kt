package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface ChatDetailNavigator {

    fun goToAuthenticationScreen()

    fun backToChatScreen()
}

class ChatDetailNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ChatDetailNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToChatScreen() {
        activity.supportFragmentManager.popBackStack()
    }
}
