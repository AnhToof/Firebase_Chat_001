package com.lobesoftware.toof.firebase_chat_001.data.model

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var id: String? = null,
    var email: String? = null,
    var fullName: String? = null,
    var phone: String? = null,
    @Exclude
    var action: String? = null
) : Parcelable
