package com.lobesoftware.toof.firebase_chat_001

import android.content.Context
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideValidator(): Validator {
        return Validator(context)
    }
}
