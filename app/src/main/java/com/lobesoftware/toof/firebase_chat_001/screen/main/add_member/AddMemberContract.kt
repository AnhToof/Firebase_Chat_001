package com.lobesoftware.toof.firebase_chat_001.screen.main.add_member

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface AddMemberContract {

    interface View {

        fun onCheckCurrentUserFail()

        fun onMemberRemoved(user: User)

        fun onMemberChanged(user: User)

        fun onMemberAdded(user: User)

        fun onFilterMemberSuccess(membersFiltered: List<User>)

        fun onFetchFail()
    }

    interface Presenter : BasePresenter<View> {

        fun fetchMembers()

        fun filterMembers(searchText: String, members: ArrayList<User>)
    }
}
