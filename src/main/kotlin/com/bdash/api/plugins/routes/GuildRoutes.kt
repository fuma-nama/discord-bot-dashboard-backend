package com.bdash.api.plugins.routes

import com.bdash.api.UserPrincipal
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.plugins.GuildInfoImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonNull
import net.dv8tion.jda.api.JDA

fun Route.guildRoutes(bot: JDA) = route("/guilds/{guild}") {
    fun ApplicationCall.guild() = parameters["guild"]

    get {
        //check permissions (optional)
        val principal = call.principal<UserPrincipal>()!!

        val guild = call.guild()?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest)
        val data = bot.getGuildById(guild)
            ?: return@get call.respond(HttpStatusCode.BadRequest, message = JsonNull)

        call.respond(
            GuildInfoImpl(
                members = data.memberCount,
                enabledFeatures = FeatureDAO.getEnabledFeatures(guild),
                status = "Nice"
            )
        )
    }

    route("/features/{feature}") {
        fun ApplicationCall.feature() = parameters["feature"]

        post {
            val guild = call.guild()?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)
            val feature = call.feature()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            FeatureDAO.setFeatureEnabled(guild, feature, true)
            call.respond(HttpStatusCode.OK)
        }

        delete {
            val guild = call.guild()?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val feature = call.feature()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            FeatureDAO.setFeatureEnabled(guild, feature, true)
            call.respond(HttpStatusCode.OK)
        }
    }
}