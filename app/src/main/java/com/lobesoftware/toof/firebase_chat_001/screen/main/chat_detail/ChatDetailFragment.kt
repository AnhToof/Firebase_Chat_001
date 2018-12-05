package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
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
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.toast
import com.lobesoftware.toof.firebase_chat_001.repositories.GroupRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.MessageRepository
import com.lobesoftware.toof.firebase_chat_001.repositories.UserRepository
import com.lobesoftware.toof.firebase_chat_001.screen.main.MainActivity
import kotlinx.android.synthetic.main.fragment_chat_detail.*
import kotlinx.android.synthetic.main.fragment_chat_detail.view.*
import javax.inject.Inject

class ChatDetailFragment : Fragment(), ChatDetailContract.View {

    @Inject
    internal lateinit var mUserRepository: UserRepository
    @Inject
    internal lateinit var mGroupRepository: GroupRepository
    @Inject
    internal lateinit var mMessageRepository: MessageRepository
    private lateinit var mView: View
    private lateinit var mPresenter: ChatDetailPresenter
    private lateinit var mNavigator: ChatDetailNavigator
    private lateinit var mAdapter: ChatDetailAdapter
    private lateinit var mProgressDialog: ProgressDialog
    private var mGroup: Group? = null

    enum class GroupType(val value: Boolean) {
        PRIVATE(false),
        GROUP(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val app = activity?.application
        if (app is MainApplication) {
            app.mAppComponent.inject(this@ChatDetailFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_chat_detail, container, false)
        mPresenter = ChatDetailPresenter(this, mUserRepository, mGroupRepository, mMessageRepository)
        initViews()
        setUpData()
        handleEvents()
        return mView
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val item = menu.findItem(R.id.action_add)
        item.isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        mGroup?.let {
            if (it.type == GroupType.GROUP.value) {
                inflater.inflate(R.menu.menu_option_chat_detail, menu)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
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
        (activity as? MainActivity)?.let {
            it.showBottomNavigation()
            it.supportActionBar?.setDisplayHomeAsUpEnabled(false)
            it.supportActionBar?.title = getString(R.string.title_chat_screen)
        }
        super.onDetach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                mNavigator.backToChatScreen()
            }
            R.id.action_description -> {
                mGroup?.let {
                    mNavigator.goToDescriptionScreen(it)
                }
            }
            R.id.action_edit_group -> {
                mGroup?.let {
                    mNavigator.goToEditGroupScreen(it)
                }
            }
            R.id.action_leave_group -> {
                showDialogConfirmLeaveGroup()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCheckCurrentUserFail() {
        (activity as? MainActivity)?.let {
            it.toast(it.getString(R.string.msg_session_expired), Toast.LENGTH_LONG)
        }
        mNavigator.goToAuthenticationScreen()
    }

    override fun onFetchGroupInformationSuccess(group: Group) {
        mGroup = group
    }

    override fun onFetchFail(error: Throwable) {
        if (error == NullPointerException()) {
            (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
        } else {
            (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
        }
    }

    override fun onFetchUsersInGroupSuccess(users: List<User>) {
        mAdapter.setListUsers(users)
        mGroup?.let {
            mPresenter.fetchMessages(it)
        }
    }

    override fun onLeaveGroupSuccess() {
        mNavigator.backToChatScreen()
    }

    override fun onMessageAdded(message: Message) {
        mAdapter.addMessage(message)
        recycler_view_chat.scrollToPosition(0)
    }

    override fun showProgressDialog() {
        mProgressDialog.show()
    }

    override fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            arguments?.let { args ->
                mGroup = args.getParcelable(ARGUMENT_GROUP)
                it.supportActionBar?.title =
                        "${args[ARGUMENT_TITLE]}"
            }
            mNavigator = ChatDetailNavigatorImpl(it)
            it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        setUpRecyclerView()
        setUpProgressDialog()
    }

    private fun setUpData() {
        mGroup?.let {
            it.id?.let { id ->
                mPresenter.fetchGroupInformation(id)
            }
            mPresenter.fetchUsersInGroup(it)
        }
    }

    private fun setUpRecyclerView() {
        context?.let {
            mAdapter = ChatDetailAdapter(it)
            mView.recycler_view_chat.apply {
                val layout = LinearLayoutManager(it)
                layout.reverseLayout = true
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                adapter = mAdapter
            }
        }
    }

    private fun setUpProgressDialog() {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(getString(R.string.msg_delete_group))
    }

    private fun handleEvents() {
        mView.image_send.setOnClickListener {
            if (mView.edit_message.text.trim().isNotBlank()) {
                mGroup?.let { group ->
                    val message = Message(
                        content = mView.edit_message.text.toString()
                    )
                    mPresenter.sendMessage(group, message)
                }
                mView.edit_message.text.clear()
            }
        }
    }

    private fun showDialogConfirmLeaveGroup() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(getString(R.string.alert_leave_group))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.alert_ok)) { _, _ ->
                mGroup?.let {
                    mPresenter.leaveGroup(it)
                }
            }
            .setNegativeButton(getString(R.string.alert_cancel)) { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.setTitle(getString(R.string.app_name))
        alert.show()
    }

    companion object {
        private const val ARGUMENT_TITLE = "title"
        private const val ARGUMENT_GROUP = "group"

        fun getInstance(title: String, group: Group): ChatDetailFragment {
            val args = Bundle()
            args.putString(ARGUMENT_TITLE, title)
            args.putParcelable(ARGUMENT_GROUP, group)
            val fragment = ChatDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
