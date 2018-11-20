package com.lobesoftware.toof.firebase_chat_001.screen.main.profile

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface ProfileNavigator {

    fun goTogoToAuthenticationScreen()
}

class ProfileNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ProfileNavigator {

    override fun goTogoToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }
}
