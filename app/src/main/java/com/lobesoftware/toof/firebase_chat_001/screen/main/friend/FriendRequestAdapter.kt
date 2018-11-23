package com.lobesoftware.toof.firebase_chat_001.screen.main.friend

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.item_user.view.*

class FriendRequestAdapter(private val context: Context) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    private val mUsers = ArrayList<User>()
    private var mListener: ItemRecyclerViewClickListener<User>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, mUsers, mListener)
    }

    override fun getItemCount(): Int = mUsers.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mUsers[position])
    }

    fun updateData(user: User) {
        when (user.action) {
            Constant.ACTION_ADD -> {
                user.position = mUsers.size
                mUsers.add(user)
                notifyItemInserted(mUsers.size)
            }
            Constant.ACTION_REMOVE -> {
                for ((index, oldUser) in mUsers.withIndex()) {
                    if (oldUser.id == user.id) {
                        mUsers.remove(oldUser)
                        notifyItemRemoved(index)
                        break
                    }
                }
            }
            Constant.ACTION_CHANGE -> {
                for ((index, oldUser) in mUsers.withIndex()) {
                    if (oldUser.id == user.id) {
                        mUsers[index] = user
                        notifyItemChanged(index)
                        break
                    }
                }
            }
        }
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
        private val mConstraintLayoutRequest: ConstraintLayout = view.constraint_layout_request
        private val mButtonAccept: Button = view.button_accept
        private val mButtonReject: Button = view.button_reject
        private val mUser = users
        private val mListener = listener

        init {
            handleEvents()
        }

        fun bindViewData(user: User) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = user.fullName
            mConstraintLayoutRequest.visibility = View.VISIBLE
        }

        private fun handleEvents() {
            mButtonAccept.setOnClickListener {
                mListener?.onItemClick(mButtonAccept, mUser[adapterPosition], adapterPosition)
            }

            mButtonReject.setOnClickListener {
                mListener?.onItemClick(mButtonReject, mUser[adapterPosition], adapterPosition)
            }

            itemView.setOnClickListener {
                mListener?.onItemClick(itemView, mUser[adapterPosition], adapterPosition)
            }
        }
    }
}
