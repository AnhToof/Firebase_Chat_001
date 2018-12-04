package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend.AddFriendFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface FriendNavigator {

    fun goToAuthenticationScreen()

    fun goToAddFriendScreen()

}

class FriendNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), FriendNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun goToAddFriendScreen() {
        (activity as? MainActivity)?.hideBottomNavigation()
        activity.replaceFragment(
            R.id.frame_layout_container,
            AddFriendFragment.getInstance(activity.getString(R.string.add)),
            true
        )
    }
}
