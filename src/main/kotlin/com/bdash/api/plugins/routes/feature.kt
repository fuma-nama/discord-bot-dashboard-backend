package com.bdash.api.plugins.routes

import com.bdash.api.Feature
import com.bdash.api.database.dao.FeatureDAO
import com.bdash.api.database.dao.FeatureDAO.setFeatureEnabled
import com.bdash.api.database.dao.FeatureDAO.updateFeatureOptions
import com.bdash.api.plugins.get
import com.bdash.api.utils.featureNotFound
import com.bdash.api.utils.toJsonObject
import com.bdash.api.utils.verify
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.features() = route("/feature/{id}") {
    get {
        val (guild, feature) = call["guild", "id"]

        verify(guild) {
            val detail = FeatureDAO.getFeature(guild.toLong(), feature)

            if (detail == null) {
                call.featureNotFound()
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