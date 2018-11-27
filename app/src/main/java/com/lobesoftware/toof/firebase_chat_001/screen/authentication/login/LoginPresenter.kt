package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginPresenter : LoginContract.Presenter {

    private var mView: LoginContract.View? = null
    private lateinit var mValidator: Validator
    private lateinit var mUserRepository: UserRepository
    private val mCompositeDisposable = CompositeDisposable()

    override fun setView(view: LoginContract.View) {
        mView = view
    }

    override fun onStart() {
    }

    override fun onStop() {
        mCompositeDisposable.clear()
    }

    override fun onDestroy() {
        mView = null
    }

    override fun login(email: String, password: String) {
        mView?.let { view ->
            val errorMessageValidate = validate(email, password)
            errorMessageValidate?.let {
                view.onInputDataInValid(errorMessageValidate)
                return
            }

            view.showProgressDialog()

            val disposable = mUserRepository.loginWithEmailAndPassword(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    view.hideProgressDialog()
                }
                .subscribe({
                    view.onLoginSuccess()
                }, { error ->
                    view.onLoginFail(error.localizedMessage)
                })
            mCompositeDisposable.add(disposable)
        }
    }

    fun setValidator(validator: Validator) {
        mValidator = validator
    }

    fun setUserRepository(userRepository: UserRepository) {
        mUserRepository = userRepository
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
