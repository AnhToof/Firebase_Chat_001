package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_search_box.view.*
import kotlinx.android.synthetic.main.fragment_friend.view.*
import javax.inject.Inject

class FriendFragment : Fragment(), FriendContract.View, ItemRecyclerViewClickListener<User> {

    @Inject
    internal lateinit var mUserRepository: UserRepositoryImpl
    private lateinit var mPresenter: FriendPresenter
    private lateinit var mView: View
    private lateinit var mFriendAdapter: FriendAdapter
    private lateinit var mFriendRequestAdapter: FriendRequestAdapter
    private lateinit var mNavigator: FriendNavigator
    private var mFriends = ArrayList<User>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@FriendFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_friend, container, false)

        setHasOptionsMenu(true)

        initViews()

        mPresenter = FriendPresenter()
        mPresenter.apply {
            setView(this@FriendFragment)
            setUserRepository(mUserRepository)
        }

        if (activity is MainActivity) {
            mNavigator = FriendNavigatorImpl(activity as AppCompatActivity)
        }

        setUpData()
        handleEvents()
        return mView
    }

    override fun onItemClick(view: View, item: User, position: Int) {
        when (view.id) {
            R.id.button_accept -> {
                mPresenter.acceptFriend(item)
            }
            R.id.button_reject -> {
                mPresenter.rejectFriend(item)
            }
            else -> {
                TODO("Open user detail")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_action_bar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            TODO("Add function: add friend")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onFetchFriendRequestSuccess(user: User) {
        mFriendRequestAdapter.updateData(user)
    }

    override fun onFriendAdded(user: User) {
        mFriends.add(user)
        mFriendAdapter.addFriend(user, mFriends.size)
    }

    override fun onFriendChanged(user: User) {
        for ((index, oldUser) in mFriends.withIndex()) {
            if (oldUser.id == user.id) {
                mFriends[index] = user
                mFriendAdapter.changeFriend(user, index)
                break
            }
        }
    }

    override fun onFriendRemoved(user: User) {
        for ((index, oldUser) in mFriends.withIndex()) {
            if (oldUser.id == user.id) {
                mFriends.remove(oldUser)
                mFriendAdapter.removeFriend(user, index)
                break
            }
        }
    }

    override fun onCheckCurrentUserFail() {
        mNavigator.goToAuthenticationScreen()
    }

    override fun onFilterFriendSuccess(users: List<User>) {
        mFriendAdapter.setListFriends(users as ArrayList<User>, true)
    }

    private fun initViews() {
        setUpFriendRecyclerView()
        setUpFriendRequestRecyclerView()
    }

    private fun setUpData() {
        mPresenter.fetchFriend()
        mPresenter.fetchFriendRequest()
    }

    private fun handleEvents() {
        mView.edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    mView.constraint_layout_request.visibility = View.VISIBLE
                    mFriendAdapter.setListFriends(mFriends, false)
                } else {
                    mView.constraint_layout_request.visibility = View.GONE
                }
                mPresenter.filterFriend(s.toString(), mFriends)
            }
        })
    }

    private fun setUpFriendRecyclerView() {
        context?.let {
            mFriendAdapter = FriendAdapter(it)
            mFriendAdapter.setItemRecyclerViewListener(this)
            mView.recycler_view_friends.apply {
                val layout = LinearLayoutManager(it)
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mFriendAdapter
            }
        }
    }

    private fun setUpFriendRequestRecyclerView() {
        context?.let {
            mFriendRequestAdapter = FriendRequestAdapter(it)
            mFriendRequestAdapter.setItemRecyclerViewListener(this)
            mView.recycler_view_request.apply {
                val layout = LinearLayoutManager(it)
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mFriendRequestAdapter
            }
        }
    }
}
