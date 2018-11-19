package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import kotlinx.android.synthetic.main.fragment_login.view.*
import javax.inject.Inject

class LoginFragment : Fragment(), LoginContract.View {

    @Inject
    internal lateinit var mValidator: Validator
    @Inject
    internal lateinit var mUserRepository: UserRepositoryImpl
    private lateinit var mPresenter: LoginPresenter
    private lateinit var mView: View
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mNavigator: LoginNavigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@LoginFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_login, container, false)

        setUpProgressDialog()

        mPresenter = LoginPresenter()
        mPresenter.apply {
            setValidator(mValidator)
            setUserRepository(mUserRepository)
            setView(this@LoginFragment)
        }

        if (activity is AuthenticationActivity) {
            mNavigator = LoginNavigatorImpl(activity as AppCompatActivity)
        }

        handleEvents()

        return mView
    }

    override fun showProgressDialog() {
        mProgressDialog.show()
    }

    override fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    override fun onInputDataInValid(errorMessageValidate: ErrorMessageValidate) {
        mView.text_input_layout_email.error = errorMessageValidate.emailError
        mView.text_input_layout_email.isErrorEnabled = !errorMessageValidate.emailError.isNullOrBlank()
        mView.text_input_layout_password.error = errorMessageValidate.passwordError
        mView.text_input_layout_password.isErrorEnabled = !errorMessageValidate.passwordError.isNullOrBlank()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

    override fun onLoginSuccess() {
        mNavigator.goToMainScreen()
    }

    override fun onLoginFail(error: String) {
        if (activity is AuthenticationActivity) {
            (activity as AuthenticationActivity).toast(error) //Need cast to call toast
        }
    }

    private fun handleEvents() {
        mView.button_sign_in.setOnClickListener {
            mPresenter.login(
                mView.edit_email.text.toString(),
                mView.edit_password.text.toString()
            )
        }

        mView.button_sign_up.setOnClickListener {
            mNavigator.goToRegisterScreen()
        }
    }

    private fun setUpProgressDialog() {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(getString(R.string.msg_logging_in))
    }
}
