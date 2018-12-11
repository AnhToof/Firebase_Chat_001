package com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.user_detail.UserDetailFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface AddFriendNavigator {

    fun goToAuthenticationScreen()

    fun backToFriendScreen()

    fun goToUserDetailScreen(user: User)
}

class AddFriendNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), AddFriendNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToFriendScreen() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun goToUserDetailScreen(user: User) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            UserDetailFragment.getInstance(user),
            true
        )
    }
}
