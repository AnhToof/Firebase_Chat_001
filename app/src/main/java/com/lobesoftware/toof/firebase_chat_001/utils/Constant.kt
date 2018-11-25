package com.lobesoftware.toof.firebase_chat_001.utils

object Constant {
    const val ENTER_SPACE_FORMAT = "\n"
    const val NUMBER_FORMAT = "[0-9]"
    const val LOWER_CASE_FORMAT = "[a-z]"
    const val UPPER_CASE_FORMAT = "[A-Z]"
    const val SPECIAL_CHAR_FORMAT = "[!@#%^]"
    const val ALL_SPECIAL_CHAR_FORMAT = "[!@#$%^&*]"
    const val EMAIL_FORMAT = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    const val LENGTH_PASSWORD_FORMAT = 8
    const val MAX_LENGTH_NAME_FORMAT = 255
    const val ACTION_ADD = "Add"
    const val ACTION_REMOVE = "Remove"
    const val ACTION_CHANGE = "Change"

    object KeyDatabase {
        object User {
            const val USER = "users"
            const val GROUP = "groups"
        }

        object Friend {
            const val FRIENDSHIP = "friendships"
            const val FRIEND_REQUEST = "friend_request"
            const val TYPE_REQUEST_SENT = "sent"
            const val TYPE_REQUEST_RECEIVED = "received"
        }

        object Group {
            const val GROUP = "groups"
            const val ID = "id"
            const val TYPE = "type"
            const val MEMBER = "members"
        }
    }
}
