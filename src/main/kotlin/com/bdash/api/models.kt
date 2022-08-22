package com.bdash.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Guild(
    /**
     * guild id
     **/
    val id: String,
    /**
     * guild name (2-100 characters, excluding trailing and leading whitespace)
     **/
    val name: String,
    /**
     * icon hash
     **/
    val icon: String?,
    /**
     * icon hash, returned when in the template object
     **/
    val icon_hash: String? = null,
    /**
     * true if the user is the owner of the guild
     **/
    val owner: Boolean? = null,
    /**
     * total permissions for the user in the guild (excludes overwrites)
     **/
    val permissions: String? = null,
)

@Serializable
data class GuildExists(
    /**
     * guild id
     **/
    val id: String,
    /**
     * guild name (2-100 characters, excluding trailing and leading whitespace)
     **/
    val name: String,
    /**
     * icon hash
     **/
    val icon: String?,
    /**
     * icon hash, returned when in the template object
     **/
    val icon_hash: String? = null,
    /**
     * true if the user is the owner of the guild
     **/
    val owner: Boolean? = null,
    /**
     * total permissions for the user in the guild (excludes overwrites)
     **/
    val permissions: String? = null,

    /**
     * If bot is exists in the server
     */
    var exist: Boolean = false,
)

@Serializable
class Notification(
    val title: String,
    val description: String,
    val image: String? = null,
)

@Serializable
class Features(val enabled: Array<String>, val data: JsonElement? = null)

@Serializable
class Feature(val values: JsonObject)

@Serializable
class Settings(val values: JsonObject)

@Serializable
class TaskBody(
    val name: String? = null,
    val options: JsonObject,
)

@Serializable
class TaskDetail(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val values: JsonObject,
)

@Serializable
class TaskInfo(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
)

@Serializable
class ActionDetail(
    val tasks: List<TaskInfo>,
)