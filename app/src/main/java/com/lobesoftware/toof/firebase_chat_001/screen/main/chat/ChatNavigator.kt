package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.create_group.CreateGroupFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface ChatNavigator {

    fun goToAuthenticationScreen()

    fun goToCreateGroupScreen(fragment: ChatFragment)
}

class ChatNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ChatNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun goToCreateGroupScreen(fragment: ChatFragment) {
        (activity as? MainActivity)?.hideBottomNavigation()
        activity.replaceFragment(
            R.id.frame_layout_container,
            CreateGroupFragment.getInstance(activity.getString(R.string.add)),
            true
        )
    }
}
