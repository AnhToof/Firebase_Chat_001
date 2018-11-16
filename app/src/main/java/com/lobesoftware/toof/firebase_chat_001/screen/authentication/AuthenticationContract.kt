package com.lobesoftware.toof.firebase_chat_001.screen.authentication

import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface AuthenticationContract {

    interface View {

        fun onLogged()

        fun onUnLog()

        fun showDialog()

        fun hideDialog()
    }

    interface Presenter : BasePresenter<View> {

        fun checkLoggedState()
    }
}
