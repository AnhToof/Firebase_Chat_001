package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
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
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import com.lobesoftware.toof.firebase_chat_001.utils.validator.Validator
import kotlinx.android.synthetic.main.fragment_create_group.view.*
import javax.inject.Inject

class CreateGroupFragment : Fragment(), CreateGroupContact.View, ItemRecyclerViewClickListener<User> {

    @Inject
    internal lateinit var mValidator: Validator
    @Inject
    internal lateinit var mUserRepository: UserRepository
    @Inject
    internal lateinit var mGroupRepository: GroupRepository
    private lateinit var mView: View
    private lateinit var mNavigator: CreateGroupNavigator
    private lateinit var mPresenter: CreateGroupPresenter
    private lateinit var mAdapter: CreateGroupAdapter
    private var mMembers = ArrayList<User>()
    private lateinit var mProgressDialog: ProgressDialog
    private var mScreenType: String? = null
    private var mGroup: Group? = null
    private var mGroupId: String? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@CreateGroupFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            mScreenType = args.getString(ARGUMENT_SCREEN_TYPE)
            mGroup = args.getParcelable(ARGUMENT_GROUP)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_create_group, container, false)
        mPresenter = CreateGroupPresenter(this, mUserRepository, mGroupRepository, mValidator)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constant.ResultCode.RESULT_OK && requestCode == Constant.RequestCode.REQUEST_CODE) {
            data?.let {
                mGroup = null
                mMembers = it.getParcelableArrayListExtra(EXTRA_ARRAY)
                mAdapter.setListMembers(mMembers)
            }
        }
    }

    override fun showProgressDialog() {
        mProgressDialog.show()
    }

    override fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    override fun onItemClick(view: View, item: User, position: Int) {
        mNavigator.goToUserDetailScreen(item)
    }

    override fun onCheckCurrentUserFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_session_expired), Toast.LENGTH_LONG)
        mNavigator.goToAuthenticationScreen()
    }

    override fun onFetchMembersSuccess(members: List<User>) {
        mAdapter.setListMembers(members)
        mMembers.addAll(mAdapter.getListMembers())
    }

    override fun onCreateGroupSuccess() {
        mNavigator.backToPreviousScreen()
    }

    override fun onCreateGroupFail(error: Throwable) {
        if (error == NullPointerException()) {
            (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
        } else {
            (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
        }
    }

    override fun onInputDataInValid(errorMessage: String?) {
        errorMessage?.let {
            (activity as? MainActivity)?.toast(it, Toast.LENGTH_LONG)
        }
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            mNavigator = CreateGroupNavigatorImpl(it)
        }
        setUpRecyclerView()
        when (mScreenType) {
            Constant.ScreenType.ADD -> {
                mView.toolbar.title = "${getString(R.string.add)} ${getString(R.string.group)}"
                setUpProgressDialog(getString(R.string.msg_creating_group))
                mView.button_add_group.text = getText(R.string.action_add_group)
            }
            else -> {
                mView.toolbar.title = "${getString(R.string.edit)} ${getString(R.string.group)}"
                setUpProgressDialog(getString(R.string.msg_updating_group))
                mView.button_add_group.text = getText(R.string.action_update_group)
                mGroup?.let { group ->
                    mView.edit_title.setText(group.title.toString())
                    mView.edit_description.setText(group.description.toString())
                    mGroupId = group.id
                    mPresenter.fetchMembers(group)
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        context?.let {
            mAdapter = CreateGroupAdapter(it)
            mAdapter.setItemRecyclerViewListener(this)
            mView.recycler_view_members.apply {
                val layout = LinearLayoutManager(it)
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mAdapter
            }
        }
        mAdapter.setListMembers(mMembers)
    }

    private fun handleEvents() {
        mView.image_add_members.setOnClickListener {
            mNavigator.goToAddMemberScreen(this, mMembers)
        }
        mView.button_add_group.setOnClickListener {
            val group = Group(
                id = mGroupId,
                type = true,
                title = mView.edit_title.text.toString(),
                description = mView.edit_description.text.toString()
            )
            mMembers.forEach { user ->
                user.id?.let { id ->
                    group.members[id] = true
                }
            }
            if (mScreenType == Constant.ScreenType.ADD) {
                mPresenter.createGroup(group)
            } else {
                mPresenter.updateGroup(group)
            }
        }
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToPreviousScreen()
        }
    }

    private fun setUpProgressDialog(message: String) {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(message)
    }

    companion object {
        private const val ARGUMENT_SCREEN_TYPE = "screen_type"
        private const val ARGUMENT_GROUP = "group"
        const val EXTRA_ARRAY = "members_id"

        fun getInstance(screenType: String, group: Group?): CreateGroupFragment {
            val args = Bundle()
            args.putString(ARGUMENT_SCREEN_TYPE, screenType)
            args.putParcelable(ARGUMENT_GROUP, group)
            val fragment = CreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
