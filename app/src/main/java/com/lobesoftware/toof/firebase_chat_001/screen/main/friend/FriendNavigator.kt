package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface FriendNavigator {

    fun goToAuthenticationScreen()
}

class FriendNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), FriendNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }
}
