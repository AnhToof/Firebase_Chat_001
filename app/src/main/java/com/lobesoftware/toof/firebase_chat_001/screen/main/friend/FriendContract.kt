package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface FriendContract {

    interface View {

        fun onFetchFriendRequestSuccess(user: User)

        fun onFetchFriendSuccess(user: User)

        fun onCheckCurrentUserFail()
    }

    interface Presenter : BasePresenter<View> {

        fun fetchFriendRequest()

        fun fetchFriend()

        fun acceptFriend(user: User)

        fun rejectFriend(user: User)
    }
}
