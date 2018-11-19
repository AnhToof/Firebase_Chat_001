package com.lobesoftware.toof.firebase_chat_001.screen.main.profile

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface ProfileContract {

    interface View {

        fun onFetchInformationSuccess(user: User)

        fun onSignOutSuccess()
    }

    interface Presenter : BasePresenter<View> {

        fun fetchInformation()

        fun signOut()
    }
}
