package com.lobesoftware.toof.firebase_chat_001.screen.main.profile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.fragment_profile.view.*
import javax.inject.Inject

class ProfileFragment : Fragment(), ProfileContract.View {

    @Inject
    internal lateinit var mUserRepository: UserRepository
    private lateinit var mPresenter: ProfilePresenter
    private lateinit var mView: View
    private lateinit var mNavigator: ProfileNavigator

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@ProfileFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_profile, container, false)
        mPresenter = ProfilePresenter(this, mUserRepository)
        (activity as? MainActivity)?.let {
            mNavigator = ProfileNavigatorImpl(it)
        }
        handleData()
        handleEvents()
        return mView
    }

    override fun onStop() {
        mPresenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onFetchFail(error: Throwable) {
        (activity as? MainActivity)?.toast(error.localizedMessage)
    }

    override fun onFetchInformationSuccess(user: User) {
        mView.text_email.text = user.email
        mView.text_full_name.text = user.fullName
        user.phone?.let {
            mView.text_phone.text = it
        }
    }

    override fun onSignOutSuccess() {
        mNavigator.goTogoToAuthenticationScreen()
    }

    private fun handleEvents() {
        mView.button_logout.setOnClickListener {
            mPresenter.signOut()
        }
    }

    private fun handleData() {
        mPresenter.fetchInformation()
    }

    companion object {
        fun getInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}
