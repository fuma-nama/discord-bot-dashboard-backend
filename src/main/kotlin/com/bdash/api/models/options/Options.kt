package com.bdash.api.models.options

import kotlinx.serialization.Serializable

@Serializable
sealed class Feature (
    val id: String,
    val banner: String?,
    val name: String,
    val description: String,
    val enabled: Boolean
)

@Serializable
sealed class FeatureDetail(
    val id: String,
    val name: String,
    val description: String,
)

sealed class FeatureOption