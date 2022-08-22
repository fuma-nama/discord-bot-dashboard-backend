package com.bdash.api

import io.ktor.server.application.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

interface API {
    fun PipelineContext<Unit, ApplicationCall>.getGuildDetail(guild: String): JsonElement
    fun PipelineContext<Unit, ApplicationCall>.getGuildDetailAdvanced(guild: String): JsonElement? = null

    fun PipelineContext<Unit, ApplicationCall>.getActionsData(guild: String): JsonElement? = null
    fun PipelineContext<Unit, ApplicationCall>.getFeaturesData(guild: String): JsonElement? = null

    fun PipelineContext<Unit, ApplicationCall>.getNotifications(guild: String): Array<Notification> = emptyArray()
}

inline fun <reified T> T?.json() = Json.encodeToJsonElement(this)