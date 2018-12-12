package com.lobesoftware.toof.firebase_chat_001.screen.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.screen.main.chat.ChatFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.friend.FriendFragment
import com.lobesoftware.toof.firebase_chat_001.screen.main.profile.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    enum class Page(val id: Int, val title: String?) {
        PAGE_CHAT(0, "Chat"),
        PAGE_FRIEND(1, "Friend"),
        PAGE_PROFILE(2, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        title = Page.PAGE_CHAT.title
        setUpViewPager()
        handleEvents()
    }

    private fun setUpViewPager() {
        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(ChatFragment())
        viewPagerAdapter.addFragment(FriendFragment())
        viewPagerAdapter.addFragment(ProfileFragment())
        viewpager.apply {
            adapter = viewPagerAdapter
            offscreenPageLimit = 3
        }
    }

    private fun handleEvents() {
        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_chat -> {
                    supportActionBar?.show()
                    toolbar.title = Page.PAGE_CHAT.title
                    viewpager.currentItem = Page.PAGE_CHAT.id
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_friends -> {
                    supportActionBar?.show()
                    toolbar.title = Page.PAGE_FRIEND.title
                    viewpager.currentItem = Page.PAGE_FRIEND.id
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    supportActionBar?.hide()
                    viewpager.currentItem = Page.PAGE_PROFILE.id
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    companion object {
        fun getInstance(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
