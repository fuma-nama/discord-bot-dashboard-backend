package net.sonmoosans.kbot.database.table.features

import com.bdash.api.database.setIf
import com.bdash.api.database.utils.Feature
import com.bdash.api.database.utils.returning.UpdateReturningStatement
import com.bdash.api.utils.long
import com.bdash.api.utils.string
import com.bdash.api.utils.toJsonElement
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.jetbrains.exposed.sql.ResultRow

object KillKane : Feature("feature_kill_kane") {
    override val id = "auto_kill_kane"

    /**
     * Message to send after killing Kane
     */
    val message = varchar("message", 1024).default("Kane is died!")

    /**
     * Listening channel
     */
    val channel = long("channel").nullable()

    override fun options(self: ResultRow): Map<String, JsonElement> {
        return mapOf(
            "message" to self[message].toJsonElement(),
            "channel" to self[channel].toJsonElement()
        )
    }

    override fun onUpdate(statement: UpdateReturningStatement, options: JsonObject) {
        statement.run {
            setIf(message, options["message"]) { string()!! }
            setIf(channel, options["channel"]) { long() }
        }
    }
}