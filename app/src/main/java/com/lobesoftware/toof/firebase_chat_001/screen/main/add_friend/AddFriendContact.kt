package com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface AddFriendContact {

    interface View {

        fun onCheckCurrentUserFail()

        fun onSearchUserSuccess(user: User, action: String?)

        fun onSearchUserFail(error: Throwable)

        fun onRequestFail()

        fun onRequestSuccess()
    }

    interface Presenter : BasePresenter<View> {

        fun searchUserByEmail(email: String)

        fun requestFriend(friendId: String)
    }
}
