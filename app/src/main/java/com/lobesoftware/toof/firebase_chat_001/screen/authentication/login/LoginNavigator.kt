package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.register.RegisterFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface LoginNavigator {

    fun goToMainScreen()

    fun goToRegisterScreen()
}

class LoginNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), LoginNavigator {

    override fun goToMainScreen() {
        activity.startActivity(MainActivity.getInstance(activity), removeItself = true)
    }

    override fun goToRegisterScreen() {
        activity.replaceFragment(R.id.constraint_layout_container, RegisterFragment.getInstance(), true)
    }
}
