package com.lobesoftware.toof.firebase_chat_001.extension

import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.startActivity(intent: Intent, flags: Int? = null) {
    flags?.let { intent.flags = it }
    startActivity(intent)
}

fun AppCompatActivity.replaceFragment(
    @IdRes containerId: Int, fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String = fragment::class.java.simpleName
) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    if (addToBackStack) {
        transaction.addToBackStack(tag)
    }
    transaction.replace(containerId, fragment, tag)
    transaction.commitAllowingStateLoss()
    fragmentManager.executePendingTransactions()
}
