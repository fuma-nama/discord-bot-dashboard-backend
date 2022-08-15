package com.bdash.api.database.utils

import com.bdash.api.database.utils.returning.UpdateReturningStatement
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

abstract class Feature(name: String = "") : OptionsContainer(name)

abstract class OptionsContainer(name: String = "") : Table(name) {
    abstract val id: String

    /**
     * Linked guild, must be unique
     */
    val guild = long("guild")

    override val primaryKey = PrimaryKey(guild)

    abstract fun onUpdate(statement: UpdateReturningStatement, options: JsonObject)

    abstract fun options(self: ResultRow): Map<String, JsonElement>
}

@Serializable
data class FeaturePayload(val values: JsonElement)