package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.utils.Feature
import com.bdash.api.database.utils.OptionsContainer
import com.bdash.api.database.utils.OptionsContainerDAO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select

object FeatureDAO : OptionsContainerDAO {
    val features = hashMapOf<String, Feature>()

    fun register(features: List<Feature>) {
        for (feature in features) {
            this.features[feature.id] = feature
        }
    }

    suspend fun getFeature(guild: Long, featureId: String): Map<String, JsonElement>? {
        return features[featureId]?.getOptions(guild)
    }

    suspend fun PipelineContext<*, ApplicationCall>.setFeatureEnabled(
        guild: Long,
        featureId: String,
        enabled: Boolean,
    ) {

        val feature = features[featureId]
            ?: return call.respondText("Request Id doesn't exists", status = HttpStatusCode.NotFound)

        feature.setFeatureEnabled(guild, enabled)
        call.respond(HttpStatusCode.OK)
    }

    private suspend fun OptionsContainer.setFeatureEnabled(guild: Long, enabled: Boolean) = dbQuery {
        val feature = this

        if (enabled) {
            feature.insertIgnore {
                it[feature.guild] = guild
            }
        } else {
            feature.deleteWhere {
                feature.guild eq guild
            }
        }
    }

    suspend fun getEnabledFeatures(guild: Long): List<String> = dbQuery {
        val enabled = arrayListOf<String>()

        for (feature in features.values) {
            val disabled = feature.select { feature.guild eq guild }.empty()

            if (!disabled) {
                enabled += feature.id
            }
        }

        enabled
    }

    /**
     * @return Updated Option Values
     */
    suspend fun PipelineContext<*, ApplicationCall>.updateFeatureOptions(guild: Long, featureId: String) {
        val feature = features[featureId]

        if (feature == null) {
            call.respondText("Feature Id doesn't exists", status = HttpStatusCode.NotFound)
            return
        }

        updateOptions(guild, feature)
    }
}