package com.lobesoftware.toof.firebase_chat_001.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: String? = null,
    var email: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    var action: String? = null,
    var position: Int? = null
) : Parcelable
