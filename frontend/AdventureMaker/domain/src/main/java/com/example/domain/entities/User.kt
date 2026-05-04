package com.example.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
): Parcelable
