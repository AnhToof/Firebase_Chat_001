package com.lobesoftware.toof.firebase_chat_001.extension

import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String): String {
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}
