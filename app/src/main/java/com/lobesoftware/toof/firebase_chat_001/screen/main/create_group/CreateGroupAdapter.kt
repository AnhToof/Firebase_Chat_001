package com.lobesoftware.toof.firebase_chat_001.screen.main.create_group

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

class CreateGroupAdapter(private val context: Context) : RecyclerView.Adapter<CreateGroupAdapter.ViewHolder>() {

    private var mMembers = ArrayList<User>()
    private var mListener: ItemRecyclerViewClickListener<User>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view, mMembers, mListener)
    }

    override fun getItemCount(): Int = mMembers.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bindViewData(mMembers[position])
    }

    fun setItemRecyclerViewListener(listener: ItemRecyclerViewClickListener<User>?) {
        mListener = listener
    }

    fun setListMembers(members: List<User>) {
        mMembers.addAll(members)
        notifyDataSetChanged()
    }

    class ViewHolder(
        view: View,
        members: ArrayList<User>,
        listener: ItemRecyclerViewClickListener<User>?
    ) : RecyclerView.ViewHolder(view) {

        private val mImageAvatar = view.image_avatar
        private val mTextFullName = view.text_full_name
        private val mMembers = members
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
                mListener?.onItemClick(it, mMembers[adapterPosition], adapterPosition)
            }
        }
    }
}
