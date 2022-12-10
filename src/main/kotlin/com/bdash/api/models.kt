package com.bdash.api

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.RoleIcon
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel

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
data class RoleObject(
    val id: String,
    val name: String,
    val color: Int,
    val position: Int,
    val icon: RoleIconObject?,
) {
    constructor(role: Role) : this(
        role.id,
        role.name,
        role.colorRaw,
        role.position,
        role.icon?.let { RoleIconObject(it) })
}

@Serializable
data class RoleIconObject(
    val iconUrl: String?,
    val emoji: String?,
) {
    constructor(icon: RoleIcon) : this(icon.iconUrl, icon.emoji)
}

@Serializable
data class ChannelObject(
    val id: String, val name: String, val type: Int, val category: String?,
) {
    constructor(channel: GuildChannel) : this(
        channel.id,
        channel.name,
        channel.type.id,
        if (channel is ICategorizableChannel) channel.parentCategoryId else null
    )
}

/**
 * Guild info, it is customizable
 *
 * You may add more fields to it
 */
interface GuildInfo {
    /**
     * The IDs of enabled features
     */
    val enabledFeatures: List<String>
}

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