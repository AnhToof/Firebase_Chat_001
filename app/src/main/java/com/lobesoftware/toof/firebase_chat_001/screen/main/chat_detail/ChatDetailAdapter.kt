package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrl
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.extension.toString
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import com.lobesoftware.toof.firebase_chat_001.utils.LoadMoreListener
import kotlinx.android.synthetic.main.item_chat.view.*
import kotlinx.android.synthetic.main.item_loading.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailAdapter(private val context: Context, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mMessages = ArrayList<Message>()
    private var mIsLoadingAtTop: Boolean = false
    private var mIsLoadingAtBottom: Boolean = true
    private var mLoadMoreListener: LoadMoreListener? = null
    private var mItemClickListener: ItemRecyclerViewClickListener<Message>? = null
    private var mCurrentUserId: String? = null
    private var mLastItemPosition = 0
    private var mFirstItemPosition = 0

    enum class ViewType(val type: Int) {
        LOADING(0),
        ITEM(1)
    }

    init {
        val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = linearLayoutManager.itemCount
                mFirstItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (mFirstItemPosition == 0) {
                    mLastItemPosition = lastVisibleItem
                }
                if (!mIsLoadingAtTop && totalItemCount <= (lastVisibleItem + 1)) {
                    mLoadMoreListener?.onLoadMoreTop()
                    mIsLoadingAtTop = true
                }
                if (!mIsLoadingAtBottom && mFirstItemPosition == 0) {
                    mLoadMoreListener?.onLoadMoreBottom()
                    mIsLoadingAtBottom = true
                }
            }
        })
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessages[position].id == null) ViewType.LOADING.type else ViewType.ITEM.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            ViewType.LOADING.type -> {
                LoadingViewHolder(inflater.inflate(R.layout.item_loading, parent, false))
            }
            else -> {
                MessageViewHolder(
                    inflater.inflate(R.layout.item_chat, parent, false),
                    mCurrentUserId,
                    mMessages,
                    mItemClickListener
                )
            }
        }
    }

    override fun getItemCount(): Int = mMessages.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is LoadingViewHolder -> {
                viewHolder.setIndeterminate()
            }
            is MessageViewHolder -> {
                viewHolder.bindViewData(mMessages[position])
            }
        }
    }

    fun addMessage(message: Message) {
        mMessages.add(0, message)
        notifyItemInserted(0)
    }

    fun addLoadingAtTop() {
        mMessages.add(Message(id = null))
        notifyItemInserted(mMessages.size - 1)
    }

    fun addLoadingAtBottom() {
        mMessages.add(0, Message(id = null))
        notifyItemInserted(0)
    }

    fun addMessagesAtTop(messages: List<Message>) {
        mMessages.addAll(messages)
        notifyDataSetChanged()
    }

    fun addMessagesAtBottom(messages: List<Message>) {
        messages.forEach {
            mMessages.add(0, it)
        }
        notifyItemRangeInserted(0, messages.size)
    }

    fun removeLoadingAtTop() {
        mMessages.removeAt(mMessages.size - 1)
        notifyItemRemoved(mMessages.size - 1)
    }

    fun removeLoadingAtBottom() {
        mMessages.removeAt(0)
        notifyItemRemoved(0)
    }

    fun setLoadingAtTop(isLoadingAtTop: Boolean) {
        mIsLoadingAtTop = isLoadingAtTop
    }

    fun setLoadingAtBottom(isLoadingAtBottom: Boolean) {
        mIsLoadingAtBottom = isLoadingAtBottom
    }

    fun setCurrentUserId(currentUserId: String) {
        mCurrentUserId = currentUserId
    }

    fun setLoadMore(listener: LoadMoreListener) {
        mLoadMoreListener = listener
    }

    fun setItemRecyclerViewClickListener(listener: ItemRecyclerViewClickListener<Message>) {
        mItemClickListener = listener
    }

    fun getFirstVisibleItemPosition(): Int = mFirstItemPosition

    fun getLastVisibleItemPosition(): Int = mLastItemPosition

    class MessageViewHolder(
        view: View,
        currentId: String?,
        messages: List<Message>,
        listener: ItemRecyclerViewClickListener<Message>?
    ) : RecyclerView.ViewHolder(view) {
        private val mImageAvatar = view.image_avatar
        private val mTextFullName = view.text_full_name
        private val mTextTime = view.text_time
        private val mTextMessage = view.text_message
        private val mImageMessage = view.image_message
        private val mMessage = messages
        private val mListener = listener
        private val mCurrentId = currentId

        init {
            handleEvents()
        }

        fun bindViewData(message: Message) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = message.user?.fullName
            mTextTime.text = Date(message.timestamp).toString(Constant.DateTimeFormat.DATE_MESSAGE)
            if (message.message_type == Constant.KeyDatabase.Message.TYPE_TEXT) {
                mTextMessage.text = message.content
                mImageMessage.visibility = View.GONE
            } else {
                mImageMessage.visibility = View.VISIBLE
                mImageMessage.loadUrl(message.content)
            }
        }

        private fun handleEvents() {
            mImageAvatar.setOnClickListener {
                mMessage[adapterPosition].user?.let { user ->
                    if (user.id != mCurrentId) {
                        mListener?.onItemClick(it, mMessage[adapterPosition], adapterPosition)
                    }
                }
            }
            mImageMessage.setOnClickListener {
                mListener?.onItemClick(it, mMessage[adapterPosition], adapterPosition)
            }
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mProgressBar = view.progressBar

        fun setIndeterminate() {
            mProgressBar.isIndeterminate = true
        }
    }
}
