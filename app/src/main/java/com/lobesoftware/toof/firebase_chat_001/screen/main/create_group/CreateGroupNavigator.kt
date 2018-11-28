package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.add_member.AddMemberFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator

interface CreateGroupNavigator {

    fun goToAuthenticationScreen()

    fun backToChatScreen()

    fun goToAddMemberScreen(fragment: CreateGroupFragment, members: ArrayList<User>)
}

class CreateGroupNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), CreateGroupNavigator {

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

    override fun goToAddMemberScreen(fragment: CreateGroupFragment, members: ArrayList<User>) {
        activity.replaceFragment(
            R.id.frame_layout_container,
            AddMemberFragment.getInstance(activity.getString(R.string.add), members),
            true,
            currentFragment = fragment
        )
    }
}
