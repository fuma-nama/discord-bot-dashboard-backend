package com.bdash.api.plugins.routes

import com.bdash.api.UserPrincipal
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.plugins.GuildInfoImpl
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.JsonNull
import net.dv8tion.jda.api.JDA

fun Route.guildRoutes(bot: JDA) = route("/guilds/{guild}") {
    fun ApplicationCall.guild() = parameters["guild"]?.toLongOrNull()

    get {
        //check permissions (optional)
        val principal = call.principal<UserPrincipal>()!!

        val guild = call.guild()
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

        get {
            val feature = call.feature()
                ?: return@get call.respond(HttpStatusCode.BadRequest)
            val guild = call.guild()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val data = FeatureDAO.getFeature(guild, feature)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Feature Not Found")

            call.respond(data)
        }

        patch {
            val feature = call.feature()
                ?: return@patch call.respond(HttpStatusCode.BadRequest)
            val guild = call.guild()
                ?: return@patch call.respond(HttpStatusCode.BadRequest)

            val updated = FeatureDAO.updateFeatureOptions(guild, feature, call.receive())
                ?: return@patch call.respond(HttpStatusCode.NotFound, "Feature Not Found")

            call.respond(updated)
        }

        post {
            val guild = call.guild()
                ?: return@post call.respond(HttpStatusCode.BadRequest)
            val feature = call.feature()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            FeatureDAO.setFeatureEnabled(guild, feature, true)
            call.respond(HttpStatusCode.OK)
        }

        delete {
            val guild = call.guild()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val feature = call.feature()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            FeatureDAO.setFeatureEnabled(guild, feature, true)
            call.respond(HttpStatusCode.OK)
        }
    }
}