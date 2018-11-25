package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface ChatContract {

    interface View {

        fun onConversationRemoved(group: Group)

        fun onConversationAdded(group: Group)

        fun onConversationChanged(group: Group)

        fun onCheckCurrentUserFail()

        fun onFilterConversationSuccess(conversations: List<Group>)

        fun onFetchFail()
    }

    interface Presenter : BasePresenter<View> {

        fun fetchConversations()

        fun filterConversation(searchText: String, conversations: ArrayList<Group>)
    }
}
