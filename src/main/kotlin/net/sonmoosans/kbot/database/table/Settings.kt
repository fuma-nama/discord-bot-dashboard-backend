package net.sonmoosans.kbot.database.table

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.Settings
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.string
import com.bdash.api.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

object BotSettings : Settings() {
    val say = varchar("body", 1024).default("Hello World")

    override fun onInsert(statement: UpdateBuilder<*>, options: JsonObject) {
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