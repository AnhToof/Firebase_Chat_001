package com.lobesoftware.toof.firebase_chat_001.screen.main.add_friend

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import kotlinx.android.synthetic.main.custom_search_box.view.*
import kotlinx.android.synthetic.main.fragment_add_friend.view.*
import javax.inject.Inject

class AddFriendFragment : Fragment(), AddFriendContact.View {

    @Inject
    internal lateinit var mUserRepository: UserRepository
    private lateinit var mView: View
    private lateinit var mPresenter: AddFriendPresenter
    private lateinit var mNavigator: AddFriendNavigator
    private var mUser: User? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@AddFriendFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_add_friend, container, false)
        mPresenter = AddFriendPresenter(this, mUserRepository)
        initViews()
        handleEvents()
        return mView
    }

    override fun onStop() {
        mPresenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onCheckCurrentUserFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_session_expired), Toast.LENGTH_LONG)
        mNavigator.goToAuthenticationScreen()
    }

    override fun onRequestFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
    }

    override fun onRequestSuccess() {
        (activity as? MainActivity)?.toast(getString(R.string.action_sent), Toast.LENGTH_LONG)
        mView.button_add_friend.apply {
            text = getString(R.string.action_sent)
            setBackgroundColor(Color.GRAY)
            isEnabled = false
        }
    }

    override fun onSearchUserFail(error: Throwable) {
        if (error is NullPointerException) {
            (activity as? MainActivity)?.toast(getString(R.string.email_does_not_exist), Toast.LENGTH_LONG)
        } else {
            (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
        }
    }

    override fun onSearchUserSuccess(user: User, action: String?) {
        mUser = user
        mView.button_add_friend.apply {
            action?.let { action ->
                when (action) {
                    Constant.ACTION_SENT -> {
                        mView.constraint_layout_friend_search.visibility = View.VISIBLE
                        text = getString(R.string.action_sent)
                        setBackgroundColor(Color.GRAY)
                        isEnabled = false
                    }
                    Constant.ACTION_RECEIVED -> {
                        (activity as? MainActivity)?.toast(
                            getString(R.string.msg_have_a_request_from_user),
                            Toast.LENGTH_LONG
                        )
                    }
                    Constant.ACTION_FRIEND -> {
                        (activity as? MainActivity)?.toast(
                            getString(R.string.msg_already_friend),
                            Toast.LENGTH_LONG
                        )
                    }
                    else -> {
                        mView.constraint_layout_friend_search.visibility = View.VISIBLE
                        text = getString(R.string.action_add)
                        setBackgroundColor(Color.BLUE)
                        isEnabled = true
                    }
                }
            }
        }
        mView.image_avatar.loadUrlWithCircleCropTransform(null)
        mView.text_full_name.text = user.fullName
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            mNavigator = AddFriendNavigatorImpl(it)
        }
        mView.toolbar.title =
                "${getString(R.string.add)} ${activity?.getString(R.string.friend)}"
    }

    private fun handleEvents() {
        mView.image_search.setOnClickListener {
            mView.constraint_layout_friend_search.visibility = View.GONE
            if (mView.edit_search.text.trim().toString().toLowerCase().isNotEmpty()) {
                mPresenter.searchUserByEmail(mView.edit_search.text.toString().toLowerCase())
            }
        }
        mView.button_add_friend.setOnClickListener {
            mUser?.id?.let { friendId ->
                mPresenter.requestFriend(friendId)
            }
        }
        mView.constraint_layout_friend_search.setOnClickListener {
            mUser?.let { user ->
                mNavigator.goToUserDetailScreen(user)
            }
        }
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToFriendScreen()
        }
    }

    companion object {
        fun getInstance(): AddFriendFragment {
            return AddFriendFragment()
        }
    }
}
