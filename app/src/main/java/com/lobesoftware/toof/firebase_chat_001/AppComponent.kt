package com.lobesoftware.toof.firebase_chat_001

import android.content.Context
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.login.LoginFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface AppComponent {

    fun applicationContext(): Context

    fun inject(authenticationActivity: AuthenticationActivity)

    fun inject(loginFragment: LoginFragment)
}
