package com.lobesoftware.toof.firebase_chat_001.screen.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.replaceFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.chat.ChatFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.friend.FriendFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleEvents()
    }

    private fun handleEvents() {
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_chat -> {
                    replaceFragment(R.id.frame_layout_container, ChatFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_friends -> {
                    replaceFragment(R.id.frame_layout_container, FriendFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    replaceFragment(R.id.frame_layout_container, ProfileFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }
}
