package com.lobesoftware.toof.firebase_chat_001.screen.main.user_detail

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.GroupRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.fragment_user_detail.view.*
import javax.inject.Inject

class UserDetailFragment : Fragment(), UserDetailContract.View {

    @Inject
    internal lateinit var mUserRepository: UserRepository
    @Inject
    internal lateinit var mGroupRepository: GroupRepository
    private lateinit var mNavigator: UserDetailNavigator
    private lateinit var mPresenter: UserDetailPresenter
    private lateinit var mView: View
    private var mUser: User? = null
    private var mFriendState: Int? = null

    enum class FriendState(val value: Int) {
        ADD_FRIEND(0),
        ACCEPT_FRIEND(1)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@UserDetailFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_user_detail, container, false)
        mPresenter = UserDetailPresenter(this, mUserRepository, mGroupRepository)
        setUpData()
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

    override fun onAlreadyFriend() {
        mView.constraint_layout_friend.visibility = View.VISIBLE
        mView.button_add_friend.visibility = View.GONE
    }

    override fun onRequestedFriend() {
        mView.constraint_layout_friend.visibility = View.GONE
        mView.button_add_friend.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.button_sent_request)
            isEnabled = false
        }
    }

    override fun onNotFriend() {
        mView.constraint_layout_friend.visibility = View.GONE
        mView.button_add_friend.visibility = View.VISIBLE
        mFriendState = FriendState.ADD_FRIEND.value
    }

    override fun onReceivedFriendRequest() {
        mView.constraint_layout_friend.visibility = View.GONE
        mView.button_add_friend.apply {
            visibility = View.VISIBLE
            text = context.getString(R.string.button_accept_friend)
        }
        mFriendState = FriendState.ACCEPT_FRIEND.value
    }

    override fun onFetchFail(error: Throwable) {
        if (error == NullPointerException()) {
            (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
        } else {
            (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
        }
    }

    override fun onFetchGroupWithFriendSuccess(group: Group) {
        mNavigator.goToConversationDetail(group)
    }

    private fun setUpData() {
        arguments?.let { args ->
            mUser = args.getParcelable(ARGUMENT_USER)
        }
    }

    private fun initViews() {
        mUser?.let { user ->
            mView.text_full_name.text = user.fullName
            mView.text_email.text = user.email
            mView.text_phone.text = user.phone
            mPresenter.fetchFriendState(user)
        }
        (activity as? MainActivity)?.let {
            mNavigator = UserDetailNavigatorImpl(it)
        }
    }

    private fun handleEvents() {
        mView.button_conversation.setOnClickListener {
            mUser?.let { user ->
                mPresenter.fetchGroupWithFriendInformation(user)
            }
        }
        mView.button_un_friend.setOnClickListener {
            mUser?.id?.let { id ->
                mPresenter.unFriend(id)
            }
        }
        mView.button_add_friend.setOnClickListener {
            mUser?.let { user ->
                if (mFriendState == FriendState.ACCEPT_FRIEND.value) {
                    mPresenter.acceptFriend(user)
                } else {
                    user.id?.let { id ->
                        mPresenter.addFriend(id)
                    }
                }
            }
        }
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToPreviousScreen()
        }
    }

    companion object {
        private const val ARGUMENT_USER = "user"

        fun getInstance(user: User): UserDetailFragment {
            val args = Bundle()
            args.putParcelable(ARGUMENT_USER, user)
            val fragment = UserDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
