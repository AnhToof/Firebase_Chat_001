package com.lobesoftware.toof.firebase_chat_001.screen.main.add_member

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.main.create_group.CreateGroupFragment
import com.lobesoftware.toof.firebase_chat_001.utils.BaseNavigator
import com.lobesoftware.toof.firebase_chat_001.utils.Constant

interface AddMemberNavigator {

    fun goToAuthenticationScreen()

    fun backToAddGroupScreen()

    fun backToAddGroupScreenWithValue(fragment: AddMemberFragment, members: ArrayList<User>)
}

class AddMemberNavigatorImpl(activity: AppCompatActivity) : BaseNavigator(activity), AddMemberNavigator {

    override fun goToAuthenticationScreen() {
        activity.startActivity(AuthenticationActivity.getInstance(activity), removeItself = true)
    }

    override fun backToAddGroupScreen() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun backToAddGroupScreenWithValue(fragment: AddMemberFragment, members: ArrayList<User>) {
        val intent = Intent(activity, fragment::class.java)
        intent.putParcelableArrayListExtra(CreateGroupFragment.EXTRA_ARRAY, members)
        fragment.targetFragment?.onActivityResult(fragment.targetRequestCode, Constant.ResultCode.RESULT_OK, intent)
        activity.supportFragmentManager.popBackStack()
    }
}
