package com.lobesoftware.toof.firebase_chat_001

import android.content.Context
import com.lobesoftware.toof.firebase_chat_001.repositories.RepositoryModule
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.AuthenticationActivity
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.login.LoginFragment
import com.lobesoftware.toof.firebase_chat_001.screen.authentication.register.RegisterFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.profile.ProfileFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, RepositoryModule::class])
interface AppComponent {

    fun applicationContext(): Context

    fun inject(authenticationActivity: AuthenticationActivity)

    fun inject(loginFragment: LoginFragment)

    fun inject(registerFragment: RegisterFragment)

    fun inject(profileFragment: ProfileFragment)
}
