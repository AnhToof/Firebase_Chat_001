package com.lobesoftware.toof.firebase_chat_001.screen.main.chat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.Group
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.item_user.view.*

class ChatAdapter(private val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var mConversations = ArrayList<Group>()
    private var mListener: ItemRecyclerViewClickListener<Group>? = null
    private var mIsSearch: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, mConversations, mListener)
    }

    override fun getItemCount(): Int = mConversations.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mConversations[position])
    }

    fun setItemRecyclerViewListener(listener: ItemRecyclerViewClickListener<Group>?) {
        mListener = listener
    }

    fun setListConversations(conversations: ArrayList<Group>, isSearch: Boolean) {
        mConversations = conversations
        mIsSearch = isSearch
        notifyDataSetChanged()
    }

    fun addConversation(group: Group, position: Int) {
        mConversations.add(group)
        notifyItemInserted(position)
    }

    fun removeConversation(position: Int) {
        mConversations.removeAt(position)
        notifyItemRemoved(position)
    }

    fun changeConversation(group: Group, position: Int) {
        mConversations[position] = group
        notifyItemChanged(position)
    }

    class ViewHolder(
        view: View,
        conversations: ArrayList<Group>,
        listener: ItemRecyclerViewClickListener<Group>?
    ) : RecyclerView.ViewHolder(view) {

        private val mImageAvatar = view.image_avatar
        private val mTextFullName = view.text_full_name
        private val mConversations = conversations
        private val mListener = listener

        init {
            handleEvents()
        }

        fun bindViewData(group: Group) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = group.title
        }

        private fun handleEvents() {
            itemView.setOnClickListener {
                mListener?.onItemClick(it, mConversations[adapterPosition], adapterPosition)
            }
        }
    }
}
