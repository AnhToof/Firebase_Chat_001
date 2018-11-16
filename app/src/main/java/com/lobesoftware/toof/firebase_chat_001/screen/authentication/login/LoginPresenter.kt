package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator

class LoginPresenter : LoginContract.Presenter {

    private lateinit var mView: LoginContract.View
    private lateinit var mValidator: Validator

    fun setValidator(validator: Validator) {
        mValidator = validator
    }

    override fun setView(view: LoginContract.View) {
        mView = view
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    override fun login(email: String, password: String) {
        val errorMessageValidate = validate(email, password)
        if (errorMessageValidate != null) {
            mView.onInputDataInValid(errorMessageValidate)
        }

        //TODO("Login function") //To change body of created functions use File | Settings | File Templates.
    }

    private fun validate(email: String, password: String): ErrorMessageValidate? {
        val inputEmailError = mValidator.validateEmail(email)
        val inputPasswordError = mValidator.validatePassword(password)
        if (inputEmailError != null || inputPasswordError != null)
            return ErrorMessageValidate(inputEmailError, inputPasswordError)
        return null
    }
}

data class ErrorMessageValidate(val emailError: String?, val passwordError: String?)
