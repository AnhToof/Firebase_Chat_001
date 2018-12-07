package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface CreateGroupContact {

    interface View {

        fun showProgressDialog()

        fun hideProgressDialog()

        fun onCheckCurrentUserFail()

        fun onCreateGroupSuccess()

        fun onCreateGroupFail(error: Throwable)

        fun onInputDataInValid(errorMessage: String?)

        fun onFetchMembersSuccess(members: List<User>)
    }

    interface Presenter : BasePresenter<View> {

        fun fetchMembers(group: Group)

        fun createGroup(group: Group)

        fun updateGroup(group: Group)
    }
}
