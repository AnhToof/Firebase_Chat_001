package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface FriendContract {

    interface View {

        fun onFetchFriendRequestSuccess(user: User)

        fun onFriendRemoved(user: User)

        fun onFriendAdded(user: User)

        fun onFriendChanged(user: User)

        fun onCheckCurrentUserFail()

        fun onFilterFriendSuccess(users: List<User>)
    }

    interface Presenter : BasePresenter<View> {

        fun fetchFriendRequest()

        fun fetchFriend()

        fun acceptFriend(user: User)

        fun rejectFriend(user: User)

        fun filterFriend(searchText: String, users: ArrayList<User>)
    }
}
