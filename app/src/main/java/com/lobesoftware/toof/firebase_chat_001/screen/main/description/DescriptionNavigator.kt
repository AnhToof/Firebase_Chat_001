package com.lobesoftware.toof.firebase_chat_001.screen.main.description

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface DescriptionNavigator {

    fun backToChatDetailScreen()
}

class DescriptionNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), DescriptionNavigator {

    override fun backToChatDetailScreen() {
        activity.supportFragmentManager.popBackStack()
    }
}
