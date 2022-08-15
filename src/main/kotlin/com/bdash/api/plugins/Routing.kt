package com.bdash.api.plugins

import com.bdash.api.UserSession
import com.bdash.api.database.dao.ActionDAO
import com.bdash.api.database.dao.ActionDAO.actionNotFound
import com.bdash.api.database.dao.ActionDAO.addTask
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.database.dao.FeatureDAO.setFeatureEnabled
import com.bdash.api.database.dao.FeatureDAO.updateFeatureOptions
import com.bdash.api.database.utils.TaskBody
import com.bdash.api.discord.DiscordApi
import com.bdash.api.discord.Routes
import com.bdash.api.httpClient
import com.bdash.api.utils.toJsonObject
import com.bdash.api.utils.verify
import com.bdash.api.utils.withSession
import com.bdash.api.variable.clientUrl
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

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
                val guilds = DiscordApi.getGuildsExists(session)

                call.respond(guilds)
            }
        }

        guild()

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

fun Route.actions() = route("/action/{action}") {
    get {
        val (guild, action) = call["guild", "action"]

        verify(guild) {
            val detail = ActionDAO.getActionDetail(guild.toLong(), action)

            if (detail == null) {
                call.actionNotFound()
            } else {
                call.respond(detail)
            }
        }
    }

    post {
        val (guild, action) = call["guild", "action"]
        val body = call.receive<TaskBody>()

        verify(guild) {
            addTask(guild.toLong(), action, body.name, body.options)
        }
    }

    route("/{task}") {
        get {
            val (guild, action, task) = call["guild", "action", "task"]

            verify(guild) {
                val detail = ActionDAO.getTaskDetail(guild.toLong(), action, task.toInt())

                if (detail == null) {
                    call.actionNotFound()
                } else {
                    call.respond(detail)
                }
            }
        }

        patch {
            val (guild, action, task) = call["guild", "action", "task"]
            val payload = call.receive<TaskBody>()

            verify(guild) {
                val result = ActionDAO.updateTask(
                    guild.toLong(), action, task.toInt(), payload
                )

                if (result == null) {
                    call.actionNotFound()
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }

        delete {
            val (guild, action, task) = call["guild", "action", "task"]

            verify(guild) {
                val result = ActionDAO.deleteTask(
                    guild.toLong(), action, task.toInt()
                )

                if (result == null) {
                    call.actionNotFound()
                } else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}

fun Route.guild() = route("/guild/{guild}") {
    get {
        val id = call.parameters["guild"]!!

        withSession { session ->
            val guild = DiscordApi.getGuild(session, id)

            if (guild == null) {
                call.respondText("Guild doesn't Exists", status = HttpStatusCode.NotFound)
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

    features()
    actions()
}

fun Route.features() = route("/feature/{id}") {
    get {
        val (guild, feature) = call["guild", "id"]

        verify(guild) {
            val detail = FeatureDAO.getFeature(guild.toLong(), feature)

            if (detail == null) {
                call.respondText(
                    "Feature Id doesn't exists",
                    status = HttpStatusCode.NotFound
                )
            } else {
                call.respond(
                    Feature(detail.toJsonObject())
                )
            }
        }
    }

    patch {
        val (guild, feature) = call["guild", "id"]

        verify(guild) {
            updateFeatureOptions(guild.toLong(), feature)
        }
    }

    patch("/enabled") {
        val (guild, feature) = call["guild", "id"]
        val enabled = call.receive<Boolean>()

        verify(guild) {
            setFeatureEnabled(guild.toLong(), feature, enabled)
        }
    }
}

operator fun ApplicationCall.get(vararg names: String): List<String> {
    return names.map {
        parameters[it]!!
    }
}

@Serializable
class Feature(val values: JsonObject)

@Serializable
class Features(val enabled: Array<String>)