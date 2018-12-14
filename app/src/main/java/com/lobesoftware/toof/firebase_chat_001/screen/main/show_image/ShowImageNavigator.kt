package com.lobesoftware.toof.firebase_chat_001.screen.main.show_image

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface ShowImageNavigator {

    fun backToPreviousScreen()
}

class ShowImageNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ShowImageNavigator {

    override fun backToPreviousScreen() {
        activity.supportFragmentManager.popBackStack()
    }
}
