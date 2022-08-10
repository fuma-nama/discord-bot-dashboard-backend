package com.bdash.api.discord

import com.bdash.api.UserSession
import com.bdash.api.bot.Info
import com.bdash.api.discord.models.Guild
import com.bdash.api.discord.models.GuildExists
import com.bdash.api.httpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

object DiscordApi {
    suspend fun getGuild(user: UserSession, id: String): Guild? {
        return getGuilds(user).find {it.id == id}
    }

    suspend fun getGuilds(user: UserSession): Array<Guild> {
        println("fetching user guilds from discord api")
        return httpClient.get(Routes.guilds) {
            headers {
                append(HttpHeaders.Authorization, "${user.token_type} ${user.token}")
            }
        }.body()
    }

    suspend fun getGuildsExists(user: UserSession): List<GuildExists> {
        val guilds = httpClient.get(Routes.guilds) {
            headers {
                append(HttpHeaders.Authorization, "${user.token_type} ${user.token}")
            }
        }.body<Array<GuildExists>>()

        return guilds.filter {
            it.exist = Info.jda.getGuildById(it.id) != null

            it.owner?: false
        }
    }
}