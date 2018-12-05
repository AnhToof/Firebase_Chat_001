package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface ChatDetailContract {

    interface View {

        fun onCheckCurrentUserFail()

        fun onFetchFail()

        fun onFetchUsersInGroupSuccess(users: List<User>)

        fun onMessageAdded(message: Message)
    }

    interface Presenter : BasePresenter<View> {

        fun fetchUsersInGroup(group: Group)

        fun fetchMessages(group: Group)

        fun sendMessage(group: Group, message: Message)
    }
}
