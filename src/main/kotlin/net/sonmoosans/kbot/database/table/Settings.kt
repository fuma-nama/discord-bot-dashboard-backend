package net.sonmoosans.kbot.database.table

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.Settings
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.string
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

@Serializable
data class SettingsData(
    val say: String,
)

object BotSettings : Settings<SettingsData>() {
    val say = varchar("body", 1024).default("Hello World")

    override fun onInsert(statement: UpdateBuilder<*>, options: JsonElement) {
        val obj = options.jsonObject

        with(statement) {
            setIf(say, obj["say"]) { string().orEmpty() }
        }
    }

    override fun onUpdate(statement: UpdateReturningStatement, options: JsonElement) {
        val obj = options.jsonObject

        with(statement) {
            setIf(say, obj["say"]) { string().orEmpty() }
        }
    }

    override fun options(self: ResultRow): SettingsData {
        return SettingsData(self[say])
    }
}