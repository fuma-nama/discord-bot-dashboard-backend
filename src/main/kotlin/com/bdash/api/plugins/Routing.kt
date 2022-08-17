package com.bdash.api.plugins

import com.bdash.api.*
import com.bdash.api.bot.Info
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.database.dao.SettingsDAO
import com.bdash.api.discord.DiscordApi
import com.bdash.api.discord.Routes
import com.bdash.api.plugins.routes.actions
import com.bdash.api.plugins.routes.features
import com.bdash.api.utils.*
import com.bdash.api.variable.clientUrl
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

fun Application.configureRouting() {

    routing {
        authenticate("discord-oauth2") {
            get("/login") {
                // Redirects to 'authorizeUrl' automatically
            }

            get("/callback") {
                val principal = call.principal<OAuthAccessTokenResponse.OAuth2>()

                if (principal != null) {
                    call.sessions.set(UserSession(principal.accessToken, principal.tokenType))
                }

                call.respondRedirect(clientUrl)
            }
        }

        get("/guilds") {
            withSession { session ->
                val guilds = DiscordApi.getGuildsExists(session)

                call.respond(guilds)
            }
        }

        get("/users/@me") {
            withSession { session ->
                val user = httpClient.get(Routes.user) {
                    headers {
                        append(HttpHeaders.Authorization, "${session.token_type} ${session.token}")
                    }
                }.bodyAsText()

                call.respondText(user)
            }
        }

        head("/auth") {
            verify {
                call.response.status(HttpStatusCode.OK)
            }
        }

        post("/auth/signout") {
            call.sessions.clear<UserSession>()

            call.response.status(HttpStatusCode.OK)
        }

        guild()
    }
}

fun Route.guild() = route("/guild/{guild}") {
    get {
        val id = call.parameters["guild"]!!

        withSession { session ->
            val guild = DiscordApi.getGuild(session, id)

            if (guild == null) {
                call.guildNotFound()
            } else {
                call.respond(guild)
            }
        }
    }

    get("/features") {
        val guild = call.parameters["guild"]!!

        verify(guild) {
            val features = Features(
                FeatureDAO.getEnabledFeatures(guild.toLong()).toTypedArray()
            )

            call.respond(features)
        }
    }

    get("/detail") {
        val guildId = call.getGuild()

        verify(guildId) {
            val guild = Info.jda.getGuildById(guildId)

            if (guild == null) {
                call.guildNotFound()
            } else {
                call.respond(ServerDetails(guild.memberCount))
            }
        }
    }

    get("/detail/advanced") {
        val guildId = call.getGuild()

        verify(guildId) {
            val guild = Info.jda.getGuildById(guildId)

            if (guild == null) {
                call.guildNotFound()
            } else {
                call.respond(ServerDetailsAdvanced(guild.memberCount))
            }
        }
    }

    get("/notification") {
        val guildId = call.getGuild()

        verify(guildId) {
            val guild = Info.jda.getGuildById(guildId)!!

            val notifications = arrayOf(
                Notification(
                    "Good Job!",
                    "Your server just got ${guild.memberCount} members",
                    "https://avatars.githubusercontent.com/u/88699887?s=200&v=4"
                ),
                Notification("Good Job!", "Your server just got ${guild.memberCount} members")
            )

            call.respond(notifications)
        }
    }

    get("/settings") {
        val guild = call.parameters["guild"]!!

        verify(guild) {
            val options = SettingsDAO.getSettingOptions(guild.toLong())

            if (options == null) {
                call.guildNotFound()
            } else {
                val settings = Settings(
                    options.toJsonObject()
                )

                call.respond(settings)
            }
        }
    }

    patch<JsonObject>("/settings") { options ->
        val guild = call.parameters["guild"]!!

        verify(guild) {

            val updated = SettingsDAO.editSettings(guild.toLong(), options)

            if (updated == null) {
                call.guildNotFound()
            } else {
                call.respond(updated.toJsonObject())
            }
        }
    }

    features()
    actions()
}

operator fun ApplicationCall.get(vararg names: String): List<String> {
    return names.map {
        parameters[it]!!
    }
}

@Serializable
class ServerDetails(
    val members: Int,
)

@Serializable
class ServerDetailsAdvanced(
    val members: Int,
)