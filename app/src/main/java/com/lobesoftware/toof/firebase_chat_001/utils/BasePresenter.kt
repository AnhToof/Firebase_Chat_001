package com.lobesoftware.toof.firebase_chat_001.utils

interface BasePresenter<T> {

    fun setView(view: T)

    fun onStart()

    fun onStop()

    fun onDestroy()
}
