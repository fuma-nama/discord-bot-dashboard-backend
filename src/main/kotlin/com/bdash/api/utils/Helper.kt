package com.bdash.api.utils

import com.bdash.api.UserSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.pipeline.*

suspend inline fun PipelineContext<Unit, ApplicationCall>.withSession(receiver: (UserSession) -> Unit) {
    val session = call.sessions.get<UserSession>()

    if (session != null) {
        receiver(session)
    } else {
        call.respond(HttpStatusCode.Unauthorized, "Not Authorized")
    }
}