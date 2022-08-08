package com.bdash.api.database.dao

import com.bdash.api.database.DatabaseFactory.dbQuery
import com.bdash.api.database.models.Setting
import com.bdash.api.database.models.Settings
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

object SettingsDAO {
    suspend fun get(id: Long): Setting? = dbQuery {
        Settings.select { Settings.id eq id }
            .map(::toModel)
            .singleOrNull()
    }

    suspend fun addSettings(id: Long, say: String): Setting? = dbQuery {
        val insertStatement = Settings.insert {
            it[this.id] = id
        }

        insertStatement.resultedValues?.singleOrNull()?.let(::toModel)
    }

    suspend fun editSettings(id: Long, say: String): Boolean = dbQuery {
        Settings.update({ Settings.id eq id }) {
            it[Settings.say] = say
        } > 0
    }

    fun toModel(result: ResultRow): Setting {
        return Setting(result[Settings.id], result[Settings.say])
    }
}