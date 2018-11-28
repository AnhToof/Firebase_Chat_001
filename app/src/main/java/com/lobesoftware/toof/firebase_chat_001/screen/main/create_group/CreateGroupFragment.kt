package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.lobesoftware.toof.firebase_chat_001.MainApplication
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
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
    private lateinit var mView: View
    private lateinit var mNavigator: CreateGroupNavigator
    private lateinit var mPresenter: CreateGroupPresenter
    private lateinit var mAdapter: CreateGroupAdapter
    private var mMembers = ArrayList<User>()
    private lateinit var mProgressDialog: ProgressDialog

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@CreateGroupFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_create_group, container, false)
        mPresenter = CreateGroupPresenter()
        mPresenter.apply {
            setView(this@CreateGroupFragment)
            setUserRepository(mUserRepository)
            setValidator(mValidator)
        }
        initViews()
        handleEvents()
        return mView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_add)
        item.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onStop() {
        mPresenter.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onDetach() {
        mNavigator.backToChatScreen()
        super.onDetach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mNavigator.backToChatScreen()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constant.ResultCode.RESULT_OK && requestCode == Constant.RequestCode.REQUEST_CODE) {
            data?.let {
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
        TODO("OPEN USER DETAIL")
    }

    override fun onCheckCurrentUserFail() {
        (activity as? MainActivity)?.let {
            it.toast(it.getString(R.string.msg_session_expired), Toast.LENGTH_LONG)
        }
        mNavigator.goToAuthenticationScreen()
    }

    override fun onCreateGroupSuccess() {
        mNavigator.backToChatScreen()
    }

    override fun onCreateGroupFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
    }

    override fun onInputDataInValid(errorMessage: String?) {
        errorMessage?.let {
            (activity as? MainActivity)?.toast(it, Toast.LENGTH_LONG)
        }
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            arguments?.let { args ->
                it.supportActionBar?.title =
                        "${args[ARGUMENT_TITLE]} ${activity?.getString(R.string.group)}"
            }
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mNavigator = CreateGroupNavigatorImpl(it)
        }
        setUpProgressDialog()
        setUpRecyclerView()
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
                type = true,
                title = mView.edit_title.text.toString(),
                description = mView.edit_description.text.toString()
            )
            mMembers.forEach { user ->
                user.id?.let { id ->
                    group.members[id] = true
                }
            }
            mPresenter.createGroup(group)
        }
    }

    private fun setUpProgressDialog() {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(getString(R.string.msg_creating_group))
    }

    companion object {
        private const val ARGUMENT_TITLE = "title"
        const val EXTRA_ARRAY = "members_id"

        fun getInstance(title: String): CreateGroupFragment {
            val args = Bundle()
            args.putString(ARGUMENT_TITLE, title)
            val fragment = CreateGroupFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
