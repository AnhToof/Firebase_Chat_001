package com.lobesoftware.toof.firebase_chat_001.screen.main.user_detail

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface UserDetailContract {

    interface View {

        fun onCheckCurrentUserFail()

        fun onAlreadyFriend()

        fun onRequestedFriend()

        fun onReceivedFriendRequest()

        fun onNotFriend()

        fun onFetchFail(error: Throwable)

        fun onFetchGroupWithFriendSuccess(group: Group)
    }

    interface Presenter : BasePresenter<View> {

        fun fetchFriendState(user: User)

        fun addFriend(friendId: String)

        fun acceptFriend(user: User)

        fun unFriend(friendId: String)

        fun fetchGroupWithFriendInformation(user: User)
    }
}
