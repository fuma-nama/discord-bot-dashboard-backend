package com.bdash.api.models

@kotlinx.serialization.Serializable
data class User(
    /**
     * the user's id
     */
    val id: Long,
    /**
     * the user's username, not unique across the platform
     */
    val username: String,
    /**
     * the user's 4-digit discord-tag
     */
    val discriminator: String,
    /**
     * the user's avatar hash
     */
    val avatar: String?,
    /**
     * whether the user belongs to an OAuth2 application
     */
    val bot: Boolean = false,
    /**
     * whether the user is an Official Discord System user (part of the urgent message system)
     */
    val system: Boolean = false,
    /**
     * whether the user has two factor enabled on their account
     */
    val mfa_enabled: Boolean? = null,
    /**
     * the user's banner hash
     */
    val banner: String? = null,
    /**
     * the user's banner color encoded as an integer representation of hexadecimal color code
     */
    val accent_color: Int? = null,
    /**
     * the user's chosen language option
     */
    val locale: String? = null,
    /**
     * whether the email on this account has been verified
     */
    val verified: Boolean? = null,
    /**
     * the flags on a user's account
     */
    val flags: Int? = null,
    /**
     * the type of Nitro subscription on a user's account
     */
    val premium_type: Int? = null,
    /**
     * the public flags on a user's account
     */
    val public_flags: Int? = null
)