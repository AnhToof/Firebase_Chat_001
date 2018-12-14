package com.lobesoftware.toof.firebase_chat_001.data.model

import com.google.firebase.database.Exclude
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import java.util.*

data class Message(
    var id: String? = null,
    var from_user: String? = null,
    var to_user: String? = null,
    var content: String? = null,
    var message_type: String = Constant.KeyDatabase.Message.TYPE_TEXT,
    var timestamp: Long = Date().time,
    @Exclude
    var action: String? = null,
    @Exclude
    var user: User? = null
)
