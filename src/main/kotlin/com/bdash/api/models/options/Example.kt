package com.bdash.api.models.options

import kotlinx.serialization.Serializable

@Serializable
class FeatureExample : Feature(
    id = "auto_kill_kane",
    banner = null,
    name = "自動殺死凱恩",
    description = "凱恩加入服務器時自動殺死凱恩",
    enabled = false
)

object ExampleValues {
    var value = FeatureOptionExample("Hello World from Ktor")
}

@Serializable
class FeatureDetailExample(
    val values: FeatureOptionExample
) : FeatureDetail(
    id = "auto_kill_kane",
    name = "自動殺死凱恩",
    description = "凱恩加入服務器時自動殺死凱恩",
)

@Serializable
data class FeatureOptionExample(
    val test: String
) : FeatureOption()