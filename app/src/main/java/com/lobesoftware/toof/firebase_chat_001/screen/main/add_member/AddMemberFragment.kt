package com.lobesoftware.toof.firebase_chat_001.screen.main.add_member

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.custom_search_box.view.*
import kotlinx.android.synthetic.main.fragment_add_member.view.*
import javax.inject.Inject

class AddMemberFragment : Fragment(), AddMemberContract.View, ItemRecyclerViewClickListener<User> {

    @Inject
    internal lateinit var mUserRepository: UserRepository
    private lateinit var mView: View
    private lateinit var mNavigator: AddMemberNavigator
    private lateinit var mPresenter: AddMemberPresenter
    private lateinit var mAdapter: AddMemberAdapter
    private val mMembers = ArrayList<User>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@AddMemberFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_add_member, container, false)
        mPresenter = AddMemberPresenter(this, mUserRepository)
        initViews()
        setUpData()
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

    override fun onMemberAdded(user: User) {
        mMembers.add(user)
        mAdapter.addMember(user, mMembers.size)
    }

    override fun onMemberChanged(user: User) {
        for ((index, oldMember) in mMembers.withIndex()) {
            if (oldMember.id == user.id) {
                mMembers[index] = user
                mAdapter.changeMember(user, index)
                break
            }
        }
    }

    override fun onMemberRemoved(user: User) {
        for ((index, oldMember) in mMembers.withIndex()) {
            if (oldMember.id == user.id) {
                mMembers.removeAt(index)
                mAdapter.removeMember(index)
                break
            }
        }
    }

    override fun onFilterMemberSuccess(membersFiltered: List<User>) {
        mAdapter.setListMembers(membersFiltered)
    }

    override fun onFetchFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
    }

    override fun onItemClick(view: View, item: User, position: Int) {
        TODO("OPEN USER DETAIL")
    }

    private fun initViews() {
        setUpConversationRecyclerView()
        (activity as? MainActivity)?.let {
            mNavigator = AddMemberNavigatorImpl(activity as AppCompatActivity)
        }
        mView.toolbar.title =
                "${getString(R.string.add)} ${activity?.getString(R.string.member)}"
    }

    private fun setUpConversationRecyclerView() {
        context?.let {
            mAdapter = AddMemberAdapter(it)
            mAdapter.setItemRecyclerViewListener(this)
            mView.recycler_view_members.apply {
                val layout = LinearLayoutManager(it)
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mAdapter
            }
        }
    }

    private fun setUpData() {
        mPresenter.fetchMembers()
        arguments?.let { args ->
            args.getParcelableArrayList<User>(ARGUMENT_MEMBERS)?.let {
                mAdapter.setUsersSelected(it)
            }
        }
    }

    private fun handleEvents() {
        mView.button_add_member.setOnClickListener {
            mNavigator.backToAddGroupScreenWithValue(this, mAdapter.getUsersSelected())
        }
        mView.edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isEmpty()) {
                    mAdapter.setListMembers(mMembers)
                }
                mPresenter.filterMembers(s.toString(), mMembers)
            }
        })
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToAddGroupScreen()
        }
    }

    companion object {
        private const val ARGUMENT_MEMBERS = "members"

        fun getInstance(members: ArrayList<User>): AddMemberFragment {
            val args = Bundle()
            args.putParcelableArrayList(ARGUMENT_MEMBERS, members)
            val fragment = AddMemberFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
