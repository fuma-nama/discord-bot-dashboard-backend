package com.bdash.api.database.utils

import com.bdash.api.database.utils.returning.UpdateReturningStatement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

abstract class Feature(name: String = "") : OptionsContainer(name) {
    abstract val id: String
}

abstract class Settings(name: String? = null) : OptionsContainer(name ?: "guild_settings") {

    abstract fun onInsert(statement: UpdateBuilder<*>, options: JsonObject)
}

abstract class OptionsContainer(name: String = "") : Table(name) {
    /**
     * Linked guild, must be unique
     */
    val guild = long("guild")

    override val primaryKey = PrimaryKey(guild)

    abstract fun onUpdate(statement: UpdateReturningStatement, options: JsonObject)

    abstract fun options(self: ResultRow): Map<String, JsonElement>
}