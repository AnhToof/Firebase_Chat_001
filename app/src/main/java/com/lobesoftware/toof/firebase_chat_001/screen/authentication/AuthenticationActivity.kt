package com.lobesoftware.toof.firebase_chat_001.screen.authentication

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.extension.startActivity
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.login.LoginFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.activity_authentication.*
import javax.inject.Inject

class AuthenticationActivity : AppCompatActivity(), AuthenticationContract.View {

    @Inject
    internal lateinit var mUserRepository: UserRepositoryImpl
    private lateinit var mPresenter: AuthenticationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        if (application is MainApplication) {
            (application as MainApplication).mAppComponent.inject(this)
        }

        mPresenter = AuthenticationPresenter()
        mPresenter.apply {
            setUserRepository(mUserRepository)
            setView(this@AuthenticationActivity)
            checkLoggedState()
        }
    }

    override fun onLogged() {
        goToMainScreen()
    }

    override fun onUnLog() {
        replaceFragment(R.id.constraint_layout_container, LoginFragment())
    }

    override fun showDialog() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun hideDialog() {
        progress_bar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }

    private fun goToMainScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent, removeItself = true)
    }
}
