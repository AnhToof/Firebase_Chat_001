package com.lobesoftware.toof.firebase_chat_001.repositories

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        return UserRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideGroupRepository(): GroupRepository {
        return GroupRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideMessageRepository(): MessageRepository {
        return MessageRepositoryImpl()
    }
}
