package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepositoryImpl
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_search_box.view.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import javax.inject.Inject

class ChatFragment : Fragment(), ChatContract.View, ItemRecyclerViewClickListener<Group> {

    @Inject
    internal lateinit var mUserRepository: UserRepositoryImpl
    private lateinit var mPresenter: ChatPresenter
    private lateinit var mAdapter: ChatAdapter
    private lateinit var mView: View
    private lateinit var mNavigator: ChatNavigator
    private val mConversations = ArrayList<Group>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@ChatFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_chat, container, false)

        setHasOptionsMenu(true)
        setUpConversationRecyclerView()
        mPresenter = ChatPresenter()
        mPresenter.apply {
            setView(this@ChatFragment)
            setUserRepository(mUserRepository)
        }
        (activity as? MainActivity)?.let {
            mNavigator = ChatNavigatorImpl(it)
        }
        setUpData()
        handleEvents()
        return mView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main_action_bar, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add) {
            mNavigator.goToCreateGroupScreen(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        mPresenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onItemClick(view: View, item: Group, position: Int) {
        TODO("Open conversation")
    }

    override fun onCheckCurrentUserFail() {
        mNavigator.goToAuthenticationScreen()
    }

    override fun onFilterConversationSuccess(conversations: List<Group>) {
        mAdapter.setListConversations(conversations as ArrayList<Group>, true)
    }

    override fun onConversationAdded(group: Group) {
        mConversations.add(group)
        mAdapter.addConversation(group, mConversations.size)
    }

    override fun onConversationChanged(group: Group) {
        for ((index, oldGroup) in mConversations.withIndex()) {
            if (oldGroup.id == group.id) {
                mConversations[index] = group
                mAdapter.changeConversation(group, index)
                break
            }
        }
    }

    override fun onConversationRemoved(group: Group) {
        for ((index, oldGroup) in mConversations.withIndex()) {
            if (oldGroup.id == group.id) {
                mConversations.remove(oldGroup)
                mAdapter.removeConversation(index)
                break
            }
        }
    }

    override fun onFetchFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
    }

    private fun setUpData() {
        mPresenter.fetchConversations()
    }

    private fun handleEvents() {
        mView.edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    mAdapter.setListConversations(mConversations, false)
                }
                mPresenter.filterConversation(s.toString(), mConversations)
            }
        })
    }

    private fun setUpConversationRecyclerView() {
        context?.let {
            mAdapter = ChatAdapter(it)
            mAdapter.setItemRecyclerViewListener(this)
            mView.recycler_view_conversations.apply {
                val layout = LinearLayoutManager(it)
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mAdapter
            }
        }
    }
}
