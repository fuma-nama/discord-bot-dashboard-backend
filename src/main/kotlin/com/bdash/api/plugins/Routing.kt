package com.bdash.api.plugins

import com.bdash.api.UserSession
import com.bdash.api.discord.Routes
import com.bdash.api.httpClient
import com.bdash.api.utils.withSession
import com.bdash.api.variable.clientUrl
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*


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
                }.bodyAsText()

                call.respondText(guilds)
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