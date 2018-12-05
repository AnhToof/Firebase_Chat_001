package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.create_group.CreateGroupFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.description.DescriptionFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator
import com.lobesoftware.toof.firebase_chat_001.utils.Constant

interface ChatDetailNavigator {

    fun goToAuthenticationScreen()

    fun backToChatScreen()

    fun goToDescriptionScreen(group: Group)

    fun goToEditGroupScreen(group: Group)
}

class ChatDetailNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), ChatDetailNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToChatScreen() {
        activity.supportFragmentManager.popBackStack()
        (activity as? MainActivity)?.let {
            it.showBottomNavigation()
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.title = activity.getString(R.string.title_chat_screen)
        }
    }

    override fun goToDescriptionScreen(group: Group) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            DescriptionFragment.getInstance(group),
            true
        )
    }

    override fun goToEditGroupScreen(group: Group) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            CreateGroupFragment.getInstance(Constant.ScreenType.EDIT, group),
            true
        )
    }
}
