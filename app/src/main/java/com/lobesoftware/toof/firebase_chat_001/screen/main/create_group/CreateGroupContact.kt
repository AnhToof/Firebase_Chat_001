package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface CreateGroupContact {

    interface View {

        fun showProgressDialog()

        fun hideProgressDialog()

        fun onCheckCurrentUserFail()

        fun onCreateGroupSuccess()

        fun onCreateGroupFail()

        fun onInputDataInValid(errorMessage: String?)
    }

    interface Presenter : BasePresenter<View> {

        fun createGroup(group: Group)
    }
}
