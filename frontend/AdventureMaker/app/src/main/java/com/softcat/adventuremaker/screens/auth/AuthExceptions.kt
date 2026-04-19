package com.softcat.adventuremaker.screens.auth

import android.content.Context
import com.softcat.adventuremaker.R

class EmptyAuthFieldsException: Exception("Fill all auth fields.")

fun Throwable.toErrorMessage(context: Context) = when (this) {
    is EmptyAuthFieldsException -> context.getString(R.string.empty_auth_fields_error)
    else -> message ?: ""
}