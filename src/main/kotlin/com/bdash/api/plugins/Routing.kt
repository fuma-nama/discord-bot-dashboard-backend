package com.bdash.api.plugins

import com.bdash.api.UserSession
import com.bdash.api.bot.Info
import com.bdash.api.discord.Routes
import com.bdash.api.discord.models.Guild
import com.bdash.api.httpClient
import com.bdash.api.utils.withSession
import com.bdash.api.variable.clientUrl
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

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
                val guilds = httpClient.get(Routes.guilds) {
                    headers {
                        append(HttpHeaders.Authorization, "${session.token_type} ${session.token}")
                    }
                }.body<Array<Guild>>()

                val json = Json.encodeToString(
                    guilds.filter {
                        it.exist = Info.jda.getGuildById(it.id) != null

                        it.owner?: false
                    }
                )

                call.respondText(json)
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
            withSession {
                call.response.status(HttpStatusCode.OK)
            }
        }

        get("/auth/signout") {
            call.sessions.clear<UserSession>()

            call.response.status(HttpStatusCode.OK)
        }
    }
}