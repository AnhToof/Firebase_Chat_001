package com.lobesoftware.toof.firebase_chat_001.screen.main.user_detail

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail.ChatDetailFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface UserDetailNavigator {

    fun goToAuthenticationScreen()

    fun backToPreviousScreen()

    fun goToConversationDetail(group: Group)
}

class UserDetailNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), UserDetailNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToPreviousScreen() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun goToConversationDetail(group: Group) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            ChatDetailFragment.getInstance(group.title ?: "", group),
            true
        )
    }
}
