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
    const val ACTION_SENT = "Sent"
    const val ACTION_RECEIVED = "Received"
    const val ACTION_FRIEND = "Friend"
    const val LIMIT_MESSAGES = 11
    const val LIMIT_LAST_MESSAGE = 1

    object KeyDatabase {
        object User {
            const val USER = "users"
            const val GROUP = "groups"
            const val EMAIL = "email"
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

        object Message {
            const val MESSAGES = "messages"
            const val CONTENT = "content"
            const val TYPE = "type"
            const val TYPE_TEXT = "text"
            const val TYPE_IMAGE = "image"
            const val TIMESTAMP = "timestamp"
        }
    }

    object RequestCode {
        const val REQUEST_CODE = 999
    }

    object ResultCode {
        const val RESULT_OK = 9
    }

    object DateTimeFormat {
        const val DATE_MESSAGE = "yyyy/MM/dd HH:mm"
    }

    object ScreenType {
        const val EDIT = "edit"
        const val ADD = "add"
    }

    object KeyStorage {
        const val IMAGES = "images"
    }
}
