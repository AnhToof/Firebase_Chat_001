package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail.ChatDetailFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.create_group.CreateGroupFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator
import com.lobesoftware.toof.firebase_chat_001.utils.Constant

interface ChatNavigator {

    fun goToAuthenticationScreen()

    fun goToCreateGroupScreen()

    fun goToConversationDetail(group: Group)
}

class ChatNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ChatNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun goToCreateGroupScreen() {
        activity.replaceFragment(
            R.id.frame_layout_container,
            CreateGroupFragment.getInstance(Constant.ScreenType.ADD, null),
            true
        )
    }

    override fun goToConversationDetail(group: Group) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            ChatDetailFragment.getInstance(group.title ?: "", group),
            true
        )
    }
}
