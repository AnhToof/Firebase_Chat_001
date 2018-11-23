package com.lobesoftware.toof.firebase_chat_001.data.model

data class Group(
    var id: String? = null,
    var type: Boolean? = null,
    var title: String? = null,
    var description: String? = null,
    var action: String? = null,
    var members: HashMap<String, Boolean> = HashMap()
)
