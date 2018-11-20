package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.utils.BasePresenter

interface LoginContract {

    interface View {

        fun onInputDataInValid(errorMessageValidate: ErrorMessageValidate)

        fun onLoginSuccess()

        fun onLoginFail(error: String)

        fun showProgressDialog()

        fun hideProgressDialog()
    }

    interface Presenter : BasePresenter<View> {

        fun login(email: String, password: String)
    }
}
