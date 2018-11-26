package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.item_user.view.*

class FriendAdapter(private val context: Context) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    private var mUsers = ArrayList<User>()
    private var mListener: ItemRecyclerViewClickListener<User>? = null
    private var mIsSearch: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, mUsers, mListener)
    }

    override fun getItemCount(): Int = mUsers.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mUsers[position])
    }

    fun setListFriends(users: ArrayList<User>, isSearch: Boolean) {
        mUsers = users
        mIsSearch = isSearch
        notifyDataSetChanged()
    }

    fun addFriend(user: User, position: Int) {
        mUsers.add(user)
        notifyItemInserted(position)
    }

    fun removeFriend(user: User, position: Int) {
        mUsers.remove(user)
        notifyItemRemoved(position)
    }

    fun changeFriend(user: User, position: Int) {
        mUsers[position] = user
        notifyItemChanged(position)
    }

    fun setItemRecyclerViewListener(listener: ItemRecyclerViewClickListener<User>?) {
        mListener = listener
    }

    class ViewHolder(
        view: View,
        users: ArrayList<User>,
        listener: ItemRecyclerViewClickListener<User>?
    ) : RecyclerView.ViewHolder(view) {
        private val mImageAvatar: ImageView = view.image_avatar
        private val mTextFullName: TextView = view.text_full_name
        private val mUsers = users
        private val mListener = listener

        init {
            handleEvents()
        }

        fun bindViewData(user: User) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = user.fullName
        }

        private fun handleEvents() {
            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, mUsers[adapterPosition], adapterPosition)
            }
        }
    }
}
