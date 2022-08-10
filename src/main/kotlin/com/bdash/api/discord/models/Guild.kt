package com.bdash.api.discord.models

import kotlinx.serialization.Serializable


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
    var exist: Boolean = false
)

@Serializable
data class EmojiObject(
    /**
     * emoji id
     **/
    val id : Long?,
    val name: String?,
    /**
     * whether this emoji must be wrapped in colons
     **/
    val require_colons: Boolean? = null,
    /**
     * whether this emoji is managed
     **/
    val managed: Boolean? = null,
    /**
     * whether this emoji is animated
     **/
    val animated: Boolean? = null,
    /**
     * whether this emoji can be used, may be false due to loss of Server Boosts
     **/
    val available: Boolean? = null,
)

@Serializable
data class StickerObject(
    /**
     * id of the sticker
     **/
    val id: Long,
    /**
     * for standard stickers, id of the pack the sticker is from
     **/
    val pack_id: Long? = null,
    /**
     * name of the sticker
     **/
    val name: String,
    /**
     * description of the sticker
     **/
    val description: String?,
    /**
     * Deprecated previously the sticker asset hash, now an empty string
     **/
    val asset: String? = null,
    /**
     * type of sticker
     **/
    val type: Int,
    /**
     * type of sticker format
     **/
    val format_type: Int,
    /**
     * whether this guild sticker can be used, may be false due to loss of Server Boosts
     **/
    val available: Boolean? = null,
    /**
     * id of the guild that owns this sticker
     **/
    val guild_id: Long? = null,
    /**
     * the standard sticker's sort order within its pack
     **/
    val sort_value: Int? = null,
)
