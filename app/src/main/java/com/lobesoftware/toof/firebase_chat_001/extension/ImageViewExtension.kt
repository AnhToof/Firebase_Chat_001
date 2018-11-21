package com.lobesoftware.toof.firebase_chat_001.extension

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lobesoftware.toof.firebase_chat_001.R

fun ImageView.loadUrlWithCircleCropTransform(url: String?) {
    Glide.with(context)
        .load(url)
        .apply(RequestOptions.circleCropTransform().placeholder(R.drawable.ic_profile))
        .into(this)
}
