package com.lobesoftware.toof.firebase_chat_001.utils.validator

import android.content.Context
import com.lobesoftware.toof.firebase_chat_001.R
import com.lobesoftware.toof.firebase_chat_001.extension.validWithPattern
import com.lobesoftware.toof.firebase_chat_001.utils.Constant
import java.util.regex.Pattern

class Validator(private val context: Context) {

    private var mNotGoodPattern: Pattern? = null
    fun validateName(name: String): String? {
        val errorMessages = ArrayList<String>()
        if (name.isBlank()) {
            return context.resources.getString(R.string.empty_field)
        }
        if (name.length > Constant.MAX_LENGTH_NAME_FORMAT) {
            errorMessages.add(context.resources.getString(R.string.more_than_255_char))
        }
        if (name.validWithPattern(Constant.ALL_SPECIAL_CHAR_FORMAT)) {
            errorMessages.add(context.resources.getString(R.string.contain_special_char))
        }
        mNotGoodPattern?.let {
            val isValid = name.validWithPattern(it)
            if (!isValid) {
                errorMessages.add(
                    context.getString(R.string.contain_invalid_word)
                )
            }
        }
        return convertStringToListStringWithFormatPattern(
            errorMessages,
            Constant.ENTER_SPACE_FORMAT
        )
    }

    fun validateEmail(email: String): String? {
        if (email.isBlank()) {
            return context.resources.getString(R.string.empty_field)
        }
        return if (!email.matches(Constant.EMAIL_FORMAT.toRegex())) {
            context.resources.getString(R.string.wrong_email_format)
        } else null
    }

    fun validatePassword(password: String): String? {
        val list = ArrayList<String>()
        if (password.isBlank()) {
            return context.resources.getString(R.string.empty_field)
        }
        if (password.length < Constant.LENGTH_PASSWORD_FORMAT) {
            list.add(context.resources.getString(R.string.less_than_8_char))
        }
        if (!password.validWithPattern(Constant.LOWER_CASE_FORMAT) || !password.validWithPattern(
                Constant.UPPER_CASE_FORMAT
            )
        ) {
            list.add(context.resources.getString(R.string.upper_and_lower_case))
        }
        if (!password.validWithPattern(Constant.NUMBER_FORMAT) || !password.validWithPattern(
                Constant.SPECIAL_CHAR_FORMAT
            )
        ) {
            list.add(context.resources.getString(R.string.at_least_1_number_or_special_char))
        }
        return convertStringToListStringWithFormatPattern(
            list,
            Constant.ENTER_SPACE_FORMAT
        )
    }

    fun validateGroupTitle(title: String): String? {
        if (title.trim().isBlank()) {
            return context.getString(R.string.please_input_title_group)
        }
        return null
    }

    private fun convertStringToListStringWithFormatPattern(
        strings: List<String>,
        format: String
    ): String? {
        if (strings.isEmpty()) {
            return null
        }
        val builder = StringBuilder()
        for (s in strings) {
            builder.append(s)
            builder.append(format)
        }
        var result = builder.toString()
        result = result.substring(0, result.length - format.length)
        return result
    }
}
