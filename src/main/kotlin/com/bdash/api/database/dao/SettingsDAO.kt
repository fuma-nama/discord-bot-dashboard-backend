package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.utils.Settings
import com.bdash.api.database.utils.returning.updateReturning
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.select

object SettingsDAO {
    private lateinit var table: Settings<*>

    fun register(settings: Settings<*>) {
        this.table = settings
    }

    suspend fun getOptions(guild: Long): Any? {
        return getSettings(guild).let(table::options)
    }

    suspend fun getSettings(guild: Long): ResultRow {
        val settings = dbQuery {
            table.select { table.guild eq guild }
                .singleOrNull()
        }

        return settings ?: initSettings(guild)
    }

    suspend fun initSettings(guild: Long, options: JsonObject? = null) = dbQuery {
        val insert = table.insertIgnore {
            it[this.guild] = guild

            if (options != null) {
                onInsert(it, options)
            }
        }
        insert.resultedValues!!.single()
    }

    suspend fun editSettings(guild: Long, options: JsonObject): Any? {

        val update = dbQuery {
            table.updateReturning({ table.guild eq guild }) {
                it[table.guild] = guild

                onUpdate(it, options)
            }.singleOrNull()
        }

        return (update ?: initSettings(guild, options)).let(table::options)
    }
}