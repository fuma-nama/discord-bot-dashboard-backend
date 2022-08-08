package com.bdash.api.discord.models


@kotlinx.serialization.Serializable
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

    /**
     * If bot is exists in the server
     */
    var exist: Boolean = false
)