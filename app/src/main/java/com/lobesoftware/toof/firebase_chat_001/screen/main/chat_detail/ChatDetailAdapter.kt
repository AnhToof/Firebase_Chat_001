package com.lobesoftware.toof.firebase_chat_001.screen.main.chat_detail

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Message
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrl
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.extension.toString
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import kotlinx.android.synthetic.main.item_chat.view.*
import java.util.*
import kotlin.collections.ArrayList

class ChatDetailAdapter(private val context: Context) : RecyclerView.Adapter<ChatDetailAdapter.ViewHolder>() {

    private var mUsers = ArrayList<User>()
    private var mMessages = ArrayList<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
        return ViewHolder(view, mUsers)
    }

    override fun getItemCount(): Int = mMessages.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mMessages[position])
    }

    fun addMessage(message: Message) {
        mMessages.add(0, message)
        notifyItemInserted(0)
    }

    fun setListUsers(users: List<User>) {
        mUsers.addAll(users)
    }

    class ViewHolder(
        view: View,
        users: ArrayList<User>
    ) : RecyclerView.ViewHolder(view) {
        private val mImageAvatar = view.image_avatar
        private val mTextFullName = view.text_full_name
        private val mTextTime = view.text_time
        private val mTextMessage = view.text_message
        private val mImageMessage = view.image_message
        private val mUsers = users

        fun bindViewData(message: Message) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = mUsers.first {
                it.id == message.from_user
            }.fullName
            mTextTime.text = Date(message.timestamp).toString(Constant.DateTimeFormat.DATE_MESSAGE)
            if (message.message_type == Constant.KeyDatabase.Message.TYPE_TEXT) {
                mTextMessage.text = message.content
            } else {
                mImageMessage.visibility = View.VISIBLE
                mImageMessage.loadUrl(message.content)
            }
        }
    }
}
