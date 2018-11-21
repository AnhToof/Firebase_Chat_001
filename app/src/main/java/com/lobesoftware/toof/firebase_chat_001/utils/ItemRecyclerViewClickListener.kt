package com.lobesoftware.toof.firebase_chat_001.utils

import android.view.View

interface ItemRecyclerViewClickListener<T> {

    fun onItemClick(view: View, item: T, position: Int)
}
