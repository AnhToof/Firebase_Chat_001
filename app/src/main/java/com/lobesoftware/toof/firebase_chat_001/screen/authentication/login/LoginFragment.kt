package com.lobesoftware.toof.firebase_chat_001.screen.authentication.login

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.register.RegisterFragment
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import kotlinx.android.synthetic.main.fragment_login.view.*
import javax.inject.Inject

class LoginFragment : Fragment(), LoginContract.View {

    @Inject
    internal lateinit var mValidator: Validator

    private lateinit var mPresenter: LoginPresenter

    private lateinit var mView: View

    override fun onAttach(context: Context?) {
        (activity?.application as MainApplication).mAppComponent.inject(this@LoginFragment)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_login, container, false)

        mPresenter = LoginPresenter()
        mPresenter.setValidator(mValidator)
        mPresenter.setView(this)

        mView.button_sign_in.setOnClickListener {
            mPresenter.login(
                mView.edit_email.text.toString(),
                mView.edit_password.text.toString()
            )
        }

        mView.button_sign_up.setOnClickListener {
            (activity as AuthenticationActivity).replaceFragment(
                R.id.constraint_layout_container,
                RegisterFragment(),
                true
            )
        }
        return mView
    }

    override fun onInputDataInValid(errorMessageValidate: ErrorMessageValidate) {
        mView.text_input_layout_email.error = errorMessageValidate.emailError
        mView.text_input_layout_email.isErrorEnabled = !errorMessageValidate.emailError.isNullOrBlank()
        mView.text_input_layout_password.error = errorMessageValidate.passwordError
        mView.text_input_layout_password.isErrorEnabled = !errorMessageValidate.passwordError.isNullOrBlank()
    }

    override fun onLoginSuccess() {
        TODO("Handle action login success") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLoginFail(error: String) {
        TODO("Handle action login failed") //To change body of created functions use File | Settings | File Templates.
    }
}
