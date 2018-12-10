package com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface AddFriendNavigator {

    fun goToAuthenticationScreen()

    fun backToFriendScreen()
}

class AddFriendNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), AddFriendNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToFriendScreen() {
        activity.supportFragmentManager.popBackStack()
    }
}
