package com.lobesoftware.toof.firebase_chat_001.extension

import android.content.Intent
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.utils.Constant

fun AppCompatActivity.startActivity(intent: Intent, flags: Int? = null, removeItself: Boolean = false) {
    flags?.let { intent.flags = it }
    startActivity(intent)
    if (removeItself) {
        finish()
    }
}

fun AppCompatActivity.replaceFragment(
    @IdRes containerId: Int, fragment: Fragment,
    addToBackStack: Boolean = false,
    tag: String = fragment::class.java.simpleName,
    currentFragment: Fragment? = null
) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    if (addToBackStack) {
        transaction.addToBackStack(tag)
    }
    currentFragment?.let {
        fragment.setTargetFragment(currentFragment, Constant.RequestCode.REQUEST_CODE)
    }
    transaction.add(containerId, fragment, tag)
    transaction.commitAllowingStateLoss()
    fragmentManager.executePendingTransactions()
}

fun AppCompatActivity.toast(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}
