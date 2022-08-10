package com.bdash.api.utils

import com.bdash.api.UserSession
import com.bdash.api.discord.DiscordApi
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