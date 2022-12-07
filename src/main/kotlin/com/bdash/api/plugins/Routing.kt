package com.bdash.api.plugins

import com.bdash.api.GuildInfo
import com.bdash.api.OAuthBuilder
import com.bdash.api.UserPrincipal
import com.bdash.api.UserSession
import com.bdash.api.discord.DiscordApi
import com.bdash.api.plugins.routes.guildRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.JDA

@Serializable
data class GuildInfoImpl(
    val members: Int,
    val status: String,
    override val enabledFeatures: List<String>,
) : GuildInfo

fun Application.configureRouting(oauth: OAuthBuilder, bot: JDA) {

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

                call.respondRedirect(oauth.redirect)
            }
        }

        authenticate("app") {
            guildRoutes(bot)

            //Get discord oauth2 access token if logged in, otherwise respond 401
            get("/auth") {
                val principal = call.principal<UserPrincipal>()!!

                if (DiscordApi.checkToken(principal)) {
                    call.respond(HttpStatusCode.OK, principal.token)
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }

            post("/auth/signout") {
                call.sessions.clear<UserSession>()

                call.response.status(HttpStatusCode.OK)
            }
        }
    }
}

operator fun ApplicationCall.get(vararg names: String): List<String> {
    return names.map {
        parameters[it]!!
    }
}