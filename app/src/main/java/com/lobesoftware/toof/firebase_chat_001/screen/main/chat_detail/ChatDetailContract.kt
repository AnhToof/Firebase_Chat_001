package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.net.Uri
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface ChatDetailContract {

    interface View {

        fun showProgressDialog()

        fun hideProgressDialog()

        fun onCheckCurrentUserFail()

        fun onGetCurrentUserIdSuccess(userId: String)

        fun onFetchGroupInformationSuccess(group: Group)

        fun onFetchFail(error: Throwable)

        fun onMessageAdded(message: Message)

        fun onLeaveGroupSuccess()

        fun onUploadSuccess(uri: Uri)

        fun onUploadFail(error: Throwable)

        fun onFetchPreviousMessagesSuccess(messages: List<Message>)

        fun onFetchNextMessagesSuccess(messages: List<Message>)
    }

    interface Presenter : BasePresenter<View> {

        fun getCurrentUserId()

        fun fetchGroupInformation(groupId: String)

        fun fetchLastMessage(groupId: String, users: List<User>)

        fun fetchPreviousMessages(groupId: String, lastMessageId: String, users: List<User>)

        fun fetchNextMessages(groupId: String, firstMessageId: String, users: List<User>)

        fun sendMessage(group: Group, message: Message)

        fun leaveGroup(group: Group)

        fun uploadImage(uri: Uri)
    }
}
