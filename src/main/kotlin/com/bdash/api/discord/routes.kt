package com.bdash.api.discord

object Routes {
    private val url = "https://discord.com/api/v10"

    val user = "$url/users/@me"
    val guilds = "$url/users/@me/guilds"
    val verify = "$url/oauth2/@me"
}
