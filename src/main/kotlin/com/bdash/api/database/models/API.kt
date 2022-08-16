package com.bdash.api.database.models

import kotlinx.serialization.Serializable

@Serializable
class Notification(
    val title: String,
    val description: String,
    val image: String? = null,
)