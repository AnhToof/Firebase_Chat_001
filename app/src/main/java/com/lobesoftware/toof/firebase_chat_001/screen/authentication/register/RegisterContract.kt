package com.lobesoftware.toof.firebase_chat_001.screen.authentication.register

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface RegisterContract {

    interface View {

        fun onInputDataInValid(errorMessageValidate: ErrorMessageValidate)

        fun onRegisterSuccess()

        fun onRegisterFail(error: String)

        fun showProgressDialog()

        fun hideProgressDialog()
    }

    interface Presenter : BasePresenter<View> {

        fun register(user: User, password: String)
    }
}
