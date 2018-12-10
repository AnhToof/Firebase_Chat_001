package com.lobesoftware.toof.firebase_chat_001.screen.main.add_member

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.data.model.User
import com.lobesoftware.toof.firebase_chat_001.extension.loadUrlWithCircleCropTransform
import com.lobesoftware.toof.firebase_chat_001.utils.ItemRecyclerViewClickListener
import kotlinx.android.synthetic.main.item_user.view.*

class AddMemberAdapter(private val context: Context) : RecyclerView.Adapter<AddMemberAdapter.ViewHolder>() {

    private var mUsers = ArrayList<User>()
    private var mUsersSelected = ArrayList<User>()
    private var mListener: ItemRecyclerViewClickListener<User>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, mUsers, mUsersSelected, mListener)
    }

    override fun getItemCount(): Int = mUsers.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mUsers[position])
    }

    fun getUsersSelected(): ArrayList<User> {
        return mUsersSelected
    }

    fun setUsersSelected(users: List<User>) {
        mUsersSelected.addAll(users)
    }

    fun setItemRecyclerViewListener(listener: ItemRecyclerViewClickListener<User>?) {
        mListener = listener
    }

    fun setListMembers(users: List<User>) {
        mUsers.addAll(users)
        notifyDataSetChanged()
    }

    fun addMember(user: User, position: Int) {
        mUsers.add(user)
        notifyItemInserted(position)
    }

    fun removeMember(position: Int) {
        mUsers.removeAt(position)
        notifyItemRemoved(position)
    }

    fun changeMember(user: User, position: Int) {
        mUsers[position] = user
        notifyItemChanged(position)
    }

    class ViewHolder(
        view: View,
        users: List<User>,
        usersSelected: ArrayList<User>,
        listener: ItemRecyclerViewClickListener<User>?
    ) : RecyclerView.ViewHolder(view) {

        private val mImageAvatar = view.image_avatar
        private val mTextFullName = view.text_full_name
        private val mCheckbox = view.checkbox
        private val mUsers = users
        private val mUsersSelected = usersSelected
        private val mListener = listener

        init {
            handleEvents()
        }

        fun bindViewData(user: User) {
            mImageAvatar.loadUrlWithCircleCropTransform(null)
            mTextFullName.text = user.fullName
            mCheckbox.visibility = View.VISIBLE
            mCheckbox.isChecked = mUsersSelected.contains(user)
        }

        private fun handleEvents() {
            itemView.setOnClickListener {
                mListener?.onItemClick(it, mUsers[adapterPosition], adapterPosition)
            }

            mCheckbox.setOnClickListener {
                if (!mUsersSelected.contains(mUsers[adapterPosition])) {
                    mCheckbox.isChecked = true
                    mUsersSelected.add(mUsers[adapterPosition])
                } else {
                    mCheckbox.isChecked = false
                    mUsersSelected.remove(mUsers[adapterPosition])
                }
            }
        }
    }
}
