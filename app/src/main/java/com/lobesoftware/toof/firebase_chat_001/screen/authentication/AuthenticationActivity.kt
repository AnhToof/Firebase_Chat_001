package com.lobesoftware.toof.firebase_chat_001.screen.authentication

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.login.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        //TODO:Add function that check current user, if current user == null then show login, otherwise skip this activity
        replaceFragment(R.id.constraint_layout_container, LoginFragment())
    }
}
