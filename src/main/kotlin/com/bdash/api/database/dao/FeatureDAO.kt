package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.utils.Feature
import com.bdash.api.database.utils.OptionsContainer
import com.bdash.api.database.utils.getOptions
import com.bdash.api.database.utils.updateOptions
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select

object FeatureDAO {
    private val features = hashMapOf<String, Feature<*>>()

    fun register(features: List<Feature<*>>) {
        for (feature in features) {
            this.features[feature.id] = feature
        }
    }

    suspend fun getFeature(guild: Long, featureId: String): Any? {

        return features[featureId]?.getOptions(guild)
    }

    suspend fun setFeatureEnabled(
        guild: Long,
        featureId: String,
        enabled: Boolean,
    ) {
        val feature = features[featureId]

        feature?.setFeatureEnabled(guild, enabled)
    }

    private suspend fun OptionsContainer<*>.setFeatureEnabled(guild: Long, enabled: Boolean) = dbQuery {
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
    suspend fun updateFeatureOptions(guild: Long, featureId: String, options: JsonElement): Any? {
        return features[featureId]?.updateOptions(guild, options)
    }
}