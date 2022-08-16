package com.bdash.api.database.table

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.OptionsContainer
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.string
import com.bdash.api.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object Settings : OptionsContainer("guild_settings") {
    override val id = "settings"

    val say = varchar("body", 1024).default("Hello World")

    fun onInsert(statement: UpdateBuilder<*>, options: JsonObject) {
        with(statement) {
            setIf(say, options["say"]) { string().orEmpty() }
        }
    }

    override fun onUpdate(statement: UpdateReturningStatement, options: JsonObject) {
        with(statement) {
            setIf(say, options["say"]) { string().orEmpty() }
        }
    }

    override fun options(self: ResultRow): Map<String, JsonElement> {
        return mapOf(
            "say" to self[say].toJsonElement()
        )
    }
}