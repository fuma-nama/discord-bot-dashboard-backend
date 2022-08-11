package com.bdash.api.plugins

import com.bdash.api.UserSession
import com.bdash.api.discord.DiscordApi
import com.bdash.api.discord.Routes
import com.bdash.api.httpClient
import com.bdash.api.models.options.*
import com.bdash.api.utils.verify
import com.bdash.api.utils.withSession
import com.bdash.api.variable.clientUrl
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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

        get("/guild/{id}") {
            val id = call.parameters["id"]!!

            withSession { session ->
                val guild = DiscordApi.getGuild(session, id)

                if (guild == null) {
                    call.respondText("Guild doesn't Exists", status = HttpStatusCode.NotFound)
                } else {
                    call.respond(guild)
                }
            }
        }

        get("/guild/{guild}/features") {

            verify(call.parameters["guild"]!!) {
                val features = arrayOf(FeatureExample())

                call.respond(
                    Features(features, arrayOf(BetaFeature("AA", 3)))
                )
            }
        }

        get("/guild/{guild}/feature/{id}") {

            verify(call.parameters["guild"]!!) {

                call.respond(FeatureDetailExample(ExampleValues.value))
            }
        }

        patch("/guild/{guild}/feature/{id}") {

            verify(call.parameters["guild"]!!) {
                val value = call.receive<FeatureOptionExample>()
                ExampleValues.value = value

                println("save $value")
                call.respond(HttpStatusCode.OK)
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

@kotlinx.serialization.Serializable
class Features(
    val features: Array<FeatureExample>,
    val betaFeatures: Array<BetaFeature>
)

@kotlinx.serialization.Serializable
class BetaFeature(
    val name: String,
    val value: Int
)