package com.lobesoftware.toof.firebase_chat_001.screen.authentication.register

import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RegisterPresenter : RegisterContract.Presenter {

    private var mView: RegisterContract.View? = null
    private lateinit var mValidator: Validator
    private lateinit var mUserRepository: UserRepositoryImpl
    private val mCompositeDisposable = CompositeDisposable()

    override fun setView(view: RegisterContract.View) {
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

    override fun register(user: User, password: String) {
        mView?.let { view ->
            val errorMessageValidate = validate(user.email ?: "", password, user.fullName ?: "")
            errorMessageValidate?.let {
                view.onInputDataInValid(errorMessageValidate)
                return
            }
            view.showProgressDialog()
            val disposable = mUserRepository.register(user, password)
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate {
                    view.hideProgressDialog()
                }
                .subscribe(
                    { view.onRegisterSuccess() },
                    { error -> view.onRegisterFail(error.localizedMessage) }
                )
            mCompositeDisposable.add(disposable)
        }
    }

    fun setValidator(validator: Validator) {
        mValidator = validator
    }

    fun setUserRepository(userRepository: UserRepositoryImpl) {
        mUserRepository = userRepository
    }

    private fun validate(email: String, password: String, fullName: String): ErrorMessageValidate? {
        val inputEmailError = mValidator.validateEmail(email)
        val inputPasswordError = mValidator.validatePassword(password)
        val inputNameError = mValidator.validateName(fullName)
        if (inputEmailError != null || inputPasswordError != null)
            return ErrorMessageValidate(inputEmailError, inputPasswordError, inputNameError)
        return null
    }
}

data class ErrorMessageValidate(val emailError: String?, val passwordError: String?, val fullNameError: String?)
