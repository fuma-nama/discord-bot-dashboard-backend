package com.bdash.api.database.utils

import com.bdash.api.database.utils.returning.UpdateReturningStatement
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.UpdateBuilder

abstract class Feature<E>(name: String = "") : OptionsContainer<E>(name) {
    abstract val id: String
}

abstract class Settings<E>(name: String? = null) : OptionsContainer<E>(name ?: "guild_settings") {

    abstract fun onInsert(statement: UpdateBuilder<*>, options: JsonElement)
}

abstract class OptionsContainer<E>(name: String = "") : Table(name) {
    /**
     * Linked guild, must be unique
     */
    val guild = long("guild")

    override val primaryKey = PrimaryKey(guild)

    abstract fun onUpdate(statement: UpdateReturningStatement, options: JsonElement)

    abstract fun options(self: ResultRow): E
}