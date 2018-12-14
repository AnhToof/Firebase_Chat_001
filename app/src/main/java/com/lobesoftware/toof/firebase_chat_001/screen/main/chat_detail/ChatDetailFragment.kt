package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import com.lobesoftware.toof.firebase_chat_001.utils.LoadMoreListener
import kotlinx.android.synthetic.main.fragment_chat_detail.view.*
import javax.inject.Inject

class ChatDetailFragment : Fragment(), ChatDetailContract.View, Toolbar.OnMenuItemClickListener, LoadMoreListener,
    ItemRecyclerViewClickListener<Message> {

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
    private val mMessages = ArrayList<Message>()
    private val mUsers = ArrayList<User>()
    private var mGroup: Group? = null
    private var mCurrentUserId: String? = null
    private var mLoadFirstTime: Boolean = true

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
        mView = inflater.inflate(R.layout.fragment_chat_detail, container, false)
        mPresenter = ChatDetailPresenter(this, mUserRepository, mGroupRepository, mMessageRepository)
        initViews()
        handleEvents()
        return mView
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_description -> {
                mGroup?.let {
                    mNavigator.goToDescriptionScreen(it)
                }
                return true
            }
            R.id.action_edit_group -> {
                mGroup?.let {
                    mNavigator.goToEditGroupScreen(it)
                }
                return true
            }
            R.id.action_leave_group -> {
                showDialogConfirmLeaveGroup()
                return true
            }
        }
        return false
    }

    override fun onStart() {
        setUpData()
        super.onStart()
    }

    override fun onDestroy() {
        mPresenter.onDestroy()
        super.onDestroy()
    }

    override fun onGetCurrentUserIdSuccess(userId: String) {
        hideEditOption(userId)
        mAdapter.setCurrentUserId(userId)
        mCurrentUserId = userId
    }

    override fun onCheckCurrentUserFail() {
        (activity as? MainActivity)?.toast(getString(R.string.msg_session_expired), Toast.LENGTH_LONG)
        mNavigator.goToAuthenticationScreen()
    }

    override fun onFetchGroupInformationSuccess(group: Group) {
        mGroup = group
        mGroup?.id?.let {
            mPresenter.fetchLastMessage(it, mUsers)
        }
    }

    override fun onFetchFail(error: Throwable) {
        if (error is NullPointerException) {
            (activity as? MainActivity)?.toast(getString(R.string.msg_error_something_wrong), Toast.LENGTH_LONG)
        } else {
            (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
        }
    }

    override fun onLeaveGroupSuccess() {
        mNavigator.backToPreviousScreen()
    }

    override fun onUploadFail(error: Throwable) {
        (activity as? MainActivity)?.toast(error.localizedMessage, Toast.LENGTH_LONG)
    }

    override fun onUploadSuccess(uri: Uri) {
        mGroup?.let { group ->
            val message = Message(
                content = uri.toString(),
                message_type = Constant.KeyDatabase.Message.TYPE_IMAGE
            )
            mPresenter.sendMessage(group, message)
        }
    }

    override fun onMessageAdded(message: Message) {
        if (!mMessages.contains(message)) {
            if (mAdapter.getFirstVisibleItemPosition() <= mAdapter.getLastVisibleItemPosition()) {
                mMessages.add(0, message)
                mAdapter.addMessage(message)
                message.user?.let {
                    mUsers.add(it)
                }
                mView.recycler_view_chat.scrollToPosition(0)
            } else {
                mAdapter.setLoadingAtBottom(false)
            }
        }
    }

    override fun onFetchPreviousMessagesSuccess(messages: List<Message>) {
        mAdapter.removeLoadingAtTop()
        if (!messages.isEmpty()) {
            mAdapter.addMessagesAtTop(messages)
            mMessages.addAll(messages)
            messages.forEach {
                it.user?.let { user ->
                    if (!mUsers.contains(user)) {
                        mUsers.add(user)
                    }
                }
            }
            if (mLoadFirstTime) {
                mView.recycler_view_chat.scrollToPosition(0)
                mLoadFirstTime = false
            }
            if (messages.size + 1 < Constant.LIMIT_MESSAGES) {
                mAdapter.setLoadingAtTop(true)
            } else {
                mAdapter.setLoadingAtTop(false)
            }
        }
    }

    override fun onFetchNextMessagesSuccess(messages: List<Message>) {
        mAdapter.removeLoadingAtBottom()
        if (!mMessages.isEmpty()) {
            mAdapter.addMessagesAtBottom(messages)
            messages.forEach {
                mMessages.add(0, it)
                it.user?.let { user ->
                    if (!mUsers.contains(user)) {
                        mUsers.add(user)
                    }
                }
            }
            mView.recycler_view_chat.scrollToPosition(0)
            if (messages.size + 1 < Constant.LIMIT_MESSAGES) {
                mAdapter.setLoadingAtBottom(true)
            } else {
                mAdapter.setLoadingAtBottom(false)
            }
        }
    }

    override fun showProgressDialog() {
        mProgressDialog.show()
    }

    override fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    (activity as? MainActivity)?.toast(getString(R.string.permission_denied))
                } else {
                    selectImageFile()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            data?.data?.let {
                setUpProgressDialog(getString(R.string.uploading))
                mPresenter.uploadImage(it)
            }
        }
    }

    override fun onLoadMoreTop() {
        mAdapter.addLoadingAtTop()
        mGroup?.let { group ->
            val messageId = mMessages.last().id
            val groupId = group.id
            if (messageId != null && groupId != null) {
                mPresenter.fetchPreviousMessages(groupId, messageId, mUsers)
            }
        }
    }

    override fun onLoadMoreBottom() {
        mAdapter.addLoadingAtBottom()
        mGroup?.let { group ->
            val messageId = mMessages.last().id
            val groupId = group.id
            if (messageId != null && groupId != null) {
                mPresenter.fetchNextMessages(groupId, messageId, mUsers)
            }
        }
    }

    override fun onItemClick(view: View, item: Message, position: Int) {
        when (view.id) {
            R.id.image_message -> {
                item.content?.let {
                    mNavigator.openImage(it)
                }
            }
            R.id.image_avatar -> {
                item.user?.let {
                    mNavigator.goToUserDetailScreen(it)
                }
            }
        }
    }

    private fun initViews() {
        (activity as? MainActivity)?.let {
            arguments?.let { args ->
                mGroup = args.getParcelable(ARGUMENT_GROUP)
                mView.toolbar.title =
                    "${args[ARGUMENT_TITLE]}"
            }
            mNavigator = ChatDetailNavigatorImpl(it)
        }
        mGroup?.let {
            if (it.type == GroupType.GROUP.value) {
                mView.toolbar.inflateMenu(R.menu.menu_option_chat_detail)
            }
            mPresenter.getCurrentUserId()
        }
        setUpRecyclerView()
    }

    private fun setUpData() {
        mGroup?.let {
            it.id?.let { id ->
                mPresenter.fetchGroupInformation(id)
            }
        }
    }

    private fun setUpRecyclerView() {
        context?.let {
            mView.recycler_view_chat.apply {
                val layout = LinearLayoutManager(it)
                layout.reverseLayout = true
                layoutManager = layout
                itemAnimator = DefaultItemAnimator()
                addItemDecoration(DividerItemDecoration(it, layout.orientation))
                mAdapter = ChatDetailAdapter(it, this)
                mAdapter.setLoadMore(this@ChatDetailFragment)
                mAdapter.setItemRecyclerViewClickListener(this@ChatDetailFragment)
                adapter = mAdapter
            }
        }
    }

    private fun setUpProgressDialog(message: String) {
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage(message)
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
        mView.image_add_image.setOnClickListener {
            activity?.let { activity ->
                if (ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST_CODE
                    )
                } else {
                    selectImageFile()
                }
            }
        }
        mView.toolbar.setNavigationOnClickListener {
            mNavigator.backToPreviousScreen()
        }
        mView.toolbar.setOnMenuItemClickListener(this)
    }

    private fun selectImageFile() {
        val intent = Intent().apply {
            action = Intent.ACTION_GET_CONTENT
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo)), PICK_IMAGE_REQUEST)
    }

    private fun showDialogConfirmLeaveGroup() {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(getString(R.string.alert_leave_group))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.alert_ok)) { _, _ ->
                mGroup?.let {
                    setUpProgressDialog(getString(R.string.msg_delete_group))
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

    private fun hideEditOption(userId: String) {
        mGroup?.members?.let {
            if (!it.getValue(userId)) {
                mView.toolbar.menu.findItem(R.id.action_edit_group).isVisible = false
            }
        }
    }

    companion object {
        private const val ARGUMENT_TITLE = "title"
        private const val ARGUMENT_GROUP = "group"
        private const val PERMISSION_REQUEST_CODE = 999
        private const val PICK_IMAGE_REQUEST = 909

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
