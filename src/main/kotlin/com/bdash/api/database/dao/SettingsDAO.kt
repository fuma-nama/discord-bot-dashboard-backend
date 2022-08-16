package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.table.Settings
import com.bdash.api.database.utils.OptionsContainer
import com.bdash.api.database.utils.OptionsContainerDAO
import com.bdash.api.database.utils.returning.updateReturning
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select

object SettingsDAO : OptionsContainerDAO {
    suspend fun getSettingOptions(guild: Long): Map<String, JsonElement>? {
        return Settings.getOptions(guild)
    }

    override suspend fun OptionsContainer.getOptions(guild: Long): Map<String, JsonElement>? {
        return getSettings(guild)?.let(Settings::options)
    }

    suspend fun getSettings(guild: Long): ResultRow? {
        val settings = dbQuery {
            Settings.select { Settings.guild eq guild }
                .singleOrNull()
        }

        return settings ?: initSettings(guild)
    }

    suspend fun initSettings(guild: Long, options: JsonObject? = null) = dbQuery {
        val insert = Settings.insertIgnore {
            it[this.guild] = guild

            if (options != null) {
                onInsert(it, options)
            }
        }
        insert.resultedValues?.singleOrNull()
    }

    suspend fun editSettings(guild: Long, options: JsonObject): Map<String, JsonElement>? {
        println(options)

        val update = dbQuery {
            Settings.updateReturning({ Settings.guild eq guild }) {
                it[Settings.guild] = guild

                onUpdate(it, options)
            }.singleOrNull()
        }

        return (update ?: initSettings(guild, options))
            ?.let(Settings::options)
    }
}