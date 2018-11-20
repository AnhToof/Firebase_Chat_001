package com.lobesoftware.toof.firebase_chat_001.screen.authentication.register

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
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import kotlinx.android.synthetic.main.fragment_register.view.*
import javax.inject.Inject

class RegisterFragment : Fragment(), RegisterContract.View {

    @Inject
    internal lateinit var mValidator: Validator
    @Inject
    internal lateinit var mUserRepository: UserRepositoryImpl
    private lateinit var mPresenter: RegisterPresenter
    private lateinit var mView: View
    private lateinit var mProgressDialog: ProgressDialog
    private lateinit var mNavigator: RegisterNavigator

    companion object {
        fun getInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@RegisterFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_register, container, false)

        setUpProgressDialog()

        mPresenter = RegisterPresenter()
        mPresenter.apply {
            setValidator(mValidator)
            setUserRepository(mUserRepository)
            setView(this@RegisterFragment)
        }

        if (activity is AuthenticationActivity) {
            mNavigator = RegisterNavigatorImpl(activity as AppCompatActivity)
        }

        handleEvents()

        return mView
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
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
        mView.text_input_layout_full_name.error = errorMessageValidate.fullNameError
        mView.text_input_layout_full_name.isErrorEnabled = !errorMessageValidate.fullNameError.isNullOrBlank()
    }

    override fun onRegisterSuccess() {
        if (activity is AuthenticationActivity) {
            mNavigator.backToLoginScreen()
        }
    }

    override fun onRegisterFail(error: String) {
        if (activity is AuthenticationActivity) {
            (activity as AuthenticationActivity).toast(error)
        }
    }

    private fun setUpProgressDialog() {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(getString(R.string.msg_registering))
    }

    private fun handleEvents() {
        mView.button_sign_up.setOnClickListener {
            val user = User(
                email = mView.edit_email.text.toString(),
                fullName = mView.edit_full_name.text.toString(),
                phone = mView.edit_phone.text.toString()
            )
            mPresenter.register(
                user,
                mView.edit_password.text.toString()
            )
        }
    }
}
