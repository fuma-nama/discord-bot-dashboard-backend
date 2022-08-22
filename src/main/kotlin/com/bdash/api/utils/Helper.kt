package com.bdash.api.utils

import com.bdash.api.UserSession
import com.bdash.api.discord.DiscordApi
import com.bdash.api.discord.Error
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*
import net.dv8tion.jda.api.Permission

suspend inline fun PipelineContext<Unit, ApplicationCall>.withSession(receiver: (UserSession) -> Unit) {
    val session = call.sessions.get<UserSession>()

    if (session != null) {
        receiver(session)
    } else {
        call.respond(HttpStatusCode.Unauthorized, "Not Authorized")
    }
}

fun ApplicationCall.getGuild(): String {
    return parameters["guild"]!!
}

suspend fun ApplicationCall.guildNotFound() {
    respond(HttpStatusCode.NotFound, "Guild Id doesn't exists")
}

suspend fun ApplicationCall.featureNotFound() {
    respond(HttpStatusCode.NotFound, "Feature Id doesn't exists")
}

suspend fun ApplicationCall.actionNotFound() {
    respond(HttpStatusCode.NotFound, "Action Id doesn't exists")
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.verify(onSuccess: () -> Unit) = withSession {
    if (DiscordApi.checkToken(it)) {
        onSuccess()
    } else {
        call.respond(HttpStatusCode.Unauthorized, "Not authorized")
    }
}

suspend inline fun PipelineContext<Unit, ApplicationCall>.verify(guildId: String, onSuccess: () -> Unit) = withSession {
    val guild = DiscordApi.getGuild(it, guildId) ?: return@withSession

    val permissions = Permission.getPermissions(
        guild.permissions!!.toLong()
    )

    val isAdmin = permissions.contains(Permission.ADMINISTRATOR)

    if (isAdmin) {
        onSuccess()
    } else {
        call.respond(HttpStatusCode.Unauthorized, "Missing Required Permission")
    }
}

suspend fun HttpResponse.toError() = APIException(status, body<Error>().message)

fun apiError(code: HttpStatusCode, message: String): Nothing = throw APIException(code, message)

class APIException(val code: HttpStatusCode, message: String) : Exception(message)